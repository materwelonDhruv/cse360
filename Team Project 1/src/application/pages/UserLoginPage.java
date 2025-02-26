package application.pages;

import application.framework.BasePage;
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
@src.application.framework.Route(src.application.framework.MyPages.USER_LOGIN)
@src.application.framework.View(title = "User Login")
public class UserLoginPage extends BasePage {

    public UserLoginPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        TextField userNameField = src.application.framework.UIFactory.createTextField("Enter userName", 250);
        PasswordField passwordField = src.application.framework.UIFactory.createPasswordField("Enter Password", 250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Login button using UIFactory
        var loginButton = src.application.framework.UIFactory.createButton("Login", e -> {
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
                    src.application.framework.SessionContext.setActiveUser(user);
                    context.router().navigate(src.application.framework.MyPages.WELCOME_LOGIN);
                    return;
                } else {
                    errorLabel.setText("Invalid Password or OTP!");
                    return;
                }
            }

            // Valid login: set active user and navigate
            src.application.framework.SessionContext.setActiveUser(user);
            context.router().navigate(src.application.framework.MyPages.WELCOME_LOGIN);
        });

        // Back button using UIFactory
        var backButton = src.application.framework.UIFactory.createButton("Back", e -> {
            try {
                context.router().navigate(src.application.framework.MyPages.SETUP_LOGIN);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, backButton);
        return layout;
    }
}