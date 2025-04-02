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
@Route(MyPages.REVIEWER_PROFILE)
@View(title = "Admin Page")
public class ReviewerProfilePage extends BasePage {

    public ReviewerProfilePage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        //Labels for displaying information about the reviewer
        Label title = new Label("ReviewerName | Reviewer");
        Label name = new Label("ReviewerName");
        Label email = new Label("ReviewerEmail@gmail.com");

        //Button to allow reviewer to edit their profile
        Button editButton = new Button("Edit");

        //Fields to allow users to see reviewers reviews

        layout.getChildren().addAll();
        return layout;
    }
}