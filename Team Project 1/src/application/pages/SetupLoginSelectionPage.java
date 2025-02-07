package src.application.pages;

import src.application.AppContext;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a
 * new account
 * or logging into an existing account. It provides two buttons for navigation
 * to the respective pages.
 */
public class SetupLoginSelectionPage {

    private final AppContext context;

    public SetupLoginSelectionPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage) {

        // Buttons to select Login / Setup options that redirect to respective pages
        Button setupButton = new Button("SetUp");
        Button loginButton = new Button("Login");

        setupButton.setOnAction(_ -> {
            try {
                new SetupAccountPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        loginButton.setOnAction(_ -> {
            try {
                new UserLoginPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(setupButton, loginButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
