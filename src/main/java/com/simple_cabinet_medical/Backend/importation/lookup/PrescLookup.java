package com.simple_cabinet_medical.Backend.importation.lookup;

import com.simple_cabinet_medical.Backend.importation.domain.OldPrescription;
import com.simple_cabinet_medical.Backend.importation.util.MdbConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PrescLookup {

    private final MdbConnection mdbConnection;

    private boolean hasPrescTable;

    public PrescLookup(MdbConnection mdbConnection) {
        this.mdbConnection = mdbConnection;
    }

    /**
     * Détecte la présence de la table presc.
     *
     * Cette méthode est appelée une seule fois.
     */
    public void initialize() throws SQLException {

        hasPrescTable =
                mdbConnection.hasTable("presc");

        log.info(
                "MDB table 'presc' exists: {}",
                hasPrescTable
        );
    }

    /**
     * Recherche les prescriptions d'une consultation.
     */
    public List<OldPrescription> findByConsultation(
            long numCons
    ) throws SQLException {

        /*
         * Si le MDB ne possède pas la table presc,
         * aucune prescription.
         */
        if (!hasPrescTable) {
            return List.of();
        }

        Connection connection =
                mdbConnection.getConnection();

        String sql = """
                SELECT
                    num_cons,
                    num_mal,
                    medic_presc,
                    form_presc,
                    poso_presc,
                    duree_presc
                FROM presc
                WHERE num_cons = ?
                """;

        List<OldPrescription> prescriptions =
                new ArrayList<>();

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setLong(1, numCons);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    prescriptions.add(
                            new OldPrescription(
                                    rs.getLong("num_cons"),
                                    rs.getLong("num_mal"),
                                    rs.getString("medic_presc"),
                                    rs.getString("form_presc"),
                                    rs.getString("poso_presc"),
                                    rs.getString("duree_presc")
                            )
                    );
                }
            }
        }

        return prescriptions;
    }
}