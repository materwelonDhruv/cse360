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
    public HomepageButtonBuilder(String text, AppContext context) {
        super(text);
        Button button = super.getSource();
        button.setOnAction(e -> {
            if (context.router().getCurrentPage() == MyPages.USER_HOME) {
                Roles currentRole = context.getSession().getCurrentRole();
                MyPages homepage = UIFactory.getPageForRole(currentRole);
                context.router().navigate(homepage);
            } else {
                context.router().navigate(MyPages.USER_HOME);
            }

        });
    }
}
