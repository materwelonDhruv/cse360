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
 * Centralized application context for managing the database, routing, and repositories.
 * <p>
 * This class is responsible for initializing the database connection, managing the session,
 * and providing access to various repositories (e.g., {@link Users}, {@link Messages}, etc.).
 * It also creates a single {@link PageRouter} for navigating between different pages.
 * </p>
 *
 * @author Dhruv
 * @see DatabaseConnection
 * @see PageRouter
 * @see Session
 */
public class AppContext {
    private static AppContext INSTANCE;

    private final Connection connection;

    private final Session session;

    // Single, shared PageRouter for navigation across pages
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
    private final ReviewerRequests reviewerRequestsRepository;

    /**
     * Private constructor sets up the DB connection, runs migrations, and
     * creates the single PageRouter for the entire app.
     *
     * @param primaryStage The main JavaFX stage that the router will control.
     * @throws SQLException if DB initialization fails
     */
    private AppContext(Stage primaryStage) throws SQLException {
        // Initialize DB and schema
        DatabaseConnection.initialize();
        SchemaManager schemaManager = new SchemaManager();
        this.connection = DatabaseConnection.getConnection();

        // Sync DB schema and inspect tables
        schemaManager.syncTables(connection);
        schemaManager.inspectTables(connection);

        // Build repositories
        this.userRepository = new Users(connection);
        this.messageRepository = new Messages(connection);
        this.inviteRepository = new Invites(connection);
        this.otpRepository = new OneTimePasswords(connection);
        this.questionRepository = new Questions(connection);
        this.answerRepository = new Answers(connection);
        this.privateMessagesRepository = new PrivateMessages(connection);
        this.readMessagesRepository = new ReadMessages(connection);
        this.reviewsRepository = new Reviews(connection);
        this.reviewerRequestsRepository = new ReviewerRequests(connection);

        // Create the PageRouter ONCE, passing the main stage
        this.router = (primaryStage != null) ? new PageRouter(primaryStage) : null;

        // Create the session
        this.session = new Session();
    }

    /**
     * Returns a singleton instance of AppContext, initialized with a primary stage.
     *
     * @param primaryStage The main JavaFX stage.
     * @return The singleton instance of AppContext.
     * @throws SQLException If an error occurs during initialization.
     */
    public static synchronized AppContext getInstance(Stage primaryStage) throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new AppContext(primaryStage);
        }
        return INSTANCE;
    }

    /**
     * Returns a singleton instance of AppContext without requiring a primary stage.
     *
     * @return The singleton instance of AppContext.
     * @throws SQLException If an error occurs during initialization.
     */
    public static synchronized AppContext getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new AppContext(null);
        }
        return INSTANCE;
    }

    /**
     * Returns the single PageRouter for page navigation.
     *
     * @return The PageRouter instance.
     * @throws IllegalStateException if the PageRouter was not initialized.
     */
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

    public ReviewerRequests reviewerRequests() {
        return reviewerRequestsRepository;
    }

    /**
     * Returns the current database connection.
     *
     * @return The database connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns the current session.
     *
     * @return The session object.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Closes the database connection if needed.
     *
     * @throws SQLException If an error occurs while closing the connection.
     */
    public void closeConnection() throws SQLException {
        DatabaseConnection.closeConnection();
    }
}