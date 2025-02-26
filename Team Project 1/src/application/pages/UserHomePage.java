package application.pages;

import application.framework.*;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

/**
 * This page displays a simple welcome message for the user and provides navigation.
 * It shows the user's current role and, if multiple roles exist, a dropdown to select another.
 */
@Route(MyPages.USER_HOME)
@View(title = "User Page")
public class UserHomePage extends BasePage {

    public UserHomePage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Retrieve the active user from session.
        User user = SessionContext.getActiveUser();
        if (user == null) {
            return new VBox(UIFactory.createLabel("No active user found."));
        }


        // Greeting and role display.
        Label userLabel = UIFactory.createLabel("Hello, " + user.getUserName() + "!");
        int roleInt = user.getRoles();
        Roles[] allRoles = RolesUtil.intToRoles(roleInt);
        // Assume primary role is the first one.
        Roles userCurrentRole = (allRoles.length > 0) ? allRoles[0] : null;
        Label roleLabel = UIFactory.createLabel("Role: " + userCurrentRole);

        // Create Logout and Question Display buttons.
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        Button questionDisplayButton = UIFactory.createButton("Your Homepage",
                e -> e.routeToPage(MyPages.USER_QUESTION_DISPLAY, context));

        layout.getChildren().addAll(userLabel, roleLabel, logoutButton, questionDisplayButton);

        // If more than one role, add a role selection dropdown and a Go button.
        if (allRoles.length > 1) {
            MenuButton roleMenu = new MenuButton("Select Role");
            final Roles[] selectedRole = new Roles[1];
            for (Roles rol : allRoles) {
                if (!rol.equals(userCurrentRole)) {
                    MenuItem roleItem = new MenuItem(rol.toString());
                    roleItem.setOnAction(e -> {
                        selectedRole[0] = rol;
                        roleMenu.setText(rol.toString());
                    });
                    roleMenu.getItems().add(roleItem);
                }
            }
            Button goButton = UIFactory.createButton("Go", e ->
                    e.routeToPage(
                            (selectedRole[0] != null && RolesUtil.hasRole(selectedRole, Roles.ADMIN))
                                    ? MyPages.ADMIN_HOME
                                    : MyPages.USER_HOME,
                            context
                    )
            );
            layout.getChildren().addAll(roleMenu, goButton);
        }
        return layout;
    }
}