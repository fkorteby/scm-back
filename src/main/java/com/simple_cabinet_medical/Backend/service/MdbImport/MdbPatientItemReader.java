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
 * Reader for "v1.7" MDB files.
 *
 * <p>NOTE 1 (optional columns): some MDB files of this "version 1.7" do not
 * actually have every expected column (e.g. trait2_cons missing on some
 * client files). All optional columns are checked ONCE at open() time (not
 * per row, for performance) — a missing column simply means the field is
 * skipped, never a crash of the whole Job.
 *
 * <p>NOTE 2 (treatment source priority): trait_cons/trait2_cons is parsed
 * FIRST for a given consultation; if it yields at least one treatment, the
 * presc table is not consulted for that consultation. Only if trait_cons is
 * empty/unparsable do we fall back to presc.
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

    // Columns detected once at open() time (see open())
    private boolean hasAutresMal;
    private boolean hasConduiteCons;
    private boolean hasTrait2Cons;

    private static final Pattern EXTRACT_BRACKET =
            Pattern.compile("^(.+?)\\s*\\[([^\\]]+)\\]\\s*$");
    private static final Pattern EXTRACT_PARENTHESIS =
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

    // ─── Spring Batch lifecycle ─────────────────────────────────────────────

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            conn = DriverManager.getConnection(buildJdbcUrl());

            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalStateException("Client not found: id=" + clientId));

            medicamentCache = new HashMap<>();
            for (Medicament m : medicamentRepository.findMedicamentsByClientCreatorId(1L)) {
                medicamentCache.put(cacheKey(m.getNomCommerciale(), m.getDosage()), m);
            }

            hasAutresMal    = tableHasColumn("mal", "autres_mal");
            hasConduiteCons = tableHasColumn("cons", "conduite_cons");
            hasTrait2Cons   = tableHasColumn("cons", "trait2_cons");

            boolean hasPrescTable = tableExists("presc");

            // ── Preload (two full scans, done once) ──────────────────────
            consultationsByPatient = preloadConsultations();
            prescByConsultation = hasPrescTable ? preloadPresc() : Collections.emptyMap();

            // ── Patient cursor (streaming, stays a live cursor) ──────────
            stmtPatients = conn.createStatement();
            ResultSet rsPatients = stmtPatients.executeQuery("SELECT * FROM mal");
            patientCursor = new RowCursor<>(rsPatients, this::mapPatient);

        } catch (SQLException e) {
            throw new ItemStreamException("Unable to open v1.7 MDB file: " + e.getMessage(), e);
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
            return null; // end of stream -> Spring Batch stops the step cleanly
        }

        RawPatient rp = patientCursor.consume();
        Patient patient = buildPatient(rp);

        List<ConsultationUnit> units = new ArrayList<>();
        List<RawConsultation> rawConsultations = consultationsByPatient.getOrDefault(rp.numMal(), Collections.emptyList());

        for (RawConsultation rc : rawConsultations) {
            Consultation c = buildConsultation(rc);
            List<Traitement> treatments = resolveTreatments(rc, c);
            units.add(new ConsultationUnit(c, treatments));
        }

        return new PatientImportUnit(rp.numMal(), patient, units);
    }

    /**
     * Three-level logic, applied INDEPENDENTLY for each consultation (so a
     * database mixing "old style" trait_cons and "new style" presc
     * consultations is handled correctly, row by row — never a global choice
     * for the whole file):
     *
     * <ol>
     *   <li>Try to PARSE trait_cons/trait2_cons (structured format
     *       "NAME[DOSAGE] form (duration" + dosage instructions on the next
     *       line). If it yields at least one treatment -> done for this
     *       consultation.</li>
     *   <li>Otherwise, look at the presc table for this consultation
     *       (num_cons). If it has rows -> done.</li>
     *   <li>Otherwise, if trait_cons still contained text (but FREE text,
     *       hand-typed, not matching the structured format — e.g.
     *       "ANTAG 20 +SORBITOL AMP+LIBRAX 1 COM /J+ METEOXANE") -> it is NOT
     *       parsed as a structured drug (too risky), it is stored AS-IS on
     *       the consultation's free-text field so nothing is lost. No
     *       structured Traitement is created in this case (no drug can be
     *       reliably attached).</li>
     * </ol>
     */
    private List<Traitement> resolveTreatments(RawConsultation rc, Consultation c) {
        List<Traitement> treatments = new ArrayList<>();

        String raw = (rc.traitCons() != null ? rc.traitCons() : "")
                + (rc.trait2Cons() != null ? " " + rc.trait2Cons() : "");

        for (TraitementParser.ParsedTraitement pt : TraitementParser.parse(raw)) {
            Medicament med = getOrCreateMedicament(pt);
            treatments.add(buildTraitement(med, pt.duree, pt.posologie));
        }

        if (!treatments.isEmpty()) {
            return treatments; // found in trait_cons -> no need for presc
        }

        List<RawPresc> prescRows = prescByConsultation.get(rc.numCons());
        if (prescRows != null) {
            for (RawPresc rpx : prescRows) {
                String rawDrug = clean(rpx.medic());
                if (rawDrug == null || rawDrug.isBlank()) continue;

                Medicament med = getOrCreateMedicamentDirect(
                        extractName(rawDrug), extractDosage(rawDrug), rpx.forme(), rpx.poso());
                treatments.add(buildTraitement(med, rpx.duree(), rpx.poso()));
            }
        }

        if (!treatments.isEmpty()) {
            return treatments; // found in presc -> done
        }

        // Neither structured trait_cons nor presc: if trait_cons still had
        // free text, keep it as-is on the consultation.
        if (!raw.isBlank()) {
            c.setTraitement(raw.trim());

            if (unparsedLogCount < MAX_UNPARSED_LOGS) {
                unparsedLogCount++;
                String sample = raw.length() > 150 ? raw.substring(0, 150) + "…" : raw;
                log.info("[import-mdb] num_cons={}: unstructured free-text trait_cons, kept as-is. Preview: {}",
                        rc.numCons(), sample.replace("\n", " \\n "));
            }
        }

        return treatments;
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // No restartable state for this reader.
    }

    @Override
    public void close() throws ItemStreamException {
        closeQuietly(stmtPatients);
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }

    // ─── ResultSet -> raw DTO mapping ────────────────────────────────────────

    private RawPatient mapPatient(ResultSet rs) throws SQLException {
        return new RawPatient(
                rs.getLong("num_mal"), rs.getString("nom_mal"), rs.getString("prenom_mal"),
                rs.getDate("ddn_mal"), rs.getString("adr_mal"), rs.getString("tel_mal"),
                rs.getString("sexe_mal"), rs.getString("sit_fam_mal"), rs.getString("profession_mal"),
                rs.getString("assurance_mal"), rs.getString("ant_med_mal"), rs.getString("ant_chir_mal"),
                rs.getString("ant_fam_mal"), hasAutresMal ? rs.getString("autres_mal") : null
        );
    }

    // ─── Building JPA entities (not persisted yet) ──────────────────────────

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
        t.setPosologie(poso != null && !poso.isBlank() ? poso : "Not specified");
        t.setClientCreatorId(clientId);
        return t;
    }

    // ─── Drugs ───────────────────────────────────────────────────────────

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

    // ─── Name / dosage extraction ────────────────────────────────────────────

    private String extractName(String drug) {
        if (drug == null) return null;
        Matcher m = EXTRACT_BRACKET.matcher(drug.trim());
        if (m.matches()) return m.group(1).trim();
        m = EXTRACT_PARENTHESIS.matcher(drug.trim());
        if (m.matches()) return m.group(1).trim();
        return drug.trim();
    }

    private String extractDosage(String drug) {
        if (drug == null) return null;
        Matcher m = EXTRACT_BRACKET.matcher(drug.trim());
        if (m.matches()) return m.group(2).trim();
        m = EXTRACT_PARENTHESIS.matcher(drug.trim());
        if (m.matches()) return m.group(2).trim();
        return null;
    }

    // ─── Utilities ──────────────────────────────────────────────────────────

    private boolean tableExists(String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    /**
     * Checks whether a column exists in a table, via JDBC metadata (no need
     * to open a ResultSet on the actual data). Called ONCE at open() time
     * per optional column — no per-row cost.
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