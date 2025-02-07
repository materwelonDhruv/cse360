package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.User;
import src.utils.permissions.Roles;

import java.sql.SQLException;

/**
 * The SetupAdmin class handles the setup process for creating an administrator
 * account.
 * This is intended to be used by the first user to initialize the system with
 * admin credentials.
 */
public class AdminSetupPage {

    private final AppContext context;

    public AdminSetupPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage) {
        // Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);

        Button setupButton = new Button("Setup");

        setupButton.setOnAction(_ -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            try {
                // Create a new User object with admin role and register in the database
                User user = new User(userName, password, email, Roles.ADMIN.getBit());
                context.users().create(user);
                System.out.println("Administrator setup completed.");

                // Navigate to the Welcome Login Page
                new WelcomeLoginPage().show(primaryStage, user);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, setupButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}