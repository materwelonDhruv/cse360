package application.pages;

import application.UserProfileWindow;
import application.framework.*;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * The StaffHomePage class provides the main landing page for staff members.
 * <p>
 * This page displays basic navigation controls and a button that opens
 * the staff private chat interface.
 * </p>
 *
 * @author Dhruv
 */
@Route(MyPages.STAFF_HOME)
@View(title = "Staff Home Page")
public class StaffHomePage extends BasePage {
    //Creates list view to display search results
    private static final ListView<String> resultView = new ListView<>();

    /**
     * Constructs the staff home page.
     */
    public StaffHomePage() {
        super();
    }

    /**
     * Updates the ListView used for displaying search results.
     *
     * @param list The list of users to display as search results.
     */
    public static void updateResults(List<User> list) {
        resultView.getItems().clear();
        for (User u : list) {
            resultView.getItems().add(u.getUserName());
        }
        int height = list.size();
        resultView.setPrefHeight(height * 26); //Gives 26 height for every element to cleanly display
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
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        resultView.getItems().clear(); //Clear previous searches
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
        Button privateChatsButton = UIFactory.createButton("Open Private Chats", b ->
                b.routeToPage(MyPages.STAFF_PRIVATE_CHATS, context)
        );

        // Button to manage reviewer roles
        Button manageReviewerButton = UIFactory.createButton("Manage Reviewer Roles", b ->
                b.routeToPage(MyPages.REMOVE_REVIEWER, context));

        // Button to navigate to the announcements page
        Button announcementsButton = UIFactory.createButton("Announcements", b ->
                b.routeToPage(MyPages.ANNOUNCEMENTS, context)
        );

        //List to add search results
        List<User> searchList = new ArrayList<User>();
        //Search field allowing staff to search for users
        TextField userSearch = UIFactory.createTextField("Username Search");
        //When a staff is searching for users, when the content is over three characters, displays fuzzy search of users
        userSearch.setOnKeyReleased(event -> {
            String inputText = userSearch.getText();
            if (inputText.length() > 3) {
                try {
                    resultView.getItems().clear(); //Clear previous searches
                    searchList.clear();
                    searchList.addAll(context.users().searchUsers(inputText)); //Add search results
                    updateResults(searchList);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //When the generated search view is double-clicked, brings staff to selected user's profile page
        resultView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selectedItem = context.users().getByUsername(resultView.getSelectionModel().getSelectedItem());
                UserProfileWindow userProfileWindow = new UserProfileWindow();
                userProfileWindow.createUserProfileStage(context, context.getSession().getActiveUser().getId(), selectedItem.getId());
            }
        });

        // Add base buttons
        Button logoutButton = UIFactory.createLogoutButton(context);
        Button homepageButton = UIFactory.createHomepageButton("Main Page", context);

        HBox topBar = new HBox(10, privateChatsButton,
                manageReviewerButton,
                announcementsButton,
                logoutButton,
                homepageButton
        );
        layout.getChildren().addAll(greeting, topBar, userSearch, resultView);

        return layout;
    }
}