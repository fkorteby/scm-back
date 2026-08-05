package com.simple_cabinet_medical.Backend.service.MdbImport;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.MedicamentRepository;
import com.simple_cabinet_medical.Backend.service.MdbImport.ImportRowPersister.ConsultationUnit;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawConsultation;
import com.simple_cabinet_medical.Backend.service.MdbImport.MdbRawRows.RawPatient;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Reader pour les fichiers MDB v1.0 — préchargement en Map (voir Javadoc de
 * MdbPatientItemReader v1.7 pour l'explication complète de pourquoi on a
 * abandonné le merge-join en flux au profit de ce préchargement).
 */
public class MdbPatientItemReaderV1 implements ItemStreamReader<PatientImportUnit> {

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

    private Client client;
    private Map<String, Medicament> medicamentCache;

    public MdbPatientItemReaderV1(String filePath, String password, Long clientId,
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

            consultationsByPatient = preloadConsultations();

            stmtPatients = conn.createStatement();
            ResultSet rsPatients = stmtPatients.executeQuery("SELECT * FROM mal");
            patientCursor = new RowCursor<>(rsPatients, this::mapPatient);

        } catch (SQLException e) {
            throw new ItemStreamException("Impossible d'ouvrir le fichier MDB v1.0 : " + e.getMessage(), e);
        }
    }

    private Map<Long, List<RawConsultation>> preloadConsultations() throws SQLException {
        Map<Long, List<RawConsultation>> map = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cons")) {

            while (rs.next()) {
                RawConsultation rc = new RawConsultation(
                        rs.getLong("id_cons"), rs.getLong("num_mal"), rs.getDate("date_cons"),
                        rs.getString("motif_cons"), rs.getString("diag_cons"),
                        rs.getString("rslt_examen_cons"), rs.getString("rslt_para_cons"),
                        null, rs.getString("trait_cons"), null
                );
                map.computeIfAbsent(rc.numMal(), k -> new ArrayList<>()).add(rc);
            }
        }
        return map;
    }

    @Override
    public PatientImportUnit read() throws Exception {
        if (patientCursor.isExhausted()) {
            return null;
        }

        RawPatient rp = patientCursor.consume();
        Patient patient = buildPatient(rp);

        List<ConsultationUnit> units = new ArrayList<>();
        List<RawConsultation> rawConsultations = consultationsByPatient.getOrDefault(rp.numMal(), Collections.emptyList());

        for (RawConsultation rc : rawConsultations) {
            List<Traitement> traitements = new ArrayList<>();
            for (TraitementParserV1.ParsedTraitement pt : TraitementParserV1.parse(rc.traitCons())) {
                if (pt.nomCommerciale == null || pt.nomCommerciale.isBlank() || pt.nomCommerciale.length() < 2) {
                    continue;
                }
                Medicament med = getOrCreateMedicament(pt);
                Traitement t = new Traitement();
                t.setMedicament(med);
                t.setDuree(pt.duree != null && !pt.duree.isBlank() ? pt.duree : null);
                t.setPosologie(pt.posologie != null && !pt.posologie.isBlank() ? pt.posologie : "Non précisée");
                t.setClientCreatorId(clientId);
                traitements.add(t);
            }
            units.add(new ConsultationUnit(buildConsultation(rc), traitements));
        }

        return new PatientImportUnit(rp.numMal(), patient, units);
    }

    @Override
    public void update(ExecutionContext executionContext) {
    }

    @Override
    public void close() throws ItemStreamException {
        closeQuietly(stmtPatients);
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }

    private RawPatient mapPatient(ResultSet rs) throws SQLException {
        return new RawPatient(
                rs.getLong("num_mal"), rs.getString("nom_mal"), rs.getString("prenom_mal"),
                rs.getDate("ddn_mal"), rs.getString("adr_mal"), rs.getString("tel_mal"),
                rs.getString("sexe_mal"), rs.getString("sit_fam_mal"), rs.getString("profession_mal"),
                rs.getString("assurance_mal"), rs.getString("ant_med_mal"), rs.getString("ant_chir_mal"),
                rs.getString("ant_fam_mal"), null
        );
    }

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
        c.setCatEvolution(null);
        c.setClientCreatorId(clientId);
        c.setStatusConsultation(EStatusConsultation.TERMINEE);
        return c;
    }

    private Medicament getOrCreateMedicament(TraitementParserV1.ParsedTraitement pt) {
        String key = cacheKey(pt.nomCommerciale, pt.dosage);
        Medicament cached = medicamentCache.get(key);
        if (cached != null) return cached;

        Medicament med = new Medicament();
        med.setNomCommerciale(pt.nomCommerciale);
        med.setDosage(pt.dosage);
        med.setForme(pt.forme);
        med.setPosologie(pt.posologie);
        med.setClientCreatorId(1L);

        med = rowPersister.saveMedicament(med);
        medicamentCache.put(key, med);
        return med;
    }

    private String cacheKey(String nom, String dosage) {
        return (nom == null ? "" : nom.toUpperCase()) + "_" + (dosage == null ? "" : dosage.toUpperCase());
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