package development;

import database.connection.DatabaseConnection;

/**
 * Utility class for clearing the entire database.
 * <p>
 * This class provides a main method that initializes the database connection and clears all data
 * from the database. It is intended for development or testing purposes where resetting the
 * database is necessary.
 * </p>
 *
 * <p><strong>Warning:</strong> This operation is destructive and will remove all data from the database.</p>
 *
 * @author Dhruv
 * @see DatabaseConnection
 */
public class ClearDatabase {

    /**
     * Main method for clearing the database.
     * <p>
     * Initializes the database connection and calls the {@code clearDatabase()} method
     * to remove all entries from the database.
     * </p>
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            DatabaseConnection.initialize();
            DatabaseConnection.clearDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}