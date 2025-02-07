package src.application.pages;

import src.application.AppContext;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.Invite;
import src.database.model.entities.User;
import src.utils.Helpers;
import src.validators.PasswordValidator;
import src.validators.UsernameValidator;

import java.sql.SQLException;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to
 * register.
 */
public class SetupAccountPage {

    private final AppContext context;

    // DatabaseHelper to handle database operations.
    public SetupAccountPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    /**
     * Displays the Setup Account page in the provided stage.
     *
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);

        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button setupButton = new Button("Setup");

        setupButton.setOnAction(_ -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();

            try {
                // Check if the user already exists
                if (!context.users().doesUserExist(userName)) {

                    // Validate the invitation code
                    Invite invite = context.invites().getInviteFromCode(code);
                    if (invite != null) {
                        // delete the invitation from the database
                        context.invites().delete(invite.getId());

                        // Check if the invite is less than a day old
                        if (Helpers.getCurrentTimeInSeconds() - invite.getCreatedAt() < 86400) {
                            // Create a new user and register them in the database
                            User user = new User(userName, password, email, invite.getRoles());
                            context.users().create(user);

                            // Navigate to the Welcome Login Page
                            new WelcomeLoginPage().show(primaryStage, user);
                        } else {
                            errorLabel.setText("Invitation is expired");
                        }
                    } else {
                        errorLabel.setText("Invitation code does not exist or is expired");
                    }
                } else {
                    errorLabel.setText("This userName is taken!!.. Please use another to setup an account");
                }

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, emailField, inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}