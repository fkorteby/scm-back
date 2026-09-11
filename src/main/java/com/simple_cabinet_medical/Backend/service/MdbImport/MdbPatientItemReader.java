package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawConsultation;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawPatient;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawPresc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * CORRECTION 1 (colonnes optionnelles) : certains fichiers .mdb de cette
 * "version 1.7" n'ont en réalité PAS toutes les colonnes attendues (ex:
 * trait2_cons absente sur certains fichiers clients). Avant, seules
 * conduite_cons et autres_mal étaient protégées par hasColumn(...) ; le reste
 * plantait le Job entier avec une HsqlException "Column not found" dès la
 * première ligne. Maintenant TOUTES les colonnes optionnelles sont谁 vérifiées
 * une seule fois à l'ouverture (pas à chaque ligne, pour la performance), et
 * une colonne absente => on continue simplement sans cette donnée, jamais de
 * crash du Job pour ça.
 * crash du Job pour ça.
 *
 * CORRECTION 2 (priorité des traitements) : avant, on regardait D'ABORD la
 * table presc, et seulement si elle ne contenait rien pour cette consultation
 * on repliait sur trait_cons/trait2_cons. Maintenant c'est l'inverse, comme
 * demandé : on essaie D'ABORD de parser trait_cons/trait2_cons ; si ça donne
 * au moins un traitement, on s'arrête là (pas besoin d'aller chercher dans
 * presc) ; seulement si trait_cons est vide/ne donne rien, on va chercher
 * dans presc.
 */
public class MdbPatientItemReader implements ItemStreamReader<PatientImportUnit> {

    private static final Logger log = LoggerFactory.getLogger(MdbPatientItemReader.class);
    private static final int MAX_UNPARSED_LOGS = 30;
    private int unparsedLogCount = 0;

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

    // Colonnes détectées une seule fois à l'ouverture (voir open())
    private boolean hasAutresMal;
    private boolean hasConduiteCons;
    private boolean hasTrait2Cons;

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

            hasAutresMal    = tableHasColumn("mal", "autres_mal");
            hasConduiteCons = tableHasColumn("cons", "conduite_cons");
            hasTrait2Cons   = tableHasColumn("cons", "trait2_cons");

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

            while (rs.next()) {
                RawConsultation rc = new RawConsultation(
                        rs.getLong("num_cons"), rs.getLong("num_mal"), rs.getDate("date_cons"),
                        rs.getString("motif_cons"), rs.getString("diag_cons"),
                        rs.getString("rslt_examen_cons"), rs.getString("rslt_para_cons"),
                        hasConduiteCons ? rs.getString("conduite_cons") : null,
                        rs.getString("trait_cons"),
                        hasTrait2Cons ? rs.getString("trait2_cons") : null
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
            Consultation c = buildConsultation(rc);
            List<Traitement> traitements = resolveTraitements(rc, c);
            units.add(new ConsultationUnit(c, traitements));
        }

        return new PatientImportUnit(rp.numMal(), patient, units);
    }

    /**
     * Logique à 3 niveaux, appliquée INDÉPENDAMMENT pour chaque consultation
     * (donc une base qui mélange des consultations "ancien style" trait_cons
     * et des consultations "nouveau style" presc est gérée correctement,
     * ligne par ligne — ce n'est jamais un choix global pour tout le fichier) :
     *
     *  1. On essaie de PARSER trait_cons/trait2_cons (format structuré
     *     "NOM[DOSAGE] forme (durée" + posologie en ligne suivante).
     *     Si ça donne au moins un traitement -> terminé pour cette consultation.
     *
     *  2. Sinon, on regarde la table presc pour cette consultation (num_cons).
     *     Si elle contient des lignes -> terminé.
     *
     *  3. Sinon, si trait_cons contenait quand même du texte (mais du texte
     *     LIBRE, tapé à la main, qui ne respecte pas le format structuré —
     *     ex: "ANTAG 20 +SORBITOL AMP+LIBRAX 1 COM /J+ METEOXANE") -> on ne
     *     tente PAS de le parser comme un médicament structuré (on se
     *     tromperait), on le stocke TEL QUEL sur le champ texte libre de la
     *     consultation, pour ne rien perdre. Aucun Traitement structuré n'est
     *     créé dans ce cas (pas de Medicament à rattacher de façon fiable).
     */
    private List<Traitement> resolveTraitements(RawConsultation rc, Consultation c) {
        List<Traitement> traitements = new ArrayList<>();

        String raw = (rc.traitCons() != null ? rc.traitCons() : "")
                + (rc.trait2Cons() != null ? " " + rc.trait2Cons() : "");

        for (TraitementParser.ParsedTraitement pt : TraitementParser.parse(raw)) {
            Medicament med = getOrCreateMedicament(pt);
            traitements.add(buildTraitement(med, pt.duree, pt.posologie));
        }

        if (!traitements.isEmpty()) {
            return traitements; // trouvé dans trait_cons -> pas besoin de presc
        }

        List<RawPresc> prescRows = prescByConsultation.get(rc.numCons());
        if (prescRows != null) {
            for (RawPresc rpx : prescRows) {
                String medicBrut = clean(rpx.medic());
                if (medicBrut == null || medicBrut.isBlank()) continue;

                Medicament med = getOrCreateMedicamentDirect(
                        extractNom(medicBrut), extractDosage(medicBrut), rpx.forme(), rpx.poso());
                traitements.add(buildTraitement(med, rpx.duree(), rpx.poso()));
            }
        }

        if (!traitements.isEmpty()) {
            return traitements; // trouvé dans presc -> terminé
        }

        // Ni trait_cons structuré, ni presc : si trait_cons contenait quand
        // même du texte libre, on le conserve tel quel sur la consultation.
        if (!raw.isBlank()) {
            c.setTraitement(raw.trim());

            if (unparsedLogCount < MAX_UNPARSED_LOGS) {
                unparsedLogCount++;
                String sample = raw.length() > 150 ? raw.substring(0, 150) + "…" : raw;
                log.info("[import-mdb] num_cons={} : trait_cons en texte libre (non structuré), conservé tel quel. Aperçu : {}",
                        rc.numCons(), sample.replace("\n", " \\n "));
            }
        }

        return traitements;
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
                rs.getString("ant_fam_mal"), hasAutresMal ? rs.getString("autres_mal") : null
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
        c.setMotifConsultation(rc.motif());
        c.setDiagnosticMedical(rc.diag());
        c.setResultatExamenClinique(rc.rsltExamen());
        c.setResultatExamenParacliniques(rc.rsltPara());
        c.setCatEvolution(rc.conduite_cons());
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

    /**
     * Vérifie si une colonne existe dans une table, via les métadonnées JDBC
     * (pas besoin d'ouvrir un ResultSet sur les données). Utilisé UNE SEULE
     * FOIS à l'ouverture pour chaque colonne optionnelle — pas de coût par ligne.
     */
    private boolean tableHasColumn(String tableName, String columnName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    private String buildJdbcUrl() {
        StringBuilder sb = new StringBuilder("jdbc:ucanaccess://").append(filePath);
        if (password != null && !password.isBlank()) sb.append(";password=").append(password);
        sb.append(";ignoreCase=true;memory=true;skipIndexes=true;immediatelyReleaseResources=true");
        return sb.toString();
    }

    private String clean(String v) { return v == null ? null : v.trim(); }

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