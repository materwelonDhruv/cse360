package src.tests.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import src.application.AppContext;
import src.database.connection.DatabaseConnection;

import java.sql.SQLException;

/**
 * Base test class that sets up an in-memory H2 database for testing.
 * Each test class extends this to share the same test DB environment.
 */
public abstract class BaseDatabaseTest {

    protected static AppContext appContext;

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

    @AfterAll
    public static void tearDownDatabase() throws SQLException {
        DatabaseConnection.clearDatabase();
        appContext.closeConnection();
        System.clearProperty("db.url");
    }
}
