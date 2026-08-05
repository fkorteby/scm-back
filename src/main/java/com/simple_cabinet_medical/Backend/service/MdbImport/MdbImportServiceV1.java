package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.*;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.PatientImportOutcome;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Service d'import pour les fichiers MDB version 1.0.
 * Voir MdbImportService (v1.7) pour l'explication complète du concept :
 * traitement patient par patient, bloc atomique, préchargement de "cons" en
 * mémoire en UNE seule lecture (au lieu d'un scan par patient) pour éviter
 * le coût du N+1 sur une base ouverte avec skipIndexes=true.
 */
@Service
public class MdbImportServiceV1 {

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

    public MdbImportServiceV1(ClientRepository clientRepository,
                              MedicamentRepository medicamentRepository,
                              ImportRowPersister rowPersister) {
        this.clientRepository = clientRepository;
        this.medicamentRepository = medicamentRepository;
        this.rowPersister = rowPersister;
    }

    // ─── Point d'entrée ────────────────────────────────────────────────────────

    public MdbImportService.ImportResult importFromMdb(String filePath, String password, Long clientId) {
        MdbImportService.ImportResult result = new MdbImportService.ImportResult();
        String url = buildJdbcUrl(filePath, password);

        try (Connection conn = DriverManager.getConnection(url)) {

            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalStateException("Client non trouvé : id=" + clientId));

            Map<String, Medicament> medicamentCache = buildMedicamentCache(1L);

            // ── Préchargement : 1 seul scan complet de "cons" ─────────────────
            Map<Long, List<RawConsultation>> consultationsByPatient = preloadConsultations(conn);

            try (Statement stmt = conn.createStatement();
                 ResultSet rsPatients = stmt.executeQuery("SELECT * FROM mal")) {

                while (rsPatients.next()) {
                    processPatientRow(rsPatients, client, clientId, consultationsByPatient, medicamentCache, result);
                }
            }

        } catch (SQLException e) {
            result.addError("Erreur fatale de connexion/lecture MDB v1.0 : " + e.getMessage());
        }

        return result;
    }

    private Map<Long, List<RawConsultation>> preloadConsultations(Connection conn) throws SQLException {
        Map<Long, List<RawConsultation>> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cons ORDER BY num_mal, id_cons")) {

            while (rs.next()) {
                RawConsultation rc = new RawConsultation(
                        rs.getLong("id_cons"),
                        rs.getLong("num_mal"),
                        rs.getDate("date_cons"),
                        rs.getString("motif_cons"),
                        rs.getString("diag_cons"),
                        rs.getString("rslt_examen_cons"),
                        rs.getString("rslt_para_cons"),
                        rs.getString("trait_cons")
                );
                map.computeIfAbsent(rc.numMal(), k -> new ArrayList<>()).add(rc);
            }
        }
        return map;
    }

    // ─── Traitement d'un patient (bloc complet) ────────────────────────────────

    private void processPatientRow(ResultSet rsPatient, Client client, Long clientId,
                                   Map<Long, List<RawConsultation>> consultationsByPatient,
                                   Map<String, Medicament> medicamentCache,
                                   MdbImportService.ImportResult result) {

        long numMal;
        try {
            numMal = rsPatient.getLong("num_mal");
        } catch (SQLException e) {
            result.addError("Ligne patient illisible (num_mal introuvable) : " + e.getMessage());
            result.patientsFailed.incrementAndGet();
            return;
        }

        try {
            Patient patient = buildPatient(rsPatient, client, clientId);

            List<RawConsultation> rawConsultations =
                    consultationsByPatient.getOrDefault(numMal, Collections.emptyList());

            List<ConsultationUnit> consultationUnits = new ArrayList<>();
            for (RawConsultation rc : rawConsultations) {
                try {
                    Consultation c = buildConsultation(rc, clientId);
                    List<Traitement> traitements = parseTraitementsForConsultation(rc.traitCons(), medicamentCache, clientId);
                    consultationUnits.add(new ConsultationUnit(c, traitements));

                } catch (Exception e) {
                    result.addError("Consultation id_cons=" + rc.idCons() + " (patient num_mal=" + numMal
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

    private Patient buildPatient(ResultSet rs, Client client, Long clientId) throws SQLException {
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
        c.setCatEvolution(null);
        c.setClientCreatorId(clientId);
        c.setStatusConsultation(EStatusConsultation.TERMINEE);
        return c;
    }

    // ─── Traitements (trait_cons uniquement, formats A/B) ──────────────────────

    private List<Traitement> parseTraitementsForConsultation(String rawTrait,
                                                             Map<String, Medicament> cache, Long clientId) {
        List<Traitement> traitements = new ArrayList<>();

        for (TraitementParserV1.ParsedTraitement pt : TraitementParserV1.parse(rawTrait)) {
            if (pt.nomCommerciale == null || pt.nomCommerciale.isBlank() || pt.nomCommerciale.length() < 2) {
                continue;
            }
            try {
                Medicament med = getOrCreateMedicament(pt, cache, clientId);

                Traitement t = new Traitement();
                t.setMedicament(med);
                t.setDuree(pt.duree != null && !pt.duree.isBlank() ? pt.duree : null);
                t.setPosologie(pt.posologie != null && !pt.posologie.isBlank() ? pt.posologie : "Non précisée");
                t.setClientCreatorId(clientId);

                traitements.add(t);
            } catch (Exception ignored) {
                // Médicament non résolvable pour ce traitement précis uniquement.
            }
        }
        return traitements;
    }

    // ─── Cache médicaments ─────────────────────────────────────────────────────

    private Map<String, Medicament> buildMedicamentCache(Long defaultClientId) {
        Map<String, Medicament> cache = new HashMap<>();
        for (Medicament m : medicamentRepository.findMedicamentsByClientCreatorId(defaultClientId)) {
            cache.put(cacheKey(m.getNomCommerciale(), m.getDosage()), m);
        }
        return cache;
    }

    private Medicament getOrCreateMedicament(TraitementParserV1.ParsedTraitement pt,
                                             Map<String, Medicament> cache, Long clientId) {
        String key = cacheKey(pt.nomCommerciale, pt.dosage);
        if (cache.containsKey(key)) return cache.get(key);

        Medicament med = new Medicament();
        med.setNomCommerciale(pt.nomCommerciale);
        med.setDosage(pt.dosage);
        med.setForme(pt.forme);
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

    private String buildJdbcUrl(String filePath, String password) {
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

    // ─── DTO interne de préchargement ──────────────────────────────────────────

    private record RawConsultation(long idCons, long numMal, java.sql.Date dateCons,
                                   String motif, String diag, String rsltExamen, String rsltPara,
                                   String traitCons) {
    }
}