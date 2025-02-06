package src.application;

import java.sql.Connection;
import java.sql.SQLException;

import src.database.connection.DatabaseConnection;
import src.database.migration.SchemaManager;
import src.database.repository.repos.*;

public class AppContext {
    private static AppContext INSTANCE;

    private final Connection connection;

    private final Users userRepository;
    private final Invites inviteRepository;
    private final OneTimePasswords otpRepository;

    public AppContext() throws SQLException {
        // 1) Initialize the DB connection
        DatabaseConnection.initialize();
        this.connection = DatabaseConnection.getConnection();

        // 2) Run any migrations or schema sync if necessary
        new SchemaManager().syncDatabases(connection);

        // 3) Initialize repositories with the connection (injected)
        this.userRepository = new Users(connection);
        this.inviteRepository = new Invites(connection);
        this.otpRepository = new OneTimePasswords(connection);
    }

    /**
     * Global access point to the AppContext.
     */
    public static synchronized AppContext getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new AppContext();
        }
        return INSTANCE;
    }

    // -- Getters for each repository:

    public Users users() {
        return userRepository;
    }

    public Invites invites() {
        return inviteRepository;
    }

    public OneTimePasswords oneTimePasswords() {
        return otpRepository;
    }

    // -- Getters for the connection:
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the connection to the database.
     * @throws SQLException if the connection cannot be closed.
     */
    public void closeConnection() throws SQLException {
        DatabaseConnection.closeConnection();
    }
}