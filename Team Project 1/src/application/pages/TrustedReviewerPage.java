package application.pages;


import application.framework.*;
import database.model.entities.Review;
import database.model.entities.User;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * This page allows students to add or remove reviewers from their personal trusted reviewers list.
 * Students may also rank each of their trusted reviewers and this ranking will be used when ordering
 * the reviews of a question or answer so that the review that is most likely to be useful will
 * appear highest.
 *
 * @author Tyler
 */
@Route(MyPages.TRUSTED_REVIEWER)
@View(title = "Manage Trusted Reviewers")
public class TrustedReviewerPage extends BasePage {
    // ListView to contain the trusted reviewers
    private final ListView<HBox> reviewersListView = new ListView<>();

    /**
     * Creates the layout for the TrustedReviewerPage
     *
     * @return layout
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Set up reviewersListView
        reviewersListView.setFixedCellSize(65);
        reviewersListView.setPlaceholder(new Label("No Trusted Reviewers"));

        // Double Click to go to the profile of the trusted reviewer
        reviewersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                HBox selectedReviewer = reviewersListView.getSelectionModel().getSelectedItem();
                if (selectedReviewer != null) {
                    UserProfileWindow UserProfileWindow = new UserProfileWindow();
                    Review r = getReviewFromHBox(selectedReviewer);
                    application.pages.UserProfileWindow.createUserProfileStage(context, context.getSession().getActiveUser().getId(), r.getReviewer().getId());
                }
            }
        });

        Label titleLabel = UIFactory.createLabel("Trusted Reviewers");

        // Load the user's trusted reviewers
        loadTrustedReviewers();

        // Button to open a menu for adding new reviewers to the trusted reviewer list
        Button addReviewersButton = UIFactory.createButton("Add Reviewers", e -> e.routeToPage(MyPages.ADD_TRUSTED_REVIEWER, context));

        // Navigation buttons using UIFactory; navigation via the shared router.
        Button backButton = UIFactory.createBackButton(context);

        // HBox for option buttons
        HBox optionBar = new HBox(15, addReviewersButton, backButton);
        optionBar.setAlignment(Pos.BASELINE_CENTER);

        layout.getChildren().addAll(titleLabel, reviewersListView, optionBar);
        return layout;
    }

    /**
     * Loads all trusted reviewers from the student's list into the reviewersListView
     */
    private void loadTrustedReviewers() {
        reviewersListView.getItems().clear();
        // Get the student's ordered trusted reviewers list from the database
        List<Review> trustedReviewers = context.reviews().getReviewersByUserId(context.getSession().getActiveUser().getId());

        // Count for newly added reviewers; used to give them proper rankings
        int numNewAdditions = 0;

        // Add trusted reviewers in order of their rankings
        for (Review trustedReviewer : trustedReviewers) {
            HBox trustedReviewerHBox = createTrustedReviewerHBox(trustedReviewer);

            // Disable ranking buttons if necessary
            if (trustedReviewers.size() == 1) {
                // Disable both ranking buttons
                VBox rankingButtonsVBox = (VBox) trustedReviewerHBox.getChildren().getLast();
                rankingButtonsVBox.getChildren().getFirst().setDisable(true);
                rankingButtonsVBox.getChildren().getLast().setDisable(true);
            } else {
                // Disable a ranking button if the reviewer is first or last in the list
                if (reviewersListView.getItems().isEmpty()) {
                    // Disable decreaseRankingButton
                    VBox rankingButtonsVBox = (VBox) trustedReviewerHBox.getChildren().getLast();
                    rankingButtonsVBox.getChildren().getLast().setDisable(true);
                } else if (reviewersListView.getItems().size() == trustedReviewers.size() - 1) {
                    // Disable increaseRankingButton
                    VBox rankingButtonsVBox = (VBox) trustedReviewerHBox.getChildren().getLast();
                    rankingButtonsVBox.getChildren().getFirst().setDisable(true);
                }
            }

            // Set ranking of the reviewer to the total number of trusted reviewers - numNewAdditions
            // if it is a new addition
            if (trustedReviewer.getRating() == Integer.MAX_VALUE) {
                trustedReviewer.setRating(trustedReviewers.size() - numNewAdditions);
                numNewAdditions++;
                context.reviews().update(trustedReviewer);
            }

            reviewersListView.getItems().addFirst(trustedReviewerHBox);
        }
    }

    /**
     * Creates an HBox containing information about the given reviewer
     * and buttons for ranking and removal from the list
     *
     * @param reviewer The {@link Review} object to make an HBox for
     * @return The Hbox to be added to the list view
     */
    private HBox createTrustedReviewerHBox(Review reviewer) {
        // HBox to contain the reviewerNameLabel and buttons
        HBox trustedReviewerHBox = new HBox(20);

        // Label for the name of the trusted reviewer
        Label reviewerNameLabel = UIFactory.createLabel(reviewer.getReviewer().getUserName());

        // Buttons for increasing or decreasing the ranking of the trusted reviewer
        Button increaseRankingButton = UIFactory.createButton("⬆", e -> e.onAction(
                a -> swapTrustedReviewerRankings(trustedReviewerHBox, -1)));
        Button decreaseRankingButton = UIFactory.createButton("⬇", e -> e.onAction(
                a -> swapTrustedReviewerRankings(trustedReviewerHBox, 1)));

        // Set up VBox to contain ranking buttons
        VBox rankingVBox = new VBox(5, increaseRankingButton, decreaseRankingButton);
        rankingVBox.setAlignment(Pos.TOP_CENTER);

        // Button for removing the trusted reviewer from the student's trusted reviewers list
        Button removeTrustedReviewerButton = UIFactory.createButton("Remove", e -> e.onAction(
                a -> removeTrustedReviewer(trustedReviewerHBox)
        ));

        // Set up trustedReviewerHBox
        trustedReviewerHBox.getChildren().addAll(reviewerNameLabel, removeTrustedReviewerButton, rankingVBox);
        HBox.setHgrow(reviewerNameLabel, Priority.ALWAYS);
        reviewerNameLabel.setMaxWidth(Double.MAX_VALUE);
        trustedReviewerHBox.setAlignment(Pos.CENTER_LEFT);
        return trustedReviewerHBox;
    }

