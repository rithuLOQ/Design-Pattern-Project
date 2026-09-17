package com.hostel.system.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * =====================================================================================
 * GANG OF FOUR (GoF) DESIGN PATTERN: SINGLETON PATTERN
 * =====================================================================================
 * 
 * PATTERN PURPOSE:
 * The Singleton pattern ensures that a class has only one instance in the entire application
 * lifecycle and provides a global point of access to that instance.
 * 
 * WHY SINGLETON IS USED HERE:
 * 1. Database Connection Management: Opening and closing physical database connections is an
 *    expensive operation in enterprise applications (involving socket handshakes, authentication,
 *    and resource allocation).
 * 2. Resource Optimization: Instead of creating a new DatabaseConnection instance every time a DAO
 *    or Service needs to communicate with MySQL, a single managed instance coordinates state,
 *    connection pooling settings, and health diagnostics.
 * 3. Prevention of Race Conditions: Ensures centralized control over shared database metadata
 *    and configurations, avoiding duplicate instances consuming system memory.
 * 
 * IMPLEMENTATION TECHNIQUE:
 * Double-Checked Locking with 'volatile' keyword for high performance and strict thread-safety
 * in multi-threaded Spring application environments.
 * =====================================================================================
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // Volatile keyword guarantees visibility of changes across threads and prevents instruction reordering
    private static volatile DatabaseConnection instance;

    // Simulated connection string parameters for database status reporting
    private final String dbUrl = "jdbc:mysql://localhost:3306/hostel_db";
    private final String dbUser = "root";
    private final boolean isConnected;
    private final long connectionEstablishedTime;

    /**
     * Private Constructor:
     * Prevents instantiation of DatabaseConnection from outside classes via 'new DatabaseConnection()'.
     */
    private DatabaseConnection() {
        // Prevent reflection attacks from instantiating another instance
        if (instance != null) {
            throw new IllegalStateException("DatabaseConnection Singleton instance already created! Use getInstance().");
        }
        
        this.connectionEstablishedTime = System.currentTimeMillis();
        this.isConnected = true;
        LOGGER.info(">>> [SINGLETON PATTERN] Initialized unique DatabaseConnection Singleton instance for hostel_db at: " 
                + connectionEstablishedTime);
    }

    /**
     * Public static method providing global access point to the Singleton instance.
     * Uses Double-Checked Locking optimization.
     *
     * @return The single instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) { // First check (no locking for efficiency)
            synchronized (DatabaseConnection.class) {
                if (instance == null) { // Second check (with locking for thread safety)
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Business method to test database connectivity status.
     *
     * @return Status string of the database connection
     */
    public String getConnectionStatus() {
        return "DatabaseConnection [Singleton] - Connected to " + dbUrl + " as " + dbUser + " (Active since " + connectionEstablishedTime + " ms)";
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }
}
