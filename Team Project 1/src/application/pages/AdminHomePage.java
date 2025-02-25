package application.pages;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * AdminHomePage represents the main dashboard for admin users.
 * It provides navigation to user management, invitation generation, OTP setup, and logout.
 */
@Route(MyPages.ADMIN_HOME)
@View(title = "Admin Page")
public class AdminHomePage extends BasePage {

    public AdminHomePage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);


        Label adminLabel = UIFactory.createLabel("Hello, Admin!", DesignGuide.TITLE_LABEL, null);

        // Navigation buttons using UIFactory; navigation via the shared router.
        Button userButton = UIFactory.createButton("Show Users", e -> context.router().navigate(MyPages.ADMIN_USER));
        Button inviteButton = UIFactory.createButton("Invite", e -> context.router().navigate(MyPages.INVITATION));
        Button otpButton = UIFactory.createButton("Set user OTP", e -> context.router().navigate(MyPages.SET_PASS));
        Button logoutButton = UIFactory.createButton("Logout", e -> context.router().navigate(MyPages.USER_LOGIN));

        layout.getChildren().addAll(adminLabel, userButton, inviteButton, otpButton, logoutButton);
        return layout;
    }
}