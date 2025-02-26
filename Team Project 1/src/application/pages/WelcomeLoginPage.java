package application.pages;

import application.framework.*;
import database.model.entities.User;
import database.repository.DataAccessException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.sql.SQLException;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
@Route(MyPages.WELCOME_LOGIN)
@View(title = "Role Select")
public class WelcomeLoginPage extends BasePage {

    public WelcomeLoginPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(5);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Get the active user from session
        User user = SessionContext.getActiveUser();
        String username = (user != null) ? user.getUserName() : "Guest";

        Label welcomeLabel = UIFactory.createLabel("Welcome " + username + "!!");

        // Get all roles assigned to the user
        assert user != null; // TODO: Handle null user
        int roleInt = user.getRoles();
        Roles[] roles = RolesUtil.intToRoles(roleInt);

        // Create Continue and Quit buttons using UIFactory
        Button continueButton = UIFactory.createButton("Continue to your page",
                e -> e.routeToPage(
                        (roles.length == 1 && RolesUtil.hasRole(roles, Roles.ADMIN) ? MyPages.ADMIN_HOME : MyPages.USER_HOME),
                        context
                )
        );
        Button quitButton = UIFactory.createButton("Quit",
                e -> e.onAction(a -> {
                    try {
                        context.closeConnection();
                    } catch (SQLException ex) {
                        throw new DataAccessException("Cannot close in WelcomePage", ex);
                    }
                    Platform.exit();
                }));

        // For multiple roles, use a dropdown (MenuButton) for selection
        MenuButton roleMenu;
        final Roles[] selectedRole = new Roles[1];
        if (roles.length > 1) {
            roleMenu = new MenuButton("Select Role");
            for (Roles role : roles) {
                MenuItem roleItem = new MenuItem(role.toString());
                roleItem.setOnAction(e -> {
                    selectedRole[0] = role;
                    roleMenu.setText(role.toString());
                });
                roleMenu.getItems().add(roleItem);
            }
            // Set continue button to use the selected role
            continueButton.setOnAction(e -> {
                if (RolesUtil.hasRole(selectedRole, Roles.ADMIN)) {
                    context.router().navigate(MyPages.ADMIN_HOME);
                } else if (selectedRole[0] != null) {
                    context.router().navigate(MyPages.USER_HOME);
                }
            });
        } else {
            roleMenu = null;
        }

        layout.getChildren().add(welcomeLabel);
        layout.getChildren().add(continueButton);
        if (roleMenu != null) {
            layout.getChildren().add(roleMenu);
        }
        layout.getChildren().add(quitButton);

        return layout;
    }
}