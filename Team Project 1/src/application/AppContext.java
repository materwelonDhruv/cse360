package src.application;

import src.database.connection.DatabaseConnection;
import src.database.migration.SchemaManager;
import src.database.repository.repos.*;

import java.sql.Connection;
import java.sql.SQLException;

public class AppContext {
    private static AppContext INSTANCE;

    private final Connection connection;

    private final Users userRepository;
    private final Invites inviteRepository;
    private final OneTimePasswords otpRepository;
    private final Questions questionRepository;
    private final Answers answerRepository;

    public AppContext() throws SQLException {
        // 1) Initialize the DB connection and schema manager
        DatabaseConnection.initialize();
        SchemaManager schemaManager = new SchemaManager();
        this.connection = DatabaseConnection.getConnection();

        // 2) Run any migrations or schema sync if necessary
        schemaManager.syncTables(connection);

        // 3) Inspect database tables
        schemaManager.inspectTables(connection);

        // 4) Initialize repositories with the connection (injected)
        this.userRepository = new Users(connection);
        this.inviteRepository = new Invites(connection);
        this.otpRepository = new OneTimePasswords(connection);
        this.questionRepository = new Questions(connection);
        this.answerRepository = new Answers(connection);
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

    public Questions questions() {
        return questionRepository;
    }

    public Answers answers() {
        return answerRepository;
    }

    /**
     * Closes the connection to the database.
     *
     * @throws SQLException if the connection cannot be closed.
     */
    public void closeConnection() throws SQLException {
        DatabaseConnection.closeConnection();
    }
}