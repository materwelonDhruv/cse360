package application.pages.admin;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * AdminHomePage represents the main dashboard for admin users.
 * It provides navigation to user management, invitation generation, OTP setup, and logout.
 *
 * @author Mike
 */
@Route(MyPages.ADMIN_HOME)
@View(title = "Admin Page")
public class AdminHomePage extends BasePage {

    /**
     * Constructor using BasePage.
     */
    public AdminHomePage() {
        super();
    }

    /**
     * @return layout
     * Creates layout for Admin Home page, including buttons to traverse to other pages.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);


        Label adminLabel = UIFactory.createLabel("Hello, Admin!");

        // Navigation buttons using UIFactory; navigation via the shared router.
        Button userButton = UIFactory.createButton("Show Users", e -> e.routeToPage(MyPages.ADMIN_USER, context));
        Button inviteButton = UIFactory.createButton("Invite", e -> e.routeToPage(MyPages.INVITATION, context));
        Button otpButton = UIFactory.createButton("Set user OTP", e -> e.routeToPage(MyPages.SET_PASS, context));
        Button pendingReuestButton = UIFactory.createButton("Pending Requests", e -> e.routeToPage(MyPages.ADMIN_PENDING, context));
        Button solvedReuestButton = UIFactory.createButton("Solved Requests", e -> e.routeToPage(MyPages.ADMIN_SOLVED, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));

        layout.getChildren().addAll(adminLabel, userButton, inviteButton, otpButton, pendingReuestButton, solvedReuestButton, logoutButton);
        return layout;
    }
}