package com.simple_cabinet_medical.Backend.importation.reader;

import com.simple_cabinet_medical.Backend.importation.domain.OldConsultation;
import com.simple_cabinet_medical.Backend.importation.util.MdbConnection;
import com.simple_cabinet_medical.Backend.importation.util.MdbConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class ConsultationReader
        implements ItemStreamReader<OldConsultation> {

    private final MdbConnection mdbConnection;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private boolean hasConduiteCons;
    private boolean hasTraitCons;
    private boolean hasTrait2Cons;

    public ConsultationReader(MdbConnection mdbConnection) {
        this.mdbConnection = mdbConnection;
    }

    @Override
    public void open(ExecutionContext executionContext)
            throws ItemStreamException {

        log.info("Opening ConsultationReader");

        try {

            /*
             * La connexion est normalement déjà ouverte
             * par la configuration du Step.
             */
            connection = mdbConnection.getConnection();

            /*
             * La table cons est obligatoire.
             */
            if (!mdbConnection.hasTable("cons")) {
                throw new SQLException(
                        "La table 'cons' est absente du fichier MDB"
                );
            }

            /*
             * Détection des colonnes optionnelles.
             */
            hasConduiteCons =
                    mdbConnection.hasColumn(
                            "cons",
                            "conduite_cons"
                    );

            hasTraitCons =
                    mdbConnection.hasColumn(
                            "cons",
                            "trait_cons"
                    );

            hasTrait2Cons =
                    mdbConnection.hasColumn(
                            "cons",
                            "trait2_cons"
                    );

            log.info(
                    "Consultation columns: conduite_cons={}, trait_cons={}, trait2_cons={}",
                    hasConduiteCons,
                    hasTraitCons,
                    hasTrait2Cons
            );

            /*
             * Statement en lecture forward-only.
             */
            statement = connection.createStatement(
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
            );

            /*
             * Colonnes optionnelles.
             */
            String conduiteColumn = hasConduiteCons
                    ? "conduite_cons"
                    : "NULL AS conduite_cons";

            String traitColumn = hasTraitCons
                    ? "trait_cons"
                    : "NULL AS trait_cons";

            String trait2Column = hasTrait2Cons
                    ? "trait2_cons"
                    : "NULL AS trait2_cons";

            String sql = String.format("""
                    SELECT
                        num_cons,
                        num_mal,
                        date_cons,
                        motif_cons,
                        diag_cons,
                        rslt_examen_cons,
                        rslt_para_cons,
                        %s,
                        %s,
                        %s
                    FROM cons
                    """,
                    conduiteColumn,
                    traitColumn,
                    trait2Column
            );

            log.debug("Consultation SQL: {}", sql);

            resultSet = statement.executeQuery(sql);

            log.info("Consultation query executed");

        } catch (SQLException e) {

            log.error(
                    "Unable to open ConsultationReader",
                    e
            );

            close();

            throw new ItemStreamException(
                    "Unable to open ConsultationReader",
                    e
            );
        }
    }

    @Override
    public OldConsultation read() throws Exception {

        if (resultSet == null) {
            throw new IllegalStateException(
                    "ConsultationReader has not been opened."
            );
        }

        if (!resultSet.next()) {
            return null;
        }

        return mapConsultation(resultSet);
    }

    private OldConsultation mapConsultation(ResultSet rs)
            throws SQLException {

        return new OldConsultation(
                rs.getLong("num_cons"),
                rs.getLong("num_mal"),
                rs.getDate("date_cons"),
                rs.getString("motif_cons"),
                rs.getString("diag_cons"),
                rs.getString("rslt_examen_cons"),
                rs.getString("rslt_para_cons"),
                rs.getString("conduite_cons"),
                rs.getString("trait_cons"),
                rs.getString("trait2_cons")
        );
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // Restart précis à implémenter plus tard.
    }

    @Override
    public void close() throws ItemStreamException {

        log.info("Closing ConsultationReader");

        /*
         * IMPORTANT :
         * On ne ferme PAS connection ici.
         *
         * Elle appartient à MdbConnection.
         */
        MdbConnectionUtils.closeQuietly(
                resultSet,
                statement
        );

        resultSet = null;
        statement = null;
        connection = null;
    }
}