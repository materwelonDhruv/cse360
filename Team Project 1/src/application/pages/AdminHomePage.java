package application.pages;

import application.framework.BasePage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * AdminHomePage represents the main dashboard for admin users.
 * It provides navigation to user management, invitation generation, OTP setup, and logout.
 */
@application.framework.Route(application.framework.MyPages.ADMIN_HOME)
@application.framework.View(title = "Admin Page")
public class AdminHomePage extends BasePage {

    public AdminHomePage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(application.framework.DesignGuide.MAIN_PADDING + " " + application.framework.DesignGuide.CENTER_ALIGN);


        Label adminLabel = application.framework.UIFactory.createLabel("Hello, Admin!");

        // Navigation buttons using UIFactory; navigation via the shared router.
        Button userButton = application.framework.UIFactory.createButton("Show Users", e -> e.routeToPage(application.framework.MyPages.ADMIN_USER, context));
        Button inviteButton = application.framework.UIFactory.createButton("Invite", e -> e.routeToPage(application.framework.MyPages.INVITATION, context));
        Button otpButton = application.framework.UIFactory.createButton("Set user OTP", e -> e.routeToPage(application.framework.MyPages.SET_PASS, context));
        Button logoutButton = application.framework.UIFactory.createButton("Logout", e -> e.routeToPage(application.framework.MyPages.USER_LOGIN, context));

        layout.getChildren().addAll(adminLabel, userButton, inviteButton, otpButton, logoutButton);
        return layout;
    }
}