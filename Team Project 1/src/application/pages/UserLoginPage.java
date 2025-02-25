package application.pages;

import application.framework.*;
import database.model.entities.User;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the appropriate page
 * upon successful login.
 */
@Route(MyPages.USER_LOGIN)
@View(title = "User Login")
public class UserLoginPage extends BasePage {

    public UserLoginPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        TextField userNameField = UIFactory.createTextField("Enter userName", 250);
        PasswordField passwordField = UIFactory.createPasswordField("Enter Password", 250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Login button using UIFactory
        var loginButton = UIFactory.createButton("Login", e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();

            User user = context.users().getByUsername(userName);
            if (user == null) {
                errorLabel.setText("Invalid User!");
                return;
            }

            boolean userValid = context.users().validateLogin(userName, password);
            if (!userValid) {
                // Try one-time password
                var otpRepo = context.oneTimePasswords();
                boolean otpValid = otpRepo.check(user.getId(), password);
                if (otpValid) {
                    SessionContext.setActiveUser(user);
                    context.router().navigate(MyPages.WELCOME_LOGIN);
                    return;
                } else {
                    errorLabel.setText("Invalid Password or OTP!");
                    return;
                }
            }

            // Valid login: set active user and navigate
            SessionContext.setActiveUser(user);
            context.router().navigate(MyPages.WELCOME_LOGIN);
        });

        // Back button using UIFactory
        var backButton = UIFactory.createButton("Back", e -> {
            try {
                context.router().navigate(MyPages.SETUP_LOGIN);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, backButton);
        return layout;
    }
}