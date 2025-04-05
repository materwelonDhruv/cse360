package tests.database;

import application.AppContext;
import database.connection.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.SQLException;

/**
 * Base test class that sets up an in-memory H2 database for testing.
 * <p>
 * Each test class that requires a shared in-memory database environment should extend this class.
 * This setup ensures consistent schema initialization and teardown for all tests.
 * </p>
 *
 * <p>
 * The database URL is set to {@code "jdbc:h2:mem:TestDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"}
 * to ensure the database remains active for the entire test lifecycle.
 * </p>
 *
 * @author Dhruv
 */
public abstract class BaseDatabaseTest {

    protected static AppContext appContext;

    /**
     * Initializes the in-memory database before all tests.
     * Sets up a clean database environment by resetting connections, clearing previous data,
     * and ensuring the schema is initialized properly.
     *
     * @throws SQLException if there is an error during database initialization.
     */
    @BeforeAll
    public static void setupDatabase() throws SQLException {
        // Use a separate in‑memory DB for tests
        System.setProperty("db.url", "jdbc:h2:mem:TestDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        // Ensure a clean slate
        DatabaseConnection.resetConnection();
        DatabaseConnection.initialize();
        DatabaseConnection.clearDatabase();
        DatabaseConnection.closeConnection();
        DatabaseConnection.resetConnection();

        // Initialize AppContext => sync schema, etc.
        appContext = AppContext.getInstance();
    }

    /**
     * Cleans up the in-memory database after all tests.
     * Ensures the database is cleared and all connections are closed properly.
     *
     * @throws SQLException if there is an error during database cleanup.
     */
    @AfterAll
    public static void tearDownDatabase() throws SQLException {
        DatabaseConnection.clearDatabase();
        appContext.closeConnection();
        System.clearProperty("db.url");
    }
}