package application.pages;

import application.framework.*;
import database.model.entities.Review;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * AdminHomePage represents the main dashboard for admin users.
 * It provides navigation to user management, invitation generation, OTP setup, and logout.
 */
@Route(MyPages.REVIEWER_PROFILE)
@View(title = "Reviewer Profile")
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
        Label email = new Label("ReviewerEmail@gmail.com");
        Label reviewsLabel = new Label("My Reviews");

        //Button to allow reviewer to edit their profile
        Button editButton = new Button("Edit");
        //Fields to allow users to see reviewers reviews
        TableView<Review> reviewTable = new TableView<>();
        ObservableList<Review> reviews = FXCollections.observableArrayList();
        reviews.addAll(context.reviews().getAll());
        reviewTable.setItems(reviews);
        Button pmButton = UIFactory.createButton("Private Message", e -> e.routeToPage(MyPages.PRIVATE_MESSAGE, context));
        Button addTrustedButton = UIFactory.createButton("Add as trusted reviewer");
        layout.getChildren().addAll(title, email, pmButton, addTrustedButton, reviewsLabel, reviewTable);

        return layout;

    }
}