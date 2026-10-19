package com.simple_cabinet_medical.Backend.importation.util;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class MdbUtils {

    private static final String PASSWORD = "farouk123456";

    private MdbUtils() {
    }

    public static String buildJdbcUrl(String filePath) {

        // Miroir HSQLDB sur disque au lieu de la RAM : indispensable
        // pour les grosses bases et pour un conteneur limité en mémoire.
        File mirror = new File(
                System.getProperty("java.io.tmpdir"),
                "ucanaccess-mirror-" + UUID.randomUUID()
        );

        return "jdbc:ucanaccess://" + filePath
                + ";password=" + PASSWORD
                + ";memory=false"
                + ";keepMirror=" + mirror.getAbsolutePath()
                + ";openExclusive=false"
                + ";ignoreCase=true";
    }

    public static void closeQuietly(
            ResultSet resultSet,
            Statement statement,
            Connection connection
    ) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException ignored) {
        }

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ignored) {
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}