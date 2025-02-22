package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.User;
import src.database.repository.repos.OneTimePasswords;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

import java.sql.SQLException;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts.
 * It validates the user's credentials and navigates to the appropriate page
 * upon successful login.
 */
public class UserLoginPage {

    private final AppContext context;

    public UserLoginPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage) {
        // Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);


        // Label to display error messages
        Label errorLabel = new Label();
        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            try {
                new SetupLoginSelectionPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");

        loginButton.setOnAction(_ -> {
            // Retrieve user's username and password
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
                User user = context.users().getByUsername(userName);
                if (user == null) {
                    errorLabel.setText("Invalid User!");
                    return;
                }

                // Check if retrieved password matches stored password
                boolean userValid = context.users().validateLogin(userName, password);
                if (!userValid) {
                    // If password does not match, try one-time password
                    OneTimePasswords otpRepo = context.oneTimePasswords();
                    boolean otpValid = otpRepo.check(user.getId(), password);
                    if (otpValid) {
                        Roles[] roles = RolesUtil.intToRoles(user.getRoles());
                        if (roles.length > 1) {
                            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage();
                            welcomeLoginPage.show(primaryStage, user);
                        } else {
                            if (RolesUtil.hasRole(roles, Roles.ADMIN)) {
                                AdminHomePage adminHomePage = new AdminHomePage();
                                adminHomePage.show(primaryStage, user);
                            } else {
                                UserHomePage userHomePage = new UserHomePage();
                                userHomePage.show(primaryStage, user, roles[0]);
                            }
                        }
                    } else {
                        //If one-time password does not match, tell user either the password or one-time password is wrong
                        errorLabel.setText("Invalid Password or OTP!");
                    }
                }

                //get all the assigned roles
                int roleInt = user.getRoles();
                Roles[] roles = RolesUtil.intToRoles(roleInt);

                // If retrieved password matches stored user password, bring user to welcome page
                if (roles.length == 1) {
                    if (RolesUtil.hasRole(roles, Roles.ADMIN)) {
                        AdminHomePage adminHomePage = new AdminHomePage();
                        adminHomePage.show(primaryStage, user);
                    } else {
                        UserHomePage userHomePage = new UserHomePage();
                        userHomePage.show(primaryStage, user, roles[0]);
                    }
                } else {
                    if (RolesUtil.hasRole(roles, Roles.ADMIN)) {
                        AdminHomePage adminHomePage = new AdminHomePage();
                        adminHomePage.show(primaryStage, user);
                    } else {
                        WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage();
                        welcomeLoginPage.show(primaryStage, user);
                    }
                }

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();

            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}