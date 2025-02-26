package application.pages;

import application.framework.*;
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
@Route(MyPages.ADMIN_SETUP)
@View(title = "Administrator Setup")
public class AdminSetupPage extends BasePage {

    public AdminSetupPage() {
        super();
    }

    @Override
    public Pane createView() {
        // Create a vertically aligned layout with consistent padding and centering.
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Create input fields using UIFactory.
        TextField userNameField = UIFactory.createTextField("Enter Admin userName", 250);
        TextField firstNameField = UIFactory.createTextField("Enter Admin first name", 250);
        TextField lastNameField = UIFactory.createTextField("Enter Admin last name", 250);
        TextField emailField = UIFactory.createTextField("Enter Email", 250);
        // For a real password field, consider using UIFactory.createPasswordField(...).
        TextField passwordField = UIFactory.createTextField("Enter Password", 250);

        // Create an error label.
        Label errorLabel = UIFactory.createLabel("", null, null);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Create Setup and Back buttons.
        Button setupButton = UIFactory.createButton("Setup", e -> {
            String userName = userNameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

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

            User adminUser = new User(userName, firstName, lastName, password, email, Roles.ADMIN.getBit());
            context.users().create(adminUser);
            // Set active user in session.
            SessionContext.setActiveUser(adminUser);
            // Navigate to welcome page.
            context.router().navigate(MyPages.WELCOME_LOGIN);

        });

        Button backButton = UIFactory.createButton("Back", e -> {
            context.router().navigate(MyPages.SETUP_LOGIN);
        });

        layout.getChildren().addAll(userNameField, firstNameField, lastNameField, passwordField, emailField, setupButton, errorLabel, backButton);
        return layout;
    }
}