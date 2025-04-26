package application.pages.staff;

import application.framework.*;
import database.model.entities.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The StaffHomePage class provides the main landing page for staff members.
 * <p>
 * This page displays basic navigation controls and a button that opens
 * the staff private chat interface.
 * </p>
 */
@Route(MyPages.STAFF_HOME)
@View(title = "Staff Home Page")
public class StaffHomePage extends BasePage {

    /**
     * Constructs the staff home page.
     */
    public StaffHomePage() {
        super();
    }

    /**
     * Creates and returns the main layout for the staff homepage,
     * including a welcome message, navigation options, and a button
     * to open private chats with users.
     *
     * @return A Pane containing the staff homepage UI elements.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Retrieve the active user (staff) from session.
        User staffUser = context.getSession().getActiveUser();
        if (staffUser == null) {
            layout.getChildren().add(new Label("No active staff user found."));
            return layout;
        }

        // Greeting label
        Label greeting = UIFactory.createLabel("Hello, " + staffUser.getFirstName() + " (Staff)!");
        greeting.getStyleClass().add("heading");

        // Button to navigate to the staff private chats page
        Button privateChatsButton = UIFactory.createButton("Open Private Chats",
                b -> b.routeToPage(MyPages.STAFF_PRIVATE_CHATS, context));

        // Button to manage reviewer roles
        Button manageReviewerButton = UIFactory.createButton("Manage Reviewer Roles",
                b -> b.routeToPage(MyPages.REMOVE_REVIEWER, context));

        // Button to navigate to the announcements page
        Button announcementsButton = UIFactory.createButton("Announcements",
                b -> b.routeToPage(MyPages.ANNOUNCEMENTS, context));

        // Button to navigate to the staff navigation page
        Button staffNavigationButton = UIFactory.createButton("Navigate To A Page",
                b -> b.routeToPage(MyPages.STAFF_NAVIGATION, context));

        // Button to logout
        Button logoutButton = UIFactory.createLogoutButton(context);

        // Question display button
        Button questionDisplayButton = UIFactory.createHomepageButton("Question Display", context);

        //Button for solved requests
        Button solved_requests = UIFactory.createButton("Solved Requests", e -> e.routeToPage(
                MyPages.ADMIN_SOLVED, context
        ));

        // role menu to select and switch between the role
        MenuButton roleMenu = UIFactory.createNavMenu(context, "Select Role");

        HBox topBar = new HBox(10, logoutButton, roleMenu);
        topBar.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(greeting,
                manageReviewerButton,
                solved_requests,
                privateChatsButton,
                questionDisplayButton,
                announcementsButton,
                staffNavigationButton,
                topBar);

        return layout;
    }
}