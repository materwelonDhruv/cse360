package src.application.pages;

import src.application.AppContext;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.User;

import java.sql.SQLException;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts.
 * It validates the user's credentials and navigates to the appropriate page
 * upon successful login.
 */
public class UserLoginPage {

    private final AppContext context;

    public UserLoginPage() throws SQLException {
        this.context = AppContext.getInstance();
    }
    public void show(Stage primaryStage) {
        // Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);


        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");

        loginButton.setOnAction(_ -> {
            // Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
                boolean userValid = context.users().validateLogin(userName, password);
                if (userValid) {
                    User user = context.users().getByUsername(userName);
                    if (user != null) {

                        // Open the welcome page
                        WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage();
                        welcomeLoginPage.show(primaryStage, user);
                    } else {
                        errorLabel.setText("Error retrieving user details.");
                    }

                } else {
                    // Display an error if the login fails
                    errorLabel.setText("Error logging in");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
