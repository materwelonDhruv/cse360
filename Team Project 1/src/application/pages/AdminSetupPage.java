package application.pages;

import application.framework.BasePage;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import validators.EmailValidator;
import validators.PasswordValidator;
import validators.UsernameValidator;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 * This page is used to initialize the system with admin credentials.
 */
@src.application.framework.Route(src.application.framework.MyPages.ADMIN_SETUP)
@src.application.framework.View(title = "Administrator Setup")
public class AdminSetupPage extends BasePage {

    public AdminSetupPage() {
        super();
    }

    @Override
    public Pane createView() {
        // Create a vertically aligned layout with consistent padding and centering.
        VBox layout = new VBox(10);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        // Create input fields using UIFactory.
        TextField userNameField = src.application.framework.UIFactory.createTextField("Enter Admin userName", 250);
        TextField firstNameField = src.application.framework.UIFactory.createTextField("Enter Admin first name", 250);
        TextField lastNameField = src.application.framework.UIFactory.createTextField("Enter Admin last name", 250);
        TextField emailField = src.application.framework.UIFactory.createTextField("Enter Email", 250);
        // For a real password field, consider using UIFactory.createPasswordField(...).
        TextField passwordField = src.application.framework.UIFactory.createTextField("Enter Password", 250);
        TextField inviteCodeField = src.application.framework.UIFactory.createTextField("Enter Invitation Code", 250);

        // Create an error label.
        Label errorLabel = src.application.framework.UIFactory.createLabel("", null, null);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Create Setup and Back buttons.
        Button setupButton = src.application.framework.UIFactory.createButton("Setup", e -> {
            String userName = userNameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();

            // Validate user input.
            try {
                UsernameValidator.validateUserName(userName);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }
            try {
                PasswordValidator.validatePassword(password);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }
            try {
                EmailValidator.validateEmail(email);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }

            // Check if the user already exists.
            if (!context.users().doesUserExist(userName)) {
                // Find the invitation code in the database.
                var invite = context.invites().findInvite(code);
                if (invite != null) {
                    // Create a new admin user.
                    User adminUser = new User(userName, firstName, lastName, password, email, Roles.ADMIN.getBit());
                    context.users().create(adminUser);
                    // Set active user in session.
                    src.application.framework.SessionContext.setActiveUser(adminUser);
                    // Navigate to welcome page.
                    context.router().navigate(src.application.framework.MyPages.WELCOME_LOGIN);
                } else {
                    errorLabel.setText("Invitation code does not exist or is expired");
                }
            } else {
                errorLabel.setText("This userName is taken! Please use another.");
            }
        });

        Button backButton = src.application.framework.UIFactory.createButton("Back", e -> {
            context.router().navigate(src.application.framework.MyPages.SETUP_LOGIN);
        });

        layout.getChildren().addAll(userNameField, firstNameField, lastNameField, passwordField, emailField, inviteCodeField, setupButton, errorLabel, backButton);
        return layout;
    }
}