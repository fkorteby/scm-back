package com.simple_cabinet_medical.Backend.importation.reader;

import com.simple_cabinet_medical.Backend.importation.domain.OldPatient;
import com.simple_cabinet_medical.Backend.importation.util.MdbUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class PatientReader implements ItemStreamReader<OldPatient> {

    private final String filePath;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public PatientReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        log.info("Opening MDB file: {}", filePath);
        long start = System.currentTimeMillis();

        try {
            connection = DriverManager.getConnection(MdbUtils.buildJdbcUrl(filePath));

            log.info("MDB connection opened in {} ms",
                    System.currentTimeMillis() - start);

            statement = connection.createStatement(
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
            );

            // Check column using uppercase table name just in case
            boolean hasAutresMal = hasColumn("AUTRES_MAL");

            String conditionalColumn = hasAutresMal ? ", autres_mal" : ", NULL AS autres_mal";

            String sql = String.format("""
                SELECT 
                    num_mal,
                    nom_mal,
                    prenom_mal,
                    ddn_mal,
                    adr_mal,
                    tel_mal,
                    sexe_mal,
                    sit_fam_mal,
                    profession_mal,
                    assurance_mal,
                    ant_med_mal,
                    ant_chir_mal,
                    ant_fam_mal%s
                FROM mal
                """, conditionalColumn);

            resultSet = statement.executeQuery(sql);
            log.info("Query on table 'mal' executed");

        } catch (SQLException e) {
            log.error("Unable to open MDB file: {}", filePath, e);
            throw new ItemStreamException("Unable to open MDB file: " + filePath, e);
        }
    }

    private boolean hasColumn(String columnName) throws SQLException {
        var metadata = connection.getMetaData();

        // Pass null or uppercase table name depending on Access metadata convention
        try (ResultSet columns = metadata.getColumns(null, null, "mal", columnName)) {
            if (columns.next()) {
                return true;
            }
        }

        // Fallback: try uppercase table name "MAL"
        try (ResultSet columns = metadata.getColumns(null, null, "MAL", columnName)) {
            return columns.next();
        }
    }

    @Override
    public OldPatient read() throws Exception {

        if (resultSet == null) {
            throw new IllegalStateException("PatientReader has not been opened.");
        }

        if (!resultSet.next()) {
            return null;
        }

        return mapPatient(resultSet);
    }

    private OldPatient mapPatient(ResultSet rs) throws SQLException {
        return new OldPatient(
                rs.getLong("num_mal"),
                rs.getString("nom_mal"),
                rs.getString("prenom_mal"),
                rs.getDate("ddn_mal"),
                rs.getString("adr_mal"),
                rs.getString("tel_mal"),
                rs.getString("sexe_mal"),
                rs.getString("sit_fam_mal"),
                rs.getString("profession_mal"),
                rs.getString("assurance_mal"),
                rs.getString("ant_med_mal"),
                rs.getString("ant_chir_mal"),
                rs.getString("ant_fam_mal"),
                rs.getString("autres_mal")
        );
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // À implémenter plus tard pour le restart précis.
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("Closing MDB reader");
        MdbUtils.closeQuietly(resultSet, statement, connection);
    }
}