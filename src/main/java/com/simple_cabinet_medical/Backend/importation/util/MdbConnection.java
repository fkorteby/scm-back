package com.simple_cabinet_medical.Backend.importation.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MdbConnection implements AutoCloseable {

    private final String filePath;

    private Connection connection;

    public MdbConnection(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Ouvre la connexion MDB.
     */
    public void open() throws SQLException {

        if (connection != null && !connection.isClosed()) {
            return;
        }

        log.info("Opening MDB connection: {}", filePath);

        long start = System.currentTimeMillis();

        connection = DriverManager.getConnection(
                MdbUtils.buildJdbcUrl(filePath)
        );

        log.info(
                "MDB connection opened in {} ms",
                System.currentTimeMillis() - start
        );
    }

    /**
     * Retourne la connexion actuellement ouverte.
     */
    public Connection getConnection() {

        if (connection == null) {
            throw new IllegalStateException(
                    "MDB connection has not been opened."
            );
        }

        return connection;
    }

    /**
     * Vérifie si une table existe.
     */
    public boolean hasTable(String tableName)
            throws SQLException {

        Connection connection = getConnection();

        var metadata = connection.getMetaData();

        /*
         * Première tentative avec le nom fourni.
         */
        try (ResultSet tables = metadata.getTables(
                null,
                null,
                tableName,
                new String[]{"TABLE"}
        )) {
            if (tables.next()) {
                return true;
            }
        }

        /*
         * Deuxième tentative en majuscules.
         */
        try (ResultSet tables = metadata.getTables(
                null,
                null,
                tableName.toUpperCase(),
                new String[]{"TABLE"}
        )) {
            return tables.next();
        }
    }

    /**
     * Vérifie si une colonne existe dans une table.
     */
    public boolean hasColumn(
            String tableName,
            String columnName
    ) throws SQLException {

        Connection connection = getConnection();

        var metadata = connection.getMetaData();

        /*
         * Première tentative avec les noms fournis.
         */
        try (ResultSet columns = metadata.getColumns(
                null,
                null,
                tableName,
                columnName
        )) {
            if (columns.next()) {
                return true;
            }
        }

        /*
         * Deuxième tentative en majuscules.
         */
        try (ResultSet columns = metadata.getColumns(
                null,
                null,
                tableName.toUpperCase(),
                columnName.toUpperCase()
        )) {
            return columns.next();
        }
    }

    /**
     * Ferme la connexion MDB.
     */
    @Override
    public void close() {

        if (connection == null) {
            return;
        }

        try {
            if (!connection.isClosed()) {
                log.info("Closing MDB connection");
                connection.close();
            }
        } catch (SQLException e) {
            log.warn("Error while closing MDB connection", e);
        } finally {
            connection = null;
        }
    }
}