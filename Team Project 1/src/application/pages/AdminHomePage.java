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
@src.application.framework.Route(src.application.framework.MyPages.ADMIN_HOME)
@src.application.framework.View(title = "Admin Page")
public class AdminHomePage extends BasePage {

    public AdminHomePage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);


        Label adminLabel = src.application.framework.UIFactory.createLabel("Hello, Admin!", src.application.framework.DesignGuide.TITLE_LABEL, null);

        // Navigation buttons using UIFactory; navigation via the shared router.
        Button userButton = src.application.framework.UIFactory.createButton("Show Users", e -> context.router().navigate(src.application.framework.MyPages.ADMIN_USER));
        Button inviteButton = src.application.framework.UIFactory.createButton("Invite", e -> context.router().navigate(src.application.framework.MyPages.INVITATION));
        Button otpButton = src.application.framework.UIFactory.createButton("Set user OTP", e -> context.router().navigate(src.application.framework.MyPages.SET_PASS));
        Button logoutButton = src.application.framework.UIFactory.createButton("Logout", e -> context.router().navigate(src.application.framework.MyPages.USER_LOGIN));

        layout.getChildren().addAll(adminLabel, userButton, inviteButton, otpButton, logoutButton);
        return layout;
    }
}