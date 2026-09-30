package com.bank.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton class that manages database configuration
 * and hands out JDBC connections.
 */
public class DBConnection {

    private static DBConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DBConnection() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "db.properties not found in classpath. " +
                        "Copy db.properties.example to db.properties and fill it in.");
            }

            Properties props = new Properties();
            props.load(input);

            this.url      = props.getProperty("db.url");
            this.user     = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new IllegalStateException("db.properties is missing required keys.");
            }

            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DBConnection", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}