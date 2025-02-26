package application.pages;

import application.framework.*;
import database.model.entities.User;
import javafx.scene.control.Button;
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

        TextField userNameField = UIFactory.createTextField("Enter userName",
                f -> f.maxWidth(250).minChars(6).maxChars(18));
        PasswordField passwordField = UIFactory.createPasswordField("Enter Password",
                p -> p.maxWidth(250).minChars(8).maxChars(30));

        Label errorLabel = UIFactory.createLabel("",
                l -> l.style("-fx-text-fill: red; -fx-font-size: 12px;"));

        // Login button using UIFactory
        // Login button using UIFactory
        Button loginButton = UIFactory.createButton("Login",
                e -> e.onAction(a -> handleLogin(userNameField, passwordField, errorLabel))
        );

        // Back button using UIFactory
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.SETUP_LOGIN, context));

        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, backButton);
        return layout;
    }

    private void handleLogin(TextField userNameField, PasswordField passwordField, Label errorLabel) {
        String userName = userNameField.getText();
        String password = passwordField.getText();

        User user = context.users().getByUsername(userName);
        if (user == null) {
            errorLabel.setText("Invalid User!");
            return;
        }

        if (!context.users().validateLogin(userName, password)) {
            // Try one-time password
            var otpRepo = context.oneTimePasswords();
            if (otpRepo.check(user.getId(), password)) {
                SessionContext.setActiveUser(user);
                context.router().navigate(MyPages.WELCOME_LOGIN);
            } else {
                errorLabel.setText("Invalid Password or OTP!");
            }
            return;
        }

        // Valid login: set active user and navigate
        SessionContext.setActiveUser(user);
        context.router().navigate(MyPages.WELCOME_LOGIN);
    }
}