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

            // On cherche la table "presc"
            // Le type "TABLE" permet d'ignorer les vues ou les tables système si nécessaire
            try (ResultSet rs = md.getTables(null, null, "presc", new String[]{"TABLE"})) {
                if (rs.next()) {
                    // Si la table "presc" existe -> C'est la Version 1.7
                    return MdbVersion.V1_7;
                } else {
                    // Si la table "presc" n'existe pas -> C'est la Version 1.0
                    return MdbVersion.V1_0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'accès au fichier MDB (Vérifiez le mot de passe) : " + e.getMessage());
        }
    }
}