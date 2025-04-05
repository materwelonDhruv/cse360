package application;

import application.framework.MyPages;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Main entry point for the JavaFX application.
 * <p>
 * Initializes the application context and routes to the first page or login setup based on user data.
 * </p>
 *
 * @author Dhruv
 */
public class InstantiateApp extends Application {

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application and routes based on user data.
     * <p>
     * If no users exist, routes to the first page; otherwise, routes to the login setup.
     * </p>
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1) Initialize the app context (and single router) with the Stage
            AppContext context = AppContext.getInstance(primaryStage);

            // 2) Check if any users exist
            boolean isEmpty = context.users().getAll().isEmpty();

            // 3) If empty, route to FIRST, else route to SETUP_LOGIN
            if (isEmpty) {
                context.router().navigate(MyPages.FIRST);
            } else {
                context.router().navigate(MyPages.SETUP_LOGIN);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}