package com.bank.util;

// InputStream → to read the db.properties file from classpath
import java.io.InputStream;
// Connection → represents a session with the database
import java.sql.Connection;
// DriverManager → creates connections using registered drivers
import java.sql.DriverManager;
// SQLException → checked exception thrown by JDBC operations
import java.sql.SQLException;
// Properties → key=value config loader
import java.util.Properties;

/**
 * Singleton class that manages database configuration
 * and hands out JDBC connections.
 *
 * WHY SINGLETON HERE?
 *  - We want to read db.properties exactly ONCE per JVM run.
 *  - We want a single global access point for getting connections.
 *  - Reading config repeatedly would be wasteful and error-prone.
 */
public class DBConnection {

    // ---------------------------------------------------------
    // The single instance of this class.
    // 'static' → belongs to the class, not to any object.
    // Starts as null; created lazily on first getInstance() call.
    // ---------------------------------------------------------
    private static DBConnection instance;

    // ---------------------------------------------------------
    // Config values loaded once from db.properties.
    // 'private' → encapsulation (no outside access).
    // 'final'   → set once in constructor, never changed after.
    // ---------------------------------------------------------
    private final String url;
    private final String user;
    private final String password;

    // ---------------------------------------------------------
    // PRIVATE constructor — the heart of Singleton.
    // Because it's private, no one outside this class can do
    // 'new DBConnection()'. The only way in is getInstance().
    // ---------------------------------------------------------
    private DBConnection() {
        // try-with-resources: 'input' will auto-close at end of block.
        // We read db.properties from the classpath (target/classes/).
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            // If file isn't found on classpath, fail fast with a clear message.
            if (input == null) {
                throw new IllegalStateException(
                        "db.properties not found in classpath. " +
                        "Copy db.properties.example to db.properties and fill it in.");
            }

            // Properties is a built-in Java class for .properties files.
            // props.load(input) parses key=value lines into memory.
            Properties props = new Properties();
            props.load(input);

            // Read the three keys we need from the properties file.
            // If any is missing, getProperty returns null.
            this.url      = props.getProperty("db.url");
            this.user     = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

            // Validate that all three keys were present.
            if (url == null || user == null || password == null) {
                throw new IllegalStateException("db.properties is missing required keys.");
            }

            // Register the PostgreSQL JDBC driver with DriverManager.
            // Required once per JVM before any getConnection() call.
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            // Any failure during init (IO, reflection, missing file)
            // → wrap in RuntimeException so it's loud and clear.
            // We don't want silent config failures.
            throw new RuntimeException("Failed to initialize DBConnection", e);
        }
    }

    // ---------------------------------------------------------
    // The GLOBAL ACCESS POINT for the Singleton.
    //  - static      → call as DBConnection.getInstance()
    //  - synchronized→ thread-safe; prevents double-creation
    //                  if two threads call simultaneously
    //  - lazy        → instance is created only on first call
    // ---------------------------------------------------------
    public static synchronized DBConnection getInstance() {
        // First call: instance is null → create it.
        // Every later call: instance is non-null → reuse it.
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // ---------------------------------------------------------
    // Hands out a NEW JDBC Connection every time it's called.
    // We do NOT store connections here — DriverManager and the
    // driver handle internal pooling. Caller is responsible for
    // closing the returned Connection (usually via try-with-resources).
    // ---------------------------------------------------------
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}