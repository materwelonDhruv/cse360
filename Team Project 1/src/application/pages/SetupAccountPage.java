package application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import application.AppContext;
import database.model.entities.Invite;
import database.model.entities.User;
import validators.EmailValidator;
import validators.PasswordValidator;
import validators.UsernameValidator;

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

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        firstNameField.setMaxWidth(250);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");
        lastNameField.setMaxWidth(250);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);

        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        //Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            try {
                new SetupLoginSelectionPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Button setupButton = new Button("Setup");

        setupButton.setOnAction(_ -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();

            try {
                //validate username
                try {
                    UsernameValidator.validateUserName(userName);
                } catch (IllegalArgumentException e) {
                    errorLabel.setText(e.getMessage());
                }
//

                //validate password
                try {
                    PasswordValidator.validatePassword(password);

                } catch (IllegalArgumentException e) {
                    errorLabel.setText(e.getMessage());
                }

                //validate email
                try {
                    EmailValidator.validateEmail(email);

                } catch (IllegalArgumentException e) {
                    errorLabel.setText(e.getMessage());
                }

                // Check if the user already exists
                if (!context.users().doesUserExist(userName)) {
                    // Find the invitation code in the database
                    Invite invite = context.invites().findInvite(code);

                    // Validate the invitation code
                    if (invite != null) {
                        User user = new User(userName, firstName, lastName, password, email, invite.getRoles());
                        context.users().create(user);

                        // Navigate to the Welcome Login Page
                        new WelcomeLoginPage().show(primaryStage, user);
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
        layout.getChildren().addAll(userNameField, firstNameField, lastNameField, passwordField, emailField, inviteCodeField, setupButton, errorLabel, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}