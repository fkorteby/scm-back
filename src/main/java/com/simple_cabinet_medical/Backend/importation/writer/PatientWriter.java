package com.simple_cabinet_medical.Backend.importation.writer;

import com.simple_cabinet_medical.Backend.importation.domain.ImportedPatient;
import com.simple_cabinet_medical.Backend.importation.mapping.PatientMapping;
import com.simple_cabinet_medical.Backend.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class PatientWriter implements ItemWriter<ImportedPatient> {

    private final DataSource dataSource;
    private final Long clientId;
    private final PatientMapping patientMapping;

    public PatientWriter(
            DataSource dataSource,
            Long clientId,
            PatientMapping patientMapping
    ) {
        this.dataSource = dataSource;
        this.clientId = clientId;
        this.patientMapping = patientMapping;
    }

    @Override
    public void write(Chunk<? extends ImportedPatient> chunk)
            throws Exception {

        if (chunk.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO patient (
                    nom,
                    prenom,
                    date_naissance,
                    numero_tel,
                    situation,
                    sexe,
                    adresse,
                    profession,
                    assurance,
                    cin,
                    numero_securite_sociale,
                    passport,
                    acte_naissance,
                    antecedents_personnels_medicaux,
                    antecedents_personnels_chirugicaux,
                    antecedents_familiaux,
                    autres,
                    client_creator_id,
                    client_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Connection connection =
                DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps =
                     connection.prepareStatement(
                             sql,
                             new String[]{"id_patient"}
                     )) {

            log.info(
                    "PatientWriter: writing {} patients, autoCommit={}",
                    chunk.size(),
                    connection.getAutoCommit()
            );

            for (ImportedPatient imported : chunk) {

                Patient patient = imported.patient();

                ps.setString(
                        1,
                        patient.getNom()
                );

                ps.setString(
                        2,
                        patient.getPrenom()
                );

                ps.setObject(
                        3,
                        patient.getDateNaissance()
                );

                ps.setString(
                        4,
                        patient.getNumeroTel()
                );

                ps.setString(
                        5,
                        patient.getSituation()
                );

                ps.setString(
                        6,
                        patient.getSexe()
                );

                ps.setString(
                        7,
                        patient.getAdresse()
                );

                ps.setString(
                        8,
                        patient.getProfession()
                );

                ps.setObject(
                        9,
                        patient.getAssurance()
                );

                ps.setString(
                        10,
                        patient.getCin()
                );

                ps.setString(
                        11,
                        patient.getNumeroSecuriteSociale()
                );

                ps.setString(
                        12,
                        patient.getPassport()
                );

                ps.setString(
                        13,
                        patient.getActeNaissance()
                );

                ps.setString(
                        14,
                        patient.getAntecedentsPersonnelsMedicaux()
                );

                ps.setString(
                        15,
                        patient.getAntecedentsPersonnelsChirugicaux()
                );

                ps.setString(
                        16,
                        patient.getAntecedentsFamiliaux()
                );

                ps.setString(
                        17,
                        patient.getAutres()
                );

                ps.setLong(
                        18,
                        clientId
                );

                ps.setLong(
                        19,
                        clientId
                );

                ps.addBatch();
            }

            ps.executeBatch();

            /*
             * Récupération explicite des IDs générés.
             *
             * On a demandé au driver PostgreSQL de retourner
             * uniquement la colonne "id_patient".
             */
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                var metadata = generatedKeys.getMetaData();

                log.info(
                        "GENERATED KEYS: columnCount={}",
                        metadata.getColumnCount()
                );

                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    log.info(
                            "GENERATED KEYS COLUMN {}: name={}, label={}, type={}, typeName={}",
                            i,
                            metadata.getColumnName(i),
                            metadata.getColumnLabel(i),
                            metadata.getColumnType(i),
                            metadata.getColumnTypeName(i)
                    );
                }

                int index = 0;

                while (generatedKeys.next()) {

                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        log.info(
                                "GENERATED KEYS VALUE column {} = {}",
                                i,
                                generatedKeys.getObject(i)
                        );
                    }

                    Object rawId = generatedKeys.getObject("id_patient");

                    log.info(
                            "GENERATED PATIENT ID RAW VALUE = {}, JAVA TYPE = {}",
                            rawId,
                            rawId != null ? rawId.getClass().getName() : null
                    );

                    Long newPatientId = ((Number) rawId).longValue();

                    ImportedPatient imported =
                            chunk.getItems().get(index);

                    patientMapping.put(
                            imported.oldNumMal(),
                            newPatientId
                    );

                    log.info(
                            "PATIENT MAPPING: oldNumMal={} -> newPatientId={}",
                            imported.oldNumMal(),
                            newPatientId
                    );

                    index++;
                }

                if (index != chunk.size()) {
                    throw new IllegalStateException(
                            "Nombre d'IDs générés différent du nombre " +
                                    "de patients importés : generated=" +
                                    index +
                                    ", expected=" +
                                    chunk.size()
                    );
                }
            }
        }
    }
}