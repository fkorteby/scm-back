package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawConsultation;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawPatient;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawPresc;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.*;

/**
 * Reader pour les fichiers MDB v1.7.
 *
 * CORRECTION IMPORTANTE (suite à un bug constaté en prod) : la version
 * précédente faisait un merge-join en flux sur 3 curseurs triés (mal / cons /
 * presc), en supposant que "ORDER BY num_mal" produit EXACTEMENT le même
 * ordre relatif dans "mal" et dans "cons". Sur cette base Access (dont on
 * sait déjà que les métadonnées sont partiellement corrompues), cette
 * hypothèse s'est révélée fausse : le tri n'est pas fiable/stable de la même
 * façon sur les deux tables, donc le merge-join "loupait" silencieusement
 * quasiment tous les rattachements consultation/traitement, sans lever la
 * moindre erreur (d'où l'import "rapide" mais avec 0 consultation).
 *
 * RETOUR À UNE APPROCHE PLUS ROBUSTE : préchargement en Map, groupé par clé
 * (num_mal / num_cons), fait UNE SEULE FOIS à l'ouverture du reader. Ça reste
 * 2 scans complets de "cons"/"presc" (pas de N+1), mais ça ne dépend plus
 * d'aucune hypothèse sur l'ordre de tri — un HashMap regroupe correctement
 * peu importe l'ordre dans lequel les lignes arrivent.
 *
 * Le reader continue de streamer un patient à la fois via mal (read()
 * appelé par Spring Batch), donc la mémoire du Job reste dominée par la
 * taille de cons+presc préchargés (raw DTOs légers), pas par les entités JPA.
 */
public class MdbPatientItemReader implements ItemStreamReader<PatientImportUnit> {

    private final String filePath;
    private final String password;
    private final Long clientId;
    private final ClientRepository clientRepository;
    private final MedicamentRepository medicamentRepository;
    private final ImportRowPersister rowPersister;

    private Connection conn;
    private Statement stmtPatients;

    private RowCursor<RawPatient> patientCursor;
    private Map<Long, List<RawConsultation>> consultationsByPatient;
    private Map<Long, List<RawPresc>> prescByConsultation;

    private Client client;
    private Map<String, Medicament> medicamentCache;

    private static final Pattern EXTRACT_CROCHET =
            Pattern.compile("^(.+?)\\s*\\[([^\\]]+)\\]\\s*$");
    private static final Pattern EXTRACT_PARENTHESE =
            Pattern.compile("^(.+?)\\s*\\(([^)]*(?:\\([^)]*\\)[^)]*)?)\\)\\s*$");

    public MdbPatientItemReader(String filePath, String password, Long clientId,
                                ClientRepository clientRepository,
                                MedicamentRepository medicamentRepository,
                                ImportRowPersister rowPersister) {
        this.filePath = filePath;
        this.password = password;
        this.clientId = clientId;
        this.clientRepository = clientRepository;
        this.medicamentRepository = medicamentRepository;
        this.rowPersister = rowPersister;
    }

    // ─── Cycle de vie Spring Batch ──────────────────────────────────────────

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            conn = DriverManager.getConnection(buildJdbcUrl());

            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalStateException("Client non trouvé : id=" + clientId));

            medicamentCache = new HashMap<>();
            for (Medicament m : medicamentRepository.findMedicamentsByClientCreatorId(1L)) {
                medicamentCache.put(cacheKey(m.getNomCommerciale(), m.getDosage()), m);
            }

            boolean hasPrescTable = tableExists("presc");

            // ── Préchargement (2 scans complets, une seule fois) ─────────────
            consultationsByPatient = preloadConsultations();
            prescByConsultation = hasPrescTable ? preloadPresc() : Collections.emptyMap();

            // ── Curseur patient (streaming, celui-ci reste en flux) ─────────
            stmtPatients = conn.createStatement();
            ResultSet rsPatients = stmtPatients.executeQuery("SELECT * FROM mal");
            patientCursor = new RowCursor<>(rsPatients, this::mapPatient);

        } catch (SQLException e) {
            throw new ItemStreamException("Impossible d'ouvrir le fichier MDB v1.7 : " + e.getMessage(), e);
        }
    }

    private Map<Long, List<RawConsultation>> preloadConsultations() throws SQLException {
        Map<Long, List<RawConsultation>> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cons")) {

            boolean hasConduite = hasColumn(rs, "conduite_cons");

            while (rs.next()) {
                RawConsultation rc = new RawConsultation(
                        rs.getLong("num_cons"), rs.getLong("num_mal"), rs.getDate("date_cons"),
                        rs.getString("motif_cons"), rs.getString("diag_cons"),
                        rs.getString("rslt_examen_cons"), rs.getString("rslt_para_cons"),
                        hasConduite ? rs.getString("conduite_cons") : null,
                        rs.getString("trait_cons"), rs.getString("trait2_cons")
                );
                map.computeIfAbsent(rc.numMal(), k -> new ArrayList<>()).add(rc);
            }
        }
        return map;
    }

    private Map<Long, List<RawPresc>> preloadPresc() throws SQLException {
        Map<Long, List<RawPresc>> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM presc")) {

            while (rs.next()) {
                RawPresc rp = new RawPresc(
                        rs.getLong("num_cons"), 0L, rs.getString("medic_presc"),
                        rs.getString("form_presc"), rs.getString("poso_presc"), rs.getString("duree_presc")
                );
                map.computeIfAbsent(rp.numCons(), k -> new ArrayList<>()).add(rp);
            }
        }
        return map;
    }

    @Override
    public PatientImportUnit read() throws Exception {
        if (patientCursor.isExhausted()) {
            return null; // fin du flux -> Spring Batch arrête le step proprement
        }

        RawPatient rp = patientCursor.consume();
        Patient patient = buildPatient(rp);

        List<ConsultationUnit> units = new ArrayList<>();
        List<RawConsultation> rawConsultations = consultationsByPatient.getOrDefault(rp.numMal(), Collections.emptyList());

        for (RawConsultation rc : rawConsultations) {
            List<Traitement> traitements = new ArrayList<>();
            List<RawPresc> prescRows = prescByConsultation.get(rc.numCons());

            if (prescRows != null && !prescRows.isEmpty()) {
                for (RawPresc rpx : prescRows) {
                    String medicBrut = clean(rpx.medic());
                    if (medicBrut == null || medicBrut.isBlank()) continue;

                    Medicament med = getOrCreateMedicamentDirect(
                            extractNom(medicBrut), extractDosage(medicBrut), rpx.forme(), rpx.poso());
                    traitements.add(buildTraitement(med, rpx.duree(), rpx.poso()));
                }
            } else {
                String raw = (rc.traitCons() != null ? rc.traitCons() : "")
                        + (rc.trait2Cons() != null ? " " + rc.trait2Cons() : "");
                for (TraitementParser.ParsedTraitement pt : TraitementParser.parse(raw)) {
                    Medicament med = getOrCreateMedicament(pt);
                    traitements.add(buildTraitement(med, pt.duree, pt.posologie));
                }
            }

            units.add(new ConsultationUnit(buildConsultation(rc), traitements));
        }

        return new PatientImportUnit(rp.numMal(), patient, units);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // Pas d'état de restart persistable pour ce reader.
    }

    @Override
    public void close() throws ItemStreamException {
        closeQuietly(stmtPatients);
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }

    // ─── Mapping ResultSet -> DTO brut ──────────────────────────────────────

    private RawPatient mapPatient(ResultSet rs) throws SQLException {
        return new RawPatient(
                rs.getLong("num_mal"), rs.getString("nom_mal"), rs.getString("prenom_mal"),
                rs.getDate("ddn_mal"), rs.getString("adr_mal"), rs.getString("tel_mal"),
                rs.getString("sexe_mal"), rs.getString("sit_fam_mal"), rs.getString("profession_mal"),
                rs.getString("assurance_mal"), rs.getString("ant_med_mal"), rs.getString("ant_chir_mal"),
                rs.getString("ant_fam_mal"), hasColumn(rs, "autres_mal") ? rs.getString("autres_mal") : null
        );
    }

    // ─── Construction des entités JPA (pas encore persistées) ──────────────

    private Patient buildPatient(RawPatient rp) {
        Patient p = new Patient();
        p.setNom(clean(rp.nom()));
        p.setPrenom(clean(rp.prenom()));
        if (rp.ddn() != null) p.setDateNaissance(rp.ddn().toLocalDate());
        p.setAdresse(clean(rp.adresse()));
        p.setNumeroTel(normalizePhone(rp.tel()));
        p.setSexe(normalizeSexe(clean(rp.sexe())));
        p.setSituation(clean(rp.situation()));
        p.setProfession(clean(rp.profession()));
        p.setAssurance(rp.assurance() != null && rp.assurance().equalsIgnoreCase("Oui"));
        p.setAntecedentsPersonnelsMedicaux(rp.antMed());
        p.setAntecedentsPersonnelsChirugicaux(rp.antChir());
        p.setAntecedentsFamiliaux(rp.antFam());
        p.setAutres(rp.autres());
        p.setClientCreatorId(clientId);
        p.setClient(client);
        return p;
    }

    private Consultation buildConsultation(RawConsultation rc) {
        Consultation c = new Consultation();
        c.setDateConsultation(rc.dateCons() != null ? rc.dateCons().toLocalDate() : LocalDate.now());
        c.setMotifConsultation(firstNonBlank(rc.motif(), "-"));
        c.setDiagnosticMedical(firstNonBlank(rc.diag(), "-"));
        c.setResultatExamenClinique(rc.rsltExamen());
        c.setResultatExamenParacliniques(rc.rsltPara());
        c.setCatEvolution(rc.conduite());
        c.setClientCreatorId(clientId);
        c.setStatusConsultation(EStatusConsultation.TERMINEE);
        return c;
    }

    private Traitement buildTraitement(Medicament m, String duree, String poso) {
        Traitement t = new Traitement();
        t.setMedicament(m);
        t.setDuree(duree);
        t.setPosologie(poso != null && !poso.isBlank() ? poso : "Non précisée");
        t.setClientCreatorId(clientId);
        return t;
    }

    // ─── Médicaments ─────────────────────────────────────────────────────

    private Medicament getOrCreateMedicamentDirect(String nom, String dosage, String forme, String poso) {
        String key = cacheKey(nom, dosage);
        Medicament cached = medicamentCache.get(key);
        if (cached != null) return cached;

        Medicament med = new Medicament();
        med.setNomCommerciale(nom);
        med.setDosage(dosage);
        med.setForme(forme);
        med.setPosologie(poso);
        med.setClientCreatorId(1L);

        med = rowPersister.saveMedicament(med);
        medicamentCache.put(key, med);
        return med;
    }

    private Medicament getOrCreateMedicament(TraitementParser.ParsedTraitement pt) {
        String key = cacheKey(pt.nomCommerciale, pt.dosage);
        Medicament cached = medicamentCache.get(key);
        if (cached != null) return cached;

        Medicament med = new Medicament();
        med.setNomCommerciale(pt.nomCommerciale);
        med.setDosage(pt.dosage);
        med.setForme(pt.forme);
        med.setDuree(pt.duree);
        med.setPosologie(pt.posologie);
        med.setClientCreatorId(1L);

        med = rowPersister.saveMedicament(med);
        medicamentCache.put(key, med);
        return med;
    }

    private String cacheKey(String nom, String dosage) {
        return (nom == null ? "" : nom.toUpperCase()) + "_" + (dosage == null ? "" : dosage.toUpperCase());
    }

    // ─── Extraction nom / dosage ────────────────────────────────────────────

    private String extractNom(String medic) {
        if (medic == null) return null;
        Matcher m = EXTRACT_CROCHET.matcher(medic.trim());
        if (m.matches()) return m.group(1).trim();
        m = EXTRACT_PARENTHESE.matcher(medic.trim());
        if (m.matches()) return m.group(1).trim();
        return medic.trim();
    }

    private String extractDosage(String medic) {
        if (medic == null) return null;
        Matcher m = EXTRACT_CROCHET.matcher(medic.trim());
        if (m.matches()) return m.group(2).trim();
        m = EXTRACT_PARENTHESE.matcher(medic.trim());
        if (m.matches()) return m.group(2).trim();
        return null;
    }

    // ─── Utilitaires ────────────────────────────────────────────────────────

    private boolean tableExists(String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private String buildJdbcUrl() {
        StringBuilder sb = new StringBuilder("jdbc:ucanaccess://").append(filePath);
        if (password != null && !password.isBlank()) sb.append(";password=").append(password);
        sb.append(";ignoreCase=true;memory=true;skipIndexes=true;immediatelyReleaseResources=true");
        return sb.toString();
    }

    private String clean(String v) { return v == null ? null : v.trim(); }

    private String firstNonBlank(String... vs) {
        for (String v : vs) if (v != null && !v.isBlank()) return v.trim();
        return "";
    }

    private String normalizeSexe(String sexe) {
        if (sexe == null) return null;
        String s = sexe.trim().toLowerCase();
        if (s.contains("f")) return "Féminin";
        if (s.contains("m")) return "Masculin";
        return sexe.trim();
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("\\D", "");
    }

    private void closeQuietly(Statement s) {
        try {
            if (s != null) s.close();
        } catch (SQLException ignored) {
        }
    }
}