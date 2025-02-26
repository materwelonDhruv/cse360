package application.pages;

import application.framework.BasePage;
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
@src.application.framework.Route(src.application.framework.MyPages.WELCOME_LOGIN)
@src.application.framework.View(title = "Role Select")
public class WelcomeLoginPage extends BasePage {

    public WelcomeLoginPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(5);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        // Get the active user from session
        User user = src.application.framework.SessionContext.getActiveUser();
        String username = (user != null) ? user.getUserName() : "Guest";

        Label welcomeLabel = src.application.framework.UIFactory.createLabel("Welcome " + username + "!!", src.application.framework.DesignGuide.TITLE_LABEL, null);

        // Get all roles assigned to the user
        int roleInt = user.getRoles();
        Roles[] roles = RolesUtil.intToRoles(roleInt);

        // Create Continue and Quit buttons using UIFactory
        Button continueButton = src.application.framework.UIFactory.createButton("Continue to your page", null);
        Button quitButton = src.application.framework.UIFactory.createButton("Quit", e -> {
            try {
                context.closeConnection();
            } catch (SQLException ex) {
                throw new DataAccessException("Cannot close in WelcomePage", ex);
            }
            Platform.exit();
        });

        // If only one role is assigned, route accordingly
        if (roles.length == 1) {
            if (RolesUtil.hasRole(roles, Roles.ADMIN)) {
                continueButton.setOnAction(e -> context.router().navigate(src.application.framework.MyPages.ADMIN_HOME));
            } else {
                continueButton.setOnAction(e -> context.router().navigate(src.application.framework.MyPages.USER_HOME));
            }
        }

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
                    context.router().navigate(src.application.framework.MyPages.ADMIN_HOME);
                } else if (selectedRole[0] != null) {
                    context.router().navigate(src.application.framework.MyPages.USER_HOME);
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