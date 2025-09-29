package com.crypto.app.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            // Ensure data directory exists for SQLite file URLs like jdbc:sqlite:data/app.db
            if (jdbcUrl != null && jdbcUrl.startsWith("jdbc:sqlite:")) {
                String path = jdbcUrl.substring("jdbc:sqlite:".length());
                File f = new File(path).getParentFile();
                if (f != null) f.mkdirs();
            }
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

    public void connectFromEnvOrDefaultSqlite() {
        String url = System.getenv("JDBC_URL");
        if (url != null && url.trim().length() > 0) {
            String user = System.getenv("JDBC_USER");
            String pass = System.getenv("JDBC_PASSWORD");
            String driver = System.getenv("JDBC_DRIVER");
            connect(url.trim(), user, pass, driver);
        } else {
            // Default to a local Postgres database if not provided
            connect("jdbc:postgresql://localhost:5432/crypto_wallet", "postgres", "postgres", "org.postgresql.Driver");
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database not connected");
        }
        return connection;
    }

    /
}


