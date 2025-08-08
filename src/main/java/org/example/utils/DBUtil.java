// DBUtil.java
package org.example.utils;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DBUtil {
    private static final Properties queries = new Properties();

    static {
        try (InputStream input = DBUtil.class.getClassLoader()
                .getResourceAsStream("queries.properties")) {
            if (input == null) {
                throw new RuntimeException("queries.properties file not found in classpath");
            }
            queries.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading queries", e);
        }
    }


    public static String getQuery(String key) {
        String query = queries.getProperty(key);
        if (query == null) {
            throw new RuntimeException("Query not found for key: " + key);
        }
        return query;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
}