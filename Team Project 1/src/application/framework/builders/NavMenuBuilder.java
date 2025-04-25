package application.framework.builders;

import application.AppContext;
import application.framework.MyPages;
import application.framework.UIFactory;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import utils.permissions.Roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for creating and customizing a navigation menu.
 *
 * @author Dhruv
 */
public class NavMenuBuilder {
    private final MenuButton menuButton;

    /**
     * Initializes the builder with the given application context and menu text.
     */
    public NavMenuBuilder(AppContext context, String menuText) {
        Roles currentRole = context.getSession().getCurrentRole();
        Roles[] allRoles = utils.permissions.RolesUtil.intToRoles(context.getSession().getActiveUser().getRoles());
        List<Roles> menuRoles = new ArrayList<>();
        if (currentRole == null) {
            menuRoles.addAll(Arrays.asList(allRoles));
        } else {
            for (Roles role : allRoles) {
                if (!role.equals(currentRole)) {
                    menuRoles.add(role);
                }
            }
        }

        menuButton = new MenuButton(menuText);
        for (Roles role : menuRoles) {
            MenuItem roleItem = new MenuItem(role.toString());
            roleItem.setOnAction(e -> {
                MyPages page = UIFactory.getPageForRole(role);
                context.getSession().setCurrentRole(role);
                context.router().navigate(page);
            });
            menuButton.getItems().add(roleItem);
        }

        // If user only has one role, disable menu
        if (menuButton.getItems().size() == 1) {
            menuButton.setDisable(true);
        }
    }

    /**
     * Sets the text of the menu button.
     */
    public NavMenuBuilder text(String text) {
        menuButton.setText(text);
        return this;
    }

    /**
     * Builds and returns the {@link MenuButton} instance.
     */
    public MenuButton build() {
        return menuButton;
    }
}