    /**
     * Swaps the ranking of the given reviewer with another in the student's trusted reviewers list.
     *
     * @param trustedReviewerHBox1 The Hbox corresponding to the reviewer to swap
     * @param offset               The amount to be added to the given reviewer's index to find the index of
     *                             the second reviewer to be swapped with
     */
    private void swapTrustedReviewerRankings(HBox trustedReviewerHBox1, int offset) {
        if (!reviewersListView.getItems().contains(trustedReviewerHBox1)) {
            return;
        }
        // Get the two indices of the reviewers to swap
        int index1 = reviewersListView.getItems().indexOf(trustedReviewerHBox1);
        int index2 = index1 + offset;
        if (index2 >= reviewersListView.getItems().size() || index2 < 0) {
            return;
        }

        // Get second HBox and Reviews
        HBox trustedReviewerHBox2 = reviewersListView.getItems().get(index2);
        Review r1 = getReviewFromHBox(trustedReviewerHBox1);
        Review r2 = getReviewFromHBox(trustedReviewerHBox2);

        // Swap Rankings
        int temp = r1.getRating();
        r1.setRating(r2.getRating());
        r2.setRating(temp);

        // Update database
        context.reviews().update(r1);
        context.reviews().update(r2);

        // Get VBoxes containing the ranking buttons
        VBox VBox1 = (VBox) trustedReviewerHBox1.getChildren().getLast();
        VBox VBox2 = (VBox) trustedReviewerHBox2.getChildren().getLast();

        // Swap button disable properties
        boolean tempIncreaseDisable = VBox1.getChildren().getFirst().isDisable();
        boolean tempDecreaseDisable = VBox1.getChildren().getLast().isDisable();
        VBox1.getChildren().getFirst().setDisable(VBox2.getChildren().getFirst().isDisable());
        VBox1.getChildren().getLast().setDisable(VBox2.getChildren().getLast().isDisable());
        VBox2.getChildren().getFirst().setDisable(tempIncreaseDisable);
        VBox2.getChildren().getLast().setDisable(tempDecreaseDisable);

        // Swap HBoxes in the list view
        reviewersListView.getItems().set(index1, trustedReviewerHBox2);
        reviewersListView.getItems().set(index2, trustedReviewerHBox1);
    }

    /**
     * Removes the given reviewer from the student's trusted reviewers list
     *
     * @param trustedReviewerHBox The HBox corresponding to the desired reviewer
     */
    private void removeTrustedReviewer(HBox trustedReviewerHBox) {
        if (!reviewersListView.getItems().contains(trustedReviewerHBox)) {
            return;
        }
        // Delete the trusted reviewer from the database
        Review reviewer = getReviewFromHBox(trustedReviewerHBox);
        context.reviews().delete(reviewer.getReviewer().getId(), reviewer.getUser().getId());

        // Disable a ranking button of new first or last trustedReviewerHBox in list view
        if (reviewersListView.getItems().size() > 1) {
            if (reviewersListView.getItems().getFirst().equals(trustedReviewerHBox)) {
                // Disable new first increaseRankingButton
                VBox rankingButtonsVBox = (VBox) reviewersListView.getItems().get(1).getChildren().getLast();
                rankingButtonsVBox.getChildren().getFirst().setDisable(true);
            } else if (reviewersListView.getItems().getLast().equals(trustedReviewerHBox)) {
                // Disable new last decreaseRankingButton
                int newLastIndex = reviewersListView.getItems().size() - 2;
                VBox rankingButtonsVBox = (VBox) reviewersListView.getItems().get(newLastIndex).getChildren().getLast();
                rankingButtonsVBox.getChildren().getLast().setDisable(true);
            }
        }

        reviewersListView.getItems().remove(trustedReviewerHBox);
    }

    // Method to get the Review from the given trustedReviewerHBox using the reviewer's name

    /**
     * Gets the {@link Review} from the given trusted reviewer using their username
     *
     * @param trustedReviewerHBox The HBox corresponding to the trusted reviewer
     * @return The {@link Review} of the given trusted reviewer
     */
    private Review getReviewFromHBox(HBox trustedReviewerHBox) {
        Label reviewerName = (Label) trustedReviewerHBox.getChildren().getFirst();
        User reviewer = context.users().getByUsername(reviewerName.getText());
        return context.reviews().getByCompositeKey(reviewer.getId(), context.getSession().getActiveUser().getId());
    }
}