package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.User;
import src.validators.PasswordValidator;

import java.sql.SQLException;

/**
 * InvitePage class represents the page where an admin can generate an
 **/

public class ResetPasswordPage {

    private final AppContext context;

    public ResetPasswordPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage, User user) {

        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label resetPassLabel = new Label("Reset your password ");
        resetPassLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10");

        // Input field for the user's new password
        TextField newPassField = new TextField();
        newPassField.setPromptText("Enter new password");
        newPassField.setMaxWidth(250);

        // Button to generate the invitation code
        Button resetPasswordButton = new Button("Reset Password");
        resetPasswordButton.setOnAction(event -> {
            String password = newPassField.getText();
            try {
                PasswordValidator.validatePassword(password);

            } catch (IllegalArgumentException e) {
                resetPasswordButton.setText(e.getMessage());
            }

        });

        Button backButton = new Button("Back");

        // Action for back button
        backButton.setOnAction(_ -> {
            try {
                new AdminHomePage().show(primaryStage, user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


        // Add components to layout
        layout.getChildren().addAll(resetPassLabel, newPassField, resetPasswordButton, backButton);

        // Set the scene
        Scene inviteScene = new Scene(layout, 800, 400);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invite Page");
    }
}