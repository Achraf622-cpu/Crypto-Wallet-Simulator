package com.crypto.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static Database INSTANCE;
    private Connection connection;

    private Database() {}

    public static synchronized Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    public void connect(String jdbcUrl) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl);
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect DB: " + e.getMessage(), e);
        }
    }

    public void connect(String jdbcUrl, String user, String password, String driverClass) {
        try {
            if (driverClass != null && driverClass.trim().length() > 0) {
                try {
                    Class.forName(driverClass.trim());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("JDBC driver not found: " + driverClass, e);
                }
            }
            this.connection = (user != null)
                    ? DriverManager.getConnection(jdbcUrl, user, password == null ? "" : password)
                    : DriverManager.getConnection(jdbcUrl);
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect DB: " + e.getMessage(), e);
        }
    }

    public void connectFromEnvOrDefault() {
        String url = System.getenv("JDBC_URL");
        if (url != null && url.trim().length() > 0) {
            String user = System.getenv("JDBC_USER");
            String pass = System.getenv("JDBC_PASSWORD");
            String driver = System.getenv("JDBC_DRIVER");
            connect(url.trim(), user, pass, driver);
        } else {
            // Default to a local Postgres database if not provided
            connect("jdbc:postgresql://localhost:5432/crypto_wallet", "postgres", "password", "org.postgresql.Driver");
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database not connected");
        }
        return connection;
    }


}


