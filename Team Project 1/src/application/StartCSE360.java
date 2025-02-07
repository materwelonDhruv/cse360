package src.application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import src.application.pages.FirstPage;
import src.application.pages.SetupLoginSelectionPage;

import java.sql.SQLException;

public class StartCSE360 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Force the AppContext to initialize everything
            AppContext context = AppContext.getInstance();

            // Delete all expired invitations
            context.invites().deleteExpired();

            VBox root = new VBox();
            Scene scene = new Scene(root, 400, 300);

            primaryStage.setTitle("My JavaFX App");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Example: check if any users exist
            boolean isEmpty = context.users().getAll().isEmpty();

            if (isEmpty) {
                new FirstPage().show(primaryStage);
            } else {
                new SetupLoginSelectionPage().show(primaryStage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}