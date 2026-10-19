package com.simple_cabinet_medical.Backend.importation.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public final class MdbConnectionUtils {

    private MdbConnectionUtils() {
    }

    public static void closeQuietly(
            ResultSet resultSet,
            Statement statement
    ) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.warn(
                        "Error while closing ResultSet",
                        e
                );
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.warn(
                        "Error while closing Statement",
                        e
                );
            }
        }
    }
}
