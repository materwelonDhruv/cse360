package application;

import application.framework.PageRouter;
import database.connection.DatabaseConnection;
import database.migration.SchemaManager;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Centralized application context for database and routing.
 */
public class AppContext {
    private static AppContext INSTANCE;

    private final Connection connection;

    // Single, shared PageRouter so all pages can navigate.
    private final PageRouter router;

    // Repositories:
    private final database.repository.repos.Users userRepository;
    private final database.repository.repos.Messages messageRepository;
    private final database.repository.repos.Invites inviteRepository;
    private final database.repository.repos.OneTimePasswords otpRepository;
    private final database.repository.repos.Questions questionRepository;
    private final database.repository.repos.Answers answerRepository;
    private final database.repository.repos.PrivateMessages privateMessagesRepository;
    private final database.repository.repos.ReadMessages readMessagesRepository;

    /**
     * Private constructor sets up the DB connection, runs migrations, and
     * creates the single PageRouter for the entire app.
     *
     * @param primaryStage The main JavaFX stage that the router will control.
     * @throws SQLException if DB initialization fails
     */
    private AppContext(Stage primaryStage) throws SQLException {
        // 1) Initialize DB and schema
        DatabaseConnection.initialize();
        SchemaManager schemaManager = new SchemaManager();
        this.connection = DatabaseConnection.getConnection();

        // 2) Sync DB schema
        schemaManager.syncTables(connection);
        // 3) Inspect tables
        schemaManager.inspectTables(connection);

        // 4) Build repositories
        this.userRepository = new database.repository.repos.Users(connection);
        this.messageRepository = new database.repository.repos.Messages(connection);
        this.inviteRepository = new database.repository.repos.Invites(connection);
        this.otpRepository = new database.repository.repos.OneTimePasswords(connection);
        this.questionRepository = new database.repository.repos.Questions(connection);
        this.answerRepository = new database.repository.repos.Answers(connection);
        this.privateMessagesRepository = new database.repository.repos.PrivateMessages(connection);
        this.readMessagesRepository = new database.repository.repos.ReadMessages(connection);

        // 5) Create the PageRouter ONCE, passing the main stage
        if (primaryStage != null) {
            this.router = new PageRouter(primaryStage);
        } else {
            this.router = null;
        }
    }

    /**
     * Global access to AppContext with a single Stage.
     * If not initialized, create it. Otherwise, return existing.
     */
    public static synchronized AppContext getInstance(Stage primaryStage) throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new AppContext(primaryStage);
        }
        return INSTANCE;
    }

    /**
     * Global access to AppContext without a primary stage.
     * If not initialized, create it. Otherwise, return existing.
     */
    public static synchronized AppContext getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new AppContext(null);
        }
        return INSTANCE;
    }

    // Access to the single PageRouter
    public PageRouter router() {
        if (router == null) {
            throw new IllegalStateException("PageRouter is not available. AppContext was initialized without a primary stage.");
        }

        return router;
    }

    // Repositories:

    public database.repository.repos.Users users() {
        return userRepository;
    }

    public database.repository.repos.Messages messages() {
        return messageRepository;
    }

    public database.repository.repos.Invites invites() {
        return inviteRepository;
    }

    public database.repository.repos.OneTimePasswords oneTimePasswords() {
        return otpRepository;
    }

    public database.repository.repos.Questions questions() {
        return questionRepository;
    }

    public database.repository.repos.Answers answers() {
        return answerRepository;
    }

    public database.repository.repos.PrivateMessages privateMessages() {
        return privateMessagesRepository;
    }

    public database.repository.repos.ReadMessages readMessages() {
        return readMessagesRepository;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection if needed.
     */
    public void closeConnection() throws SQLException {
        DatabaseConnection.closeConnection();
    }
}