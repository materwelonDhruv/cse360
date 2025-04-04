package database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the H2 database.
 * <p>
 * This class provides methods for initializing, retrieving, closing, clearing, and resetting
 * the database connection. It uses a singleton-like pattern to ensure a single connection is
 * maintained throughout the application's lifecycle.
 * </p>
 *
 * <p>
 * The default database URL is {@code jdbc:h2:~/FoundationDatabase}, but it can be overridden
 * by setting the {@code db.url} system property.
 * </p>
 *
 * <strong>Warning:</strong> The {@code clearDatabase()} method drops all objects from the database.
 *
 * @author Dhruv
 */
public class DatabaseConnection {

    private static final String DEFAULT_DB_URL = "jdbc:h2:~/FoundationDatabase";
    private static final String DB_URL = System.getProperty("db.url", DEFAULT_DB_URL);
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static Connection connection;
    private static boolean initialized = false;

    private DatabaseConnection() {
    }

    /**
     * Ensures a connection to H2.
     * <p>
     * Initializes the database connection if it has not already been established.
     * Attempts to load the H2 JDBC driver and connects to the database using the specified URL.
     * </p>
     *
     * @throws SQLException if the driver is not found or the connection fails.
     */
    public static void initialize() throws SQLException {
        if (!initialized) {
            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to database with URL: " + DB_URL);
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected to database.");

                initialized = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("H2 Driver not found.", e);
            }
        }
    }

    /**
     * Returns a valid, initialized connection.
     * <p>
     * Ensures that a connection is established before returning it. If the connection is closed
     * or has not been initialized, it attempts to reinitialize it.
     * </p>
     *
     * @return The active database connection.
     * @throws SQLException if the connection cannot be established.
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized || connection == null || connection.isClosed()) {
            initialize();
        }
        return connection;
    }

    /**
     * Closes the connection to the database.
     * <p>
     * If a connection is established, it closes the connection and outputs a message indicating
     * the connection has been closed.
     * </p>
     *
     * @throws SQLException if an error occurs while closing the connection.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }

    /**
     * Deletes all data from the database.
     * <p>
     * This operation executes a {@code DROP ALL OBJECTS} statement, effectively removing all
     * tables, indexes, views, and other objects from the database.
     * </p>
     *
     * <strong>Warning:</strong> This operation is irreversible and will destroy all data in the database.
     *
     * @throws SQLException if an error occurs during the deletion process.
     */
    public static void clearDatabase() throws SQLException {
        try (var stmt = getConnection().createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            System.out.println("Database cleared.");
        }
    }

    /**
     * Resets the connection to null and sets initialized to false.
     * <p>
     * This method effectively disables the current connection, forcing a new one to be established
     * when {@code getConnection()} is next called.
     * </p>
     */
    public static void resetConnection() {
        connection = null;
        initialized = false;
    }
}