package application;

import application.framework.MyPages;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Main entry point for the JavaFX application.
 * Initializes AppContext with a single Stage and decides first route.
 */
public class InstantiateApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

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