package application.framework.builders;

import application.AppContext;
import application.framework.MyPages;
import application.framework.UIFactory;
import javafx.scene.control.Button;
import utils.permissions.Roles;

/**
 * Builder class for creating a homepage button that navigates to the user's homepage.
 *
 * @author Dhruv
 */
public class HomepageButtonBuilder extends ButtonBuilder {
    /**
     * Initializes the builder with the given text and application context.
     */
    public HomepageButtonBuilder(AppContext context) {
        super("");

        boolean isUserHome = context.router().getCurrentPage() == MyPages.USER_HOME;
        Button button = super
                .text(isUserHome ? "Your Homepage" : "Back to User Home")
                .getSource();

        button.setOnAction(e -> {
            if (isUserHome) {
                Roles currentRole = context.getSession().getCurrentRole();
                MyPages homepage = UIFactory.getPageForRole(currentRole);
                context.router().navigate(homepage);
            } else {
                context.router().navigate(MyPages.USER_HOME);
            }
        });
    }
}
