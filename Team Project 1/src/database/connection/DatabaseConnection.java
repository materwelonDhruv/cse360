package src.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL      = "jdbc:h2:~/FoundationDatabase";
    private static final String USER        = "sa";
    private static final String PASS        = "";

    private static Connection connection;
    private static boolean initialized = false;

    private DatabaseConnection() {}

    /**
     * Ensures a connection to H2.
     */
    public static void initialize() throws SQLException {
        if (!initialized) {
            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to database...");
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
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized || connection == null || connection.isClosed()) {
            initialize();
        }
        return connection;
    }

    /**
     * Closes the connection to the database.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }

    /**
     * Deletes all data from the database.
     */
    public static void clearDatabase() throws SQLException {
        try (var stmt = getConnection().createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            System.out.println("Database cleared.");
        }
    }
}