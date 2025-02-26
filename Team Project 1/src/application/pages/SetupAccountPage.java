package application.pages;

import application.framework.*;
import database.model.entities.Invite;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import validators.EmailValidator;
import validators.PasswordValidator;
import validators.UsernameValidator;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
@Route(MyPages.SETUP_ACCOUNT)
@View(title = "Account Setup")
public class SetupAccountPage extends BasePage {

    public SetupAccountPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Create input fields using UIFactory.
        TextField userNameField = UIFactory.createTextField("Enter userName", 250);
        PasswordField passwordField = UIFactory.createPasswordField("Enter Password", 250);
        TextField emailField = UIFactory.createTextField("Enter Email", 250);
        TextField firstNameField = UIFactory.createTextField("Enter first name", 250);
        TextField lastNameField = UIFactory.createTextField("Enter last name", 250);
        TextField inviteCodeField = UIFactory.createTextField("Enter InvitationCode", 250);

        // Error label.
        Label errorLabel = UIFactory.createLabel("", null, null);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Back button navigates to SetupLoginSelectionPage.
        Button backButton = UIFactory.createButton("Back", e -> {
            try {
                context.router().navigate(MyPages.SETUP_LOGIN);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Setup button to process registration.
        Button setupButton = UIFactory.createButton("Setup", e -> {
            String userName = userNameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();


            // Validate username.
            try {
                UsernameValidator.validateUserName(userName);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }
            // Validate password.
            try {
                PasswordValidator.validatePassword(password);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }
            // Validate email.
            try {
                EmailValidator.validateEmail(email);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }

            // Check if the user already exists.
            if (!context.users().doesUserExist(userName)) {
                // Look up the invitation.
                Invite invite = context.invites().findInvite(code);
                if (invite != null) {
                    User user = new User(userName, firstName, lastName, password, email, invite.getRoles());
                    context.users().create(user);
                    SessionContext.setActiveUser(user);
                    context.router().navigate(MyPages.WELCOME_LOGIN);
                } else {
                    errorLabel.setText("Invitation code does not exist or is expired");
                }
            } else {
                errorLabel.setText("This userName is taken! Please use another.");
            }
        });

        layout.getChildren().addAll(userNameField, firstNameField, lastNameField, passwordField, emailField, inviteCodeField, setupButton, errorLabel, backButton);
        return layout;
    }
}