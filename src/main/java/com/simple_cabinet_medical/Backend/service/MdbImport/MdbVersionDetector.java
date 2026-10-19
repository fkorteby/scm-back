package com.simple_cabinet_medical.Backend.service.MdbImport;

import org.springframework.stereotype.Component;
import java.sql.*;

@Component
public class MdbVersionDetector {

    public enum MdbVersion {
        V1_0,
        V1_7
    }

    public MdbVersion detectFromContent(String filePath, String password) {
        StringBuilder urlBuilder = new StringBuilder("jdbc:ucanaccess://").append(filePath);

        if (password != null && !password.isBlank()) {
            urlBuilder.append(";password=").append(password);
        }

        urlBuilder.append(";ignoreCase=true;memory=true;readonly=true");

        try (Connection conn = DriverManager.getConnection(urlBuilder.toString())) {
            DatabaseMetaData md = conn.getMetaData();

            // Look for the "presc" table.
            // The "TABLE" type filter ignores views or system tables.
            try (ResultSet rs = md.getTables(null, null, "presc", new String[]{"TABLE"})) {
                if (rs.next()) {
                    // "presc" table exists -> Version 1.7
                    return MdbVersion.V1_7;
                } else {
                    // "presc" table does not exist -> Version 1.0
                    return MdbVersion.V1_0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error accessing the MDB file (check the password): " + e.getMessage());
        }
    }
}