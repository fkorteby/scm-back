package com.simple_cabinet_medical.Backend.importation.writer;

import com.simple_cabinet_medical.Backend.importation.domain.ImportedConsultation;
import com.simple_cabinet_medical.Backend.importation.domain.OldPrescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Slf4j
public class ConsultationWriter
        implements ItemWriter<ImportedConsultation>, StepExecutionListener {

    private final DataSource dataSource;
    private final Long clientId;

    private StepExecution stepExecution;
    private long traitementsImportes = 0L;

    /*
     * IMPORTANT : ce cache est maintenant un champ d'instance, préchargé
     * UNE SEULE FOIS dans beforeStep() avec tous les médicaments existants
     * du client. Il n'est plus recréé à chaque chunk : on évite ainsi de
     * refaire des SELECT en boucle sur des médicaments déjà connus.
     *
     * clé : nomCommerciale + "|" + forme (en minuscules)
     */
    private final Map<String, Long> medicamentCache = new HashMap<>();

    public ConsultationWriter(DataSource dataSource, Long clientId) {
        this.dataSource = dataSource;
        this.clientId = clientId;
    }

    @Override
    public void write(Chunk<? extends ImportedConsultation> chunk) throws Exception {

        if (chunk.isEmpty()) {
            return;
        }

        Connection connection = DataSourceUtils.getConnection(dataSource);

        List<? extends ImportedConsultation> items = chunk.getItems();

        log.info(
                "ConsultationWriter: writing {} consultations (batch mode), autoCommit={}",
                items.size(),
                connection.getAutoCommit()
        );

        /*
         * =====================================================
         * 1. BATCH INSERT DES CONSULTATIONS
         * =====================================================
         * Un seul aller-retour réseau pour tout le chunk au lieu
         * d'un executeUpdate() par consultation.
         */
        Long[] consultationIds = insertConsultationsBatch(connection, items);

        /*
         * =====================================================
         * 2. RÉSOLUTION DES MÉDICAMENTS
         * =====================================================
         * Le cache est déjà préchargé (beforeStep). On ne crée en
         * base QUE les médicaments réellement nouveaux, en un seul
         * batch insert pour tout le chunk.
         */
        resolveNewMedicamentsBatch(connection, items);

        /*
         * =====================================================
         * 3. BATCH INSERT DES TRAITEMENTS
         * =====================================================
         * Un seul aller-retour réseau pour tous les traitements
         * du chunk au lieu d'un executeUpdate() par prescription.
         */
        insertTraitementsBatch(connection, items, consultationIds);

        stepExecution.getExecutionContext()
                .putLong("traitementsImportes", traitementsImportes);
    }

    /**
     * =============================================================
     * 1. INSERT CONSULTATIONS EN BATCH
     * =============================================================
     */
    private Long[] insertConsultationsBatch(
            Connection connection,
            List<? extends ImportedConsultation> items
    ) throws SQLException {

        String sql = """
                INSERT INTO consultation (
                    date_consultation,
                    status_consultation,
                    resultat_examen_clinique,
                    resultat_examen_paracliniques,
                    diagnostic_medical,
                    traitement,
                    cat_evolution,
                    motif_consultation,
                    client_id,
                    patient_id,
                    client_creator_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, new String[]{"id_consultation"})) {

            for (ImportedConsultation item : items) {

                var consultation = item.consultation();

                ps.setObject(1, consultation.getDateConsultation());

                if (consultation.getStatusConsultation() != null) {
                    ps.setInt(2, consultation.getStatusConsultation().ordinal());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }

                ps.setString(3, consultation.getResultatExamenClinique());
                ps.setString(4, consultation.getResultatExamenParacliniques());
                ps.setString(5, consultation.getDiagnosticMedical());
                ps.setString(6, consultation.getTraitement());
                ps.setString(7, consultation.getCatEvolution());
                ps.setString(8, consultation.getMotifConsultation());
                ps.setLong(9, clientId);
                ps.setLong(10, item.newPatientId());
                ps.setLong(11, clientId);

                ps.addBatch();
            }

            ps.executeBatch();

            Long[] ids = new Long[items.size()];

            /*
             * Les clés générées reviennent dans l'ordre d'insertion du
             * batch (comportement du driver PostgreSQL avec
             * reWriteBatchedInserts=true) — même hypothèse que celle
             * déjà utilisée dans PatientWriter.
             */
            try (ResultSet rs = ps.getGeneratedKeys()) {
                int index = 0;
                while (rs.next()) {
                    ids[index++] = rs.getLong("id_consultation");
                }

                if (index != items.size()) {
                    throw new IllegalStateException(
                            "Nombre d'IDs de consultation générés (" + index +
                                    ") différent du nombre attendu (" + items.size() + ")"
                    );
                }
            }

            return ids;
        }
    }

    /**
     * =============================================================
     * 2. NOUVEAUX MÉDICAMENTS EN BATCH
     * =============================================================
     */
    private void resolveNewMedicamentsBatch(
            Connection connection,
            List<? extends ImportedConsultation> items
    ) throws SQLException {

        // Clés distinctes du chunk qui ne sont pas déjà dans le cache
        Map<String, String[]> aCreer = new LinkedHashMap<>();

        for (ImportedConsultation item : items) {
            for (OldPrescription prescription : item.prescriptions()) {

                String nom = clean(prescription.medicPresc());
                if (nom == null) {
                    continue;
                }

                String forme = clean(prescription.formPresc());
                String key = cacheKey(nom, forme);

                if (!medicamentCache.containsKey(key) && !aCreer.containsKey(key)) {
                    aCreer.put(key, new String[]{nom, forme});
                }
            }
        }

        if (aCreer.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO medicament (
                    nom_commerciale,
                    forme,
                    client_creator_id
                )
                VALUES (?, ?, ?)
                """;

        List<String> keys = new ArrayList<>(aCreer.keySet());

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, new String[]{"id_medicament"})) {

            for (String key : keys) {
                String[] value = aCreer.get(key);

                ps.setString(1, value[0]);
                ps.setString(2, value[1]);
                ps.setLong(3, clientId);

                ps.addBatch();
            }

            ps.executeBatch();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                int index = 0;
                while (rs.next()) {
                    medicamentCache.put(keys.get(index), rs.getLong("id_medicament"));
                    index++;
                }

                if (index != keys.size()) {
                    throw new IllegalStateException(
                            "Nombre d'IDs de médicament générés différent du nombre attendu"
                    );
                }
            }

            log.info("{} nouveaux médicaments créés en un seul batch insert", keys.size());
        }
    }

    /**
     * =============================================================
     * 3. INSERT TRAITEMENTS EN BATCH
     * =============================================================
     */
    private void insertTraitementsBatch(
            Connection connection,
            List<? extends ImportedConsultation> items,
            Long[] consultationIds
    ) throws SQLException {

        String sql = """
                INSERT INTO traitement (
                    medicament_id,
                    duree,
                    posologie,
                    id_consultation,
                    ordonnance_type_id,
                    client_creator_id
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int batched = 0;

            for (int i = 0; i < items.size(); i++) {

                ImportedConsultation item = items.get(i);
                Long consultationId = consultationIds[i];

                for (OldPrescription prescription : item.prescriptions()) {

                    String nom = clean(prescription.medicPresc());

                    if (nom == null) {
                        log.warn(
                                "Prescription ignorée : medicament_presc vide, numCons={}",
                                prescription.numCons()
                        );
                        continue;
                    }

                    String forme = clean(prescription.formPresc());
                    Long medicamentId = medicamentCache.get(cacheKey(nom, forme));

                    if (medicamentId == null) {
                        // Ne devrait pas arriver : resolveNewMedicamentsBatch
                        // a déjà créé tout ce qui manquait.
                        log.warn(
                                "Médicament introuvable après résolution : nom={}, forme={}",
                                nom, forme
                        );
                        continue;
                    }

                    ps.setLong(1, medicamentId);
                    ps.setString(2, clean(prescription.dureePresc()));
                    ps.setString(3, clean(prescription.posoPresc()));
                    ps.setLong(4, consultationId);
                    ps.setNull(5, Types.BIGINT);
                    ps.setLong(6, clientId);

                    ps.addBatch();
                    batched++;
                }
            }

            if (batched > 0) {
                ps.executeBatch();
                traitementsImportes += batched;
            }
        }
    }

    private String cacheKey(String nomCommerciale, String forme) {
        return nomCommerciale.toLowerCase()
                + "|"
                + (forme != null ? forme.toLowerCase() : "");
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    /**
     * =============================================================
     * BEFORE STEP
     * =============================================================
     * Précharge TOUS les médicaments existants du client en UNE
     * seule requête, une seule fois pour tout le step. C'est ce qui
     * élimine l'essentiel des allers-retours réseau du writer.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        this.stepExecution = stepExecution;
        this.traitementsImportes = 0L;
        this.medicamentCache.clear();

        String sql = """
                SELECT id_medicament, nom_commerciale, forme
                FROM medicament
                WHERE client_creator_id = ?
                """;

        try (Connection connection = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, clientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = cacheKey(
                            rs.getString("nom_commerciale"),
                            rs.getString("forme")
                    );
                    medicamentCache.put(key, rs.getLong("id_medicament"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Impossible de précharger les médicaments existants", e
            );
        }

        stepExecution.getExecutionContext().putLong("traitementsImportes", 0L);

        log.info(
                "ConsultationWriter initialized for step {} ({} médicaments préchargés)",
                stepExecution.getStepName(),
                medicamentCache.size()
        );
    }

    /**
     * =============================================================
     * AFTER STEP
     * =============================================================
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        log.info(
                "ConsultationWriter finished: traitementsImportes={}",
                traitementsImportes
        );

        stepExecution.getExecutionContext()
                .putLong("traitementsImportes", traitementsImportes);

        return stepExecution.getExitStatus();
    }
}