package application.pages;

import application.framework.*;
import database.model.entities.Review;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

@Route(MyPages.REVIEW_LIST)
@View(title = "Review List")
public class ReviewList extends BasePage {
    @Override
    public Pane createView() {
        //Setup layout
        BorderPane layout = new BorderPane();
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        HBox bottomBar = new HBox(10);
        HBox topBar = new HBox(10);
        //Setup bottom toolbar
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.REVIEW_HOME, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        bottomBar.getChildren().addAll(backButton, logoutButton);
        layout.setBottom(bottomBar);
        //Setup ListView of Reviews
        ListView<Review> reviewList = new ListView<>();
        ObservableList<Review> reviews = FXCollections.observableArrayList(context.reviews().getAll());
        reviewList.setItems(reviews);
        layout.getChildren().addAll(reviewList);
        //Setup top toolbar
        TextField reviewInput = UIFactory.createTextField("Review:", f -> f.minWidth(200).maxWidth(600).minChars(5).maxChars(1000));
        Button addReview = reviewAddButtonSetup(reviewList, reviewInput);
        Button editReview = reviewEditButtonSetup();
        Button deleteReview = reviewDeleteButtonSetup();
        topBar.getChildren().addAll(reviewInput, addReview, editReview, deleteReview);
        layout.setTop(topBar);
        return layout;
    }

    private Button reviewAddButtonSetup(ListView<Review> reviewTable, TextField reviewInput) {
        Button addReview = UIFactory.createButton("Add Review",
                e -> e.onAction(
                        a -> {
                            String reviewContent = (String) reviewInput.getCharacters();
                            Review newReview = new Review();
                            newReview.setReviewer(context.getSession().getActiveUser());
                            //TODO: Get user data
                            newReview.setUser(null);
                            newReview.setRating(0);
                            context.reviews().create(newReview);
                        }
                )
        );
        return addReview;
    }

    private Button reviewEditButtonSetup() {
        Button editReview = new Button("Edit Review");
        return editReview;
    }

    private Button reviewDeleteButtonSetup() {
        Button deleteReview = new Button("Delete Review");
        return deleteReview;
    }
}
