package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.*;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.PatientImportOutcome;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.*;

/**
 * Service d'import pour les fichiers MDB version 1.7.
 *
 * PERFORMANCE — pourquoi on précharge cons/presc en mémoire au lieu de
 * requêter la base à chaque patient :
 *
 *  L'URL JDBC utilise skipIndexes=true (nécessaire pour lire certains fichiers
 *  Access dont les index sont corrompus). Conséquence : toute requête
 *  "WHERE num_mal = ?" ou "WHERE num_cons = ?" déclenche un SCAN COMPLET de
 *  la table, sans utiliser d'index. Faire ça une fois par patient (9 000+ fois)
 *  transforme un import de quelques milliers de lignes en opération "lourde".
 *
 *  La correction : on lit CHAQUE table une seule fois en entier (2 scans
 *  complets pour cons + presc, au lieu d'un scan par patient/consultation),
 *  et on regroupe les lignes en mémoire (Map<num_mal, List<...>> etc.).
 *  Le volume réel (quelques milliers à dizaines de milliers de lignes par
 *  table) tient largement en RAM sous forme de simples "raw rows" — pas
 *  besoin de Spring Batch ni d'entités JPA à ce stade.
 *
 *  Le reste du concept ne change pas : traitement patient par patient, bloc
 *  atomique (patient + consultations + traitements) via
 *  ImportRowPersister.savePatientTree(...), skip propre à chaque niveau.
 */
@Service
public class MdbImportService {

    private final ClientRepository clientRepository;
    private final MedicamentRepository medicamentRepository;
    private final ImportRowPersister rowPersister;

    static {
        try {
            Class.forName("net.ucanaccess.converters.Functions");
        } catch (ClassNotFoundException e) {
            System.err.println("UCanAccess: impossible de charger les fonctions de conversion.");
        }
    }

    public MdbImportService(ClientRepository clientRepository,
                            MedicamentRepository medicamentRepository,
                            ImportRowPersister rowPersister) {
        this.clientRepository = clientRepository;
        this.medicamentRepository = medicamentRepository;
        this.rowPersister = rowPersister;
    }

    // ─── Point d'entrée ────────────────────────────────────────────────────────

    public ImportResult importFromMdb(String filePath, String password, Long clientId) {
        ImportResult result = new ImportResult();
        String url = buildJdbcUrl(filePath, password);

        try (Connection conn = DriverManager.getConnection(url)) {

            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalStateException("Client non trouvé : id=" + clientId));

            Map<String, Medicament> medicamentCache = buildMedicamentCache(1L);
            boolean hasPrescTable = tableExists(conn, "presc");

            // ── Préchargement : 1 scan complet de "cons", 1 de "presc" ────────
            Map<Long, List<RawConsultation>> consultationsByPatient = preloadConsultations(conn);
            Map<Long, List<RawPresc>> prescByConsultation =
                    hasPrescTable ? preloadPresc(conn) : Collections.emptyMap();

            try (Statement stmt = conn.createStatement();
                 ResultSet rsPatients = stmt.executeQuery("SELECT * FROM mal")) {

                boolean hasAutres = hasColumn(rsPatients, "autres_mal");

                while (rsPatients.next()) {
                    processPatientRow(rsPatients, client, clientId, hasAutres,
                            consultationsByPatient, prescByConsultation,
                            medicamentCache, result);
                }
            }

        } catch (SQLException e) {
            result.addError("Erreur fatale de connexion/lecture MDB v1.7 : " + e.getMessage());
        }

        return result;
    }

    // ─── Préchargement des tables cons / presc ─────────────────────────────────

    private Map<Long, List<RawConsultation>> preloadConsultations(Connection conn) throws SQLException {
        Map<Long, List<RawConsultation>> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cons ORDER BY num_mal, num_cons")) {

            boolean hasConduite = hasColumn(rs, "conduite_cons");

            while (rs.next()) {
                RawConsultation rc = new RawConsultation(
                        rs.getLong("num_cons"),
                        rs.getLong("num_mal"),
                        rs.getDate("date_cons"),
                        rs.getString("motif_cons"),
                        rs.getString("diag_cons"),
                        rs.getString("rslt_examen_cons"),
                        rs.getString("rslt_para_cons"),
                        hasConduite ? rs.getString("conduite_cons") : null,
                        rs.getString("trait_cons"),
                        rs.getString("trait2_cons")
                );
                map.computeIfAbsent(rc.numMal(), k -> new ArrayList<>()).add(rc);
            }
        }
        return map;
    }

    private Map<Long, List<RawPresc>> preloadPresc(Connection conn) throws SQLException {
        Map<Long, List<RawPresc>> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM presc ORDER BY num_cons")) {

            while (rs.next()) {
                RawPresc rp = new RawPresc(
                        rs.getLong("num_cons"),
                        rs.getString("medic_presc"),
                        rs.getString("form_presc"),
                        rs.getString("poso_presc"),
                        rs.getString("duree_presc")
                );
                map.computeIfAbsent(rp.numCons(), k -> new ArrayList<>()).add(rp);
            }
        }
        return map;
    }

    // ─── Traitement d'un patient (bloc complet) ────────────────────────────────

    private void processPatientRow(ResultSet rsPatient, Client client, Long clientId, boolean hasAutres,
                                   Map<Long, List<RawConsultation>> consultationsByPatient,
                                   Map<Long, List<RawPresc>> prescByConsultation,
                                   Map<String, Medicament> medicamentCache, ImportResult result) {

        long numMal;
        try {
            numMal = rsPatient.getLong("num_mal");
        } catch (SQLException e) {
            result.addError("Ligne patient illisible (num_mal introuvable) : " + e.getMessage());
            result.patientsFailed.incrementAndGet();
            return;
        }

        try {
            Patient patient = buildPatient(rsPatient, hasAutres, client, clientId);

            List<RawConsultation> rawConsultations =
                    consultationsByPatient.getOrDefault(numMal, Collections.emptyList());

            List<ConsultationUnit> consultationUnits = new ArrayList<>();
            for (RawConsultation rc : rawConsultations) {
                try {
                    Consultation c = buildConsultation(rc, clientId);
                    List<Traitement> traitements = resolveTraitements(rc, prescByConsultation, medicamentCache, clientId, result);
                    consultationUnits.add(new ConsultationUnit(c, traitements));

                } catch (Exception e) {
                    result.addError("Consultation num_cons=" + rc.numCons() + " (patient num_mal=" + numMal
                            + ") ignorée : " + ImportErrorFormatter.describe(e));
                }
            }

            PatientImportOutcome outcome = rowPersister.savePatientTree(patient, consultationUnits);

            result.patientsImported.incrementAndGet();
            result.consultationsImported.addAndGet(outcome.consultationsSaved());
            result.traitementsImported.addAndGet(outcome.traitementsSaved());

        } catch (Exception e) {
            result.patientsFailed.incrementAndGet();
            result.addError("Patient num_mal=" + numMal
                    + " ignoré (avec toutes ses consultations/traitements) : "
                    + ImportErrorFormatter.describe(e));
        }
    }

    private Patient buildPatient(ResultSet rs, boolean hasAutres, Client client, Long clientId) throws SQLException {
        Patient p = new Patient();
        p.setNom(clean(rs.getString("nom_mal")));
        p.setPrenom(clean(rs.getString("prenom_mal")));

        java.sql.Date ddn = rs.getDate("ddn_mal");
        if (ddn != null) p.setDateNaissance(ddn.toLocalDate());

        p.setAdresse(clean(rs.getString("adr_mal")));
        p.setNumeroTel(normalizePhone(rs.getString("tel_mal")));
        p.setSexe(normalizeSexe(clean(rs.getString("sexe_mal"))));
        p.setSituation(clean(rs.getString("sit_fam_mal")));
        p.setProfession(clean(rs.getString("profession_mal")));

        String assurance = rs.getString("assurance_mal");
        p.setAssurance(assurance != null && assurance.equalsIgnoreCase("Oui"));

        p.setAntecedentsPersonnelsMedicaux(rs.getString("ant_med_mal"));
        p.setAntecedentsPersonnelsChirugicaux(rs.getString("ant_chir_mal"));
        p.setAntecedentsFamiliaux(rs.getString("ant_fam_mal"));
        if (hasAutres) p.setAutres(rs.getString("autres_mal"));

        p.setClientCreatorId(clientId);
        p.setClient(client);
        return p;
    }

    private Consultation buildConsultation(RawConsultation rc, Long clientId) {
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

    // ─── Traitements d'une consultation (presc si dispo, sinon fallback) ──────

    private List<Traitement> resolveTraitements(RawConsultation rc,
                                                Map<Long, List<RawPresc>> prescByConsultation,
                                                Map<String, Medicament> cache, Long clientId,
                                                ImportResult result) {
        List<Traitement> traitements = new ArrayList<>();
        List<RawPresc> prescRows = prescByConsultation.get(rc.numCons());

        if (prescRows != null && !prescRows.isEmpty()) {
            for (RawPresc rp : prescRows) {
                try {
                    String medicBrut = clean(rp.medic());
                    if (medicBrut == null || medicBrut.isBlank()) continue;

                    Medicament med = getOrCreateMedicamentDirect(
                            extractNom(medicBrut), extractDosage(medicBrut), rp.forme(), rp.poso(), cache);

                    traitements.add(buildTraitement(med, rp.duree(), rp.poso(), clientId));

                } catch (Exception e) {
                    result.addError("Prescription (num_cons=" + rc.numCons() + ") ignorée : "
                            + ImportErrorFormatter.describe(e));
                }
            }
        } else {
            String raw = (rc.traitCons() != null ? rc.traitCons() : "")
                    + (rc.trait2Cons() != null ? " " + rc.trait2Cons() : "");

            for (TraitementParser.ParsedTraitement pt : TraitementParser.parse(raw)) {
                try {
                    Medicament med = getOrCreateMedicament(pt, cache, clientId);
                    traitements.add(buildTraitement(med, pt.duree, pt.posologie, clientId));

                } catch (Exception e) {
                    result.addError("Traitement (num_cons=" + rc.numCons() + ") ignoré : "
                            + ImportErrorFormatter.describe(e));
                }
            }
        }
        return traitements;
    }

    private Traitement buildTraitement(Medicament m, String duree, String poso, Long clientId) {
        Traitement t = new Traitement();
        t.setMedicament(m);
        t.setDuree(duree);
        t.setPosologie(poso != null && !poso.isBlank() ? poso : "Non précisée");
        t.setClientCreatorId(clientId);
        return t;
    }

    // ─── Extraction nom / dosage depuis "NOM (DOSAGE)" ou "NOM [DOSAGE]" ───────

    private static final Pattern EXTRACT_CROCHET = Pattern.compile(
            "^(.+?)\\s*\\[([^\\]]+)\\]\\s*$"
    );
    private static final Pattern EXTRACT_PARENTHESE = Pattern.compile(
            "^(.+?)\\s*\\(([^)]*(?:\\([^)]*\\)[^)]*)?)\\)\\s*$"
    );

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

    // ─── Cache médicaments ─────────────────────────────────────────────────────

    private Map<String, Medicament> buildMedicamentCache(Long defaultClientId) {
        Map<String, Medicament> cache = new HashMap<>();
        for (Medicament m : medicamentRepository.findMedicamentsByClientCreatorId(defaultClientId)) {
            cache.put(cacheKey(m.getNomCommerciale(), m.getDosage()), m);
        }
        return cache;
    }

    private Medicament getOrCreateMedicamentDirect(String nomCommerciale, String dosage, String forme,
                                                   String posologie, Map<String, Medicament> cache) {
        String key = cacheKey(nomCommerciale, dosage);
        if (cache.containsKey(key)) return cache.get(key);

        Medicament med = new Medicament();
        med.setNomCommerciale(nomCommerciale);
        med.setDosage(dosage);
        med.setForme(forme);
        med.setPosologie(posologie);
        med.setClientCreatorId(1L);

        med = rowPersister.saveMedicament(med);
        cache.put(key, med);
        return med;
    }

    private Medicament getOrCreateMedicament(TraitementParser.ParsedTraitement pt,
                                             Map<String, Medicament> cache, Long clientId) {
        String key = cacheKey(pt.nomCommerciale, pt.dosage);
        if (cache.containsKey(key)) return cache.get(key);

        Medicament med = new Medicament();
        med.setNomCommerciale(pt.nomCommerciale);
        med.setDosage(pt.dosage);
        med.setForme(pt.forme);
        med.setDuree(pt.duree);
        med.setPosologie(pt.posologie);
        med.setClientCreatorId(1L);

        med = rowPersister.saveMedicament(med);
        cache.put(key, med);
        return med;
    }

    private String cacheKey(String nom, String dosage) {
        return (nom == null ? "" : nom.toUpperCase()) + "_" + (dosage == null ? "" : dosage.toUpperCase());
    }

    // ─── Utilitaires ───────────────────────────────────────────────────────────

    private boolean tableExists(Connection conn, String tableName) {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        } catch (SQLException e) {
            return false;
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

    private String buildJdbcUrl(String filePath, String password) {
        StringBuilder sb = new StringBuilder("jdbc:ucanaccess://").append(filePath);
        if (password != null && !password.isBlank()) sb.append(";password=").append(password);
        sb.append(";ignoreCase=true;memory=true;skipIndexes=true;immediatelyReleaseResources=true");
        return sb.toString();
    }

    private String clean(String value) { return value == null ? null : value.trim(); }

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

    // ─── DTOs internes de préchargement (une seule lecture des tables) ────────

    private record RawConsultation(long numCons, long numMal, java.sql.Date dateCons,
                                   String motif, String diag, String rsltExamen, String rsltPara,
                                   String conduite, String traitCons, String trait2Cons) {
    }

    private record RawPresc(long numCons, String medic, String forme, String poso, String duree) {
    }

    // ─── ImportResult ──────────────────────────────────────────────────────────

    public static class ImportResult {
        public final AtomicInteger patientsImported      = new AtomicInteger(0);
        public final AtomicInteger patientsFailed        = new AtomicInteger(0);
        public final AtomicInteger consultationsImported = new AtomicInteger(0);
        public final AtomicInteger traitementsImported   = new AtomicInteger(0);
        public final List<String>  errors                = new ArrayList<>();

        public void addError(String msg) { errors.add(msg); }

        @Override
        public String toString() {
            return String.format(
                    "Patients importés: %d | Patients échoués: %d | Consultations: %d | Traitements: %d | Erreurs: %d",
                    patientsImported.get(), patientsFailed.get(), consultationsImported.get(),
                    traitementsImported.get(), errors.size());
        }
    }
}