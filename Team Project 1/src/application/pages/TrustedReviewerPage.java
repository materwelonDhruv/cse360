package application.pages;


import application.framework.*;
import database.model.entities.Review;
import database.model.entities.User;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import utils.permissions.Roles;

import java.util.Comparator;
import java.util.List;

/**
 * This page allows students to add or remove reviewers from their personal trusted reviewers list.
 * Students may also assign a weight to each of their trusted reviewers to be used when ordering
 * the reviews of a question or answer so that the review that is most likely to be useful will
 * appear highest.
 */
@Route(MyPages.TRUSTED_REVIEWER)
@View(title = "Manage Trusted Reviewers")
public class TrustedReviewerPage extends BasePage {
    // VBox, AnchorPan, and ScrollPane to contain the trusted reviewers
    private final ListView<HBox> reviewersListView = new ListView<HBox>();
    private final AnchorPane reviewersAnchorPane = new AnchorPane(reviewersListView);
    private final ScrollPane reviewersScrollPane = new ScrollPane(reviewersAnchorPane);

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Set up reviewersListView
        reviewersListView.prefWidthProperty().bind(reviewersScrollPane.widthProperty().subtract(17));
        reviewersListView.prefHeightProperty().bind(layout.heightProperty().multiply(.75));
        reviewersListView.setFixedCellSize(65);
        reviewersListView.setPlaceholder(new Label("No Trusted Reviewers"));

        // Retrieve the active user from session.
        User user = context.getSession().getActiveUser();
        if (user == null) {
            return new VBox(new Label("No active user found."));
        }

        Label titleLabel = UIFactory.createLabel("Trusted Reviewers");

        // Load the user's trusted reviewers
        loadTrustedReviewers();

        // Button to open a menu for adding new reviewers to the trusted reviewer list
        Button addAReviewerButton = UIFactory.createButton("Add A Reviewer");

        // Navigation buttons using UIFactory; navigation via the shared router.
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_HOME, context));

        // HBox for option buttons
        HBox optionBar = new HBox(15, addAReviewerButton, backButton);
        optionBar.setAlignment(Pos.BASELINE_CENTER);

        layout.getChildren().addAll(titleLabel, reviewersScrollPane, optionBar);
        return layout;
    }

    // Method to load all trusted reviewers in the student's list into the trustedReviewerHBox
    public void loadTrustedReviewers() {
        reviewersListView.getItems().clear();
        // Get the student's trusted reviewers list from the database
        List<Review> trustedReviewers = context.reviews().getReviewersByUserId(context.getSession().getActiveUser().getId());

        // Sort the reviewers by their rating
        trustedReviewers.sort(new Comparator<Review>() {
            public int compare(Review r1, Review r2) {
                return r1.getRating() - r2.getRating();
            }
        });

        // Add trusted reviewers in order of their rankings
        for (Review trustedReviewer : trustedReviewers) {
            HBox trustedReviewerHBox = createTrustedReviewerHBox(trustedReviewer);
            reviewersListView.getItems().add(trustedReviewerHBox);
        }
    }

    // Method to create an HBox containing information about the given
    // reviewer and buttons for ranking and removal from the list
    private HBox createTrustedReviewerHBox(Review reviewer) {
        // Label for the name of the trusted reviewer
        Label reviewerNameLabel = UIFactory.createLabel(reviewer.getReviewer().getUserName());

        // Buttons for increasing or decreasing the ranking of the trusted reviewer
        Button increaseRankingButton = UIFactory.createButton("⬆");
        Button decreaseRankingButton = UIFactory.createButton("⬇");
        VBox rankingVBox = new VBox(5, increaseRankingButton, decreaseRankingButton);
        rankingVBox.setAlignment(Pos.TOP_CENTER);

        // Button for removing the trusted reviewer from the student's trusted reviewers list
        Button removeTrustedReviewerButton = UIFactory.createButton("Remove");

        // HBox to contain the reviewerNameLabel and buttons
        HBox trustedReviewerHBox = new HBox(20, reviewerNameLabel, removeTrustedReviewerButton, rankingVBox);
        HBox.setHgrow(reviewerNameLabel, Priority.ALWAYS);
        reviewerNameLabel.setMaxWidth(Double.MAX_VALUE);
        trustedReviewerHBox.setAlignment(Pos.CENTER_LEFT);
        return trustedReviewerHBox;
    }
}