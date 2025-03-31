package application;

import application.framework.PageRouter;
import application.framework.Session;
import database.connection.DatabaseConnection;
import database.migration.SchemaManager;
import database.repository.repos.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Centralized application context for database and routing.
 */
public class AppContext {
    private static AppContext INSTANCE;

    private final Connection connection;

    private final Session session;

    // Single, shared PageRouter so all pages can navigate.
    private final PageRouter router;

    // Repositories:
    private final Users userRepository;
    private final Messages messageRepository;
    private final Invites inviteRepository;
    private final OneTimePasswords otpRepository;
    private final Questions questionRepository;
    private final Answers answerRepository;
    private final PrivateMessages privateMessagesRepository;
    private final ReadMessages readMessagesRepository;
    private final Reviews reviewsRepository;

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
        this.userRepository = new Users(connection);
        this.messageRepository = new Messages(connection);
        this.inviteRepository = new Invites(connection);
        this.otpRepository = new OneTimePasswords(connection);
        this.questionRepository = new Questions(connection);
        this.answerRepository = new Answers(connection);
        this.privateMessagesRepository = new PrivateMessages(connection);
        this.readMessagesRepository = new ReadMessages(connection);
        this.reviewsRepository = new Reviews(connection);

        // 5) Create the PageRouter ONCE, passing the main stage
        if (primaryStage != null) {
            this.router = new PageRouter(primaryStage);
        } else {
            this.router = null;
        }

        // 6) Create the Session
        this.session = new Session();
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

    public Users users() {
        return userRepository;
    }

    public Messages messages() {
        return messageRepository;
    }

    public Invites invites() {
        return inviteRepository;
    }

    public OneTimePasswords oneTimePasswords() {
        return otpRepository;
    }

    public Questions questions() {
        return questionRepository;
    }

    public Answers answers() {
        return answerRepository;
    }

    public PrivateMessages privateMessages() {
        return privateMessagesRepository;
    }

    public ReadMessages readMessages() {
        return readMessagesRepository;
    }

    public Reviews reviews() {
        return reviewsRepository;
    }

    public Connection getConnection() {
        return connection;
    }

    public Session getSession() {
        return session;
    }

    /**
     * Closes the database connection if needed.
     */
    public void closeConnection() throws SQLException {
        DatabaseConnection.closeConnection();
    }
}