package application.pages.admin;

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
 *
 * @author Mike
 */
@Route(MyPages.ADMIN_SETUP)
@View(title = "Administrator Setup")
public class AdminSetupPage extends BasePage {

    /**
     * Constructor using BasePage
     */
    public AdminSetupPage() {
        super();
    }

    /**
     * @return layout
     * Creates layout for Admin Home page, including buttons to traverse to other pages
     */
    @Override
    public Pane createView() {
        // Create a vertically aligned layout with consistent padding and centering.
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Create input fields using UIFactory.
        TextField userNameField = UIFactory.createTextField("Enter Admin userName",
                f -> f.maxWidth(250).minChars(6).maxChars(18));
        TextField firstNameField = UIFactory.createTextField("Enter Admin first name",
                f -> f.maxWidth(250).minChars(1).maxChars(50));
        TextField lastNameField = UIFactory.createTextField("Enter Admin last name",
                f -> f.maxWidth(250).minChars(1).maxChars(50));
        TextField emailField = UIFactory.createTextField("Enter Email",
                f -> f.maxWidth(250).minChars(5).maxChars(50));
        TextField passwordField = UIFactory.createPasswordField("Enter Password",
                p -> p.maxWidth(250).minChars(8).maxChars(30));

        // Create an error label.
        Label errorLabel = UIFactory.createLabel("",
                l -> l.style(DesignGuide.ERROR_LABEL));

        // Create Setup and Back buttons.
        // Create Setup button using UIFactory with reduced nesting
        Button setupButton = UIFactory.createButton("Setup",
                e -> e.onAction(a -> handleSetup(userNameField, firstNameField, lastNameField, passwordField, emailField, errorLabel))
        );

        Button backButton = UIFactory.createBackButton(context);

        layout.getChildren().addAll(userNameField, firstNameField, lastNameField, passwordField, emailField, setupButton, errorLabel, backButton);
        return layout;
    }

    /**
     * @param userNameField  TextField for username
     * @param firstNameField TextField for firstName
     * @param lastNameField  TextField for lastName
     * @param passwordField  TextField for password
     * @param emailField     TextField for email
     * @param errorLabel     Label for error display
     *                       private message that handles Javadoc setup of listed elements
     */
    private void handleSetup(TextField userNameField, TextField firstNameField, TextField lastNameField,
                             TextField passwordField, TextField emailField, Label errorLabel) {
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
        context.getSession().setActiveUser(adminUser);
        context.getSession().setCurrentRole(Roles.ADMIN);
        // Navigate to Admin Home Page
        context.router().navigate(MyPages.ADMIN_HOME);
    }
}