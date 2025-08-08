package org.example.utils;

import java.sql.*;
import java.util.*;
import java.io.*;

public class ConnectionPool {
    private static final int MAX_POOL_SIZE = 10;
    private static final Properties props = new Properties();
    private static final Queue<Connection> connectionPool = new LinkedList<>();

    static {
        try {
            Class.forName("org.h2.Driver");
            try (InputStream input = ConnectionPool.class.getClassLoader()
                    .getResourceAsStream("db.properties")) {
                props.load(input);

                try (Connection testConn = createConnection()) {
                    try (Statement stmt = testConn.createStatement()) {
                        stmt.execute("SELECT 1 FROM users LIMIT 1");
                    } catch (SQLException e) {
                        runInitScript(testConn);
                    }
                }
                for (int i = 0; i < MAX_POOL_SIZE; i++) {
                    connectionPool.add(createConnection());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Connection pool initialization failed", e);
        }
    }
    private static void runInitScript(Connection conn) throws SQLException, IOException {
        try (InputStream is = ConnectionPool.class.getClassLoader()
                .getResourceAsStream("init.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             Statement stmt = conn.createStatement()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    sb.append(line);
                    if (line.trim().endsWith(";")) {
                        stmt.execute(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        }
    }
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
    public static Connection getConnection() {
        if (connectionPool.isEmpty()) {
            try {
                return createConnection();
            } catch (SQLException e) {
                throw new RuntimeException("Error creating connection", e);
            }
        }
        return connectionPool.poll();
    }
}