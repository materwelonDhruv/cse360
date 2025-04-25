package application.pages;

import application.framework.*;
import database.model.entities.User;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utils.SearchUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This page allows students to search for reviewers that are not on their trusted reviewer list and
 * select as many as they want to add to that list. They may also double-click on a reviewer to view
 * their profile.
 *
 * @author Tyler
 */
@Route(MyPages.ADD_TRUSTED_REVIEWER)
@View(title = "Add Trusted Reviewers")
public class AddTrustedReviewerPage extends BasePage {
    // ListView to contain all reviewers not yet trusted by the student or the search results
    private static final ListView<String> resultView = new ListView<>();
    // TextField for searching reviewers by name
    private final TextField reviewerNameInput = UIFactory.createTextField("Search Reviewers By Name", f ->
            f.minChars(0).maxChars(30));

    // Boolean to keep track of whether the reviewers are sorted in the resultView
    private boolean sorted = false;

    /**
     * Creates the layout for the AddTrustedReviewerPage
     * @return layout
     */
    @Override
    public Pane createView() {
        // Retrieve the active user from session.
        User user = context.getSession().getActiveUser();
        if (user == null) {
            return new VBox(new Label("No active user found."));
        }

        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Add Trusted Reviewers");

        // Button to toggle sorting of reviewers by their ratings
        Button sortReviewersByRatingButton = UIFactory.createButton("Sort By Rating");
        sortReviewersByRatingButton.setOnAction(a -> {
            sorted = !sorted;
            loadUntrustedReviewers();
            if (sorted) {
                sortReviewersByRatingButton.setText("Show Unsorted");
            } else {
                sortReviewersByRatingButton.setText("Sort By Rating");
            }
        });

        // HBox to contain the search bar and sort button
        HBox searchOptions = new HBox(15, reviewerNameInput, sortReviewersByRatingButton);
        HBox.setHgrow(reviewerNameInput, Priority.ALWAYS);

        // Set up resultView
        resultView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultView.setFixedCellSize(26);
        resultView.setPlaceholder(new Label("No Existing Reviewers"));

        // Load all untrusted reviewers into the resultView
        loadUntrustedReviewers();

        // Perform a search when a key is released or load
        // all untrusted reviewers if reviewerNameInput is empty
        reviewerNameInput.setOnKeyReleased(event -> {
            String inputText = reviewerNameInput.getText();
            if (!inputText.isEmpty()) {
                try {
                    resultView.getItems().clear();
                    // Get all untrusted reviewers
                    List<User> reviewersToSearch = context.users().getReviewersNotRatedByUser(user.getId());
                    // Get search results and update the resultView
                    List<User> searchList = new ArrayList<>(SearchUtil.fullTextSearch(reviewersToSearch, inputText,
                            r -> r.getUserName() + " " + r.getFirstName() + " " + r.getLastName()));
                    updateResults(searchList);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                loadUntrustedReviewers();
            }
        });

        // Double Click to go to the profile of the trusted reviewer
        resultView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                List<String> selectedReviewer = resultView.getSelectionModel().getSelectedItems();
                if (selectedReviewer.size() == 1) {
                    ReviewerProfileWindow reviewerProfileWindow = new ReviewerProfileWindow();
                    User u = context.users().getByUsername(selectedReviewer.get(0));
                    reviewerProfileWindow.createReviewerProfileStage(context, context.getSession().getActiveUser().getId(), u.getId());
                }
            }
        });

        // Button to add all selected reviewers into the student's trusted reviewers list
        Button addButton = UIFactory.createButton("Add Selected Reviewers", e -> e.onAction(a -> {
            List<String> selectedReviewers = resultView.getSelectionModel().getSelectedItems();
            if (!selectedReviewers.isEmpty()) {
                for (String reviewerName : selectedReviewers) {
                    User reviewer = context.users().getByUsername(reviewerName);
                    context.reviews().setRating(reviewer, context.getSession().getActiveUser(), Integer.MAX_VALUE);
                }
                resultView.getItems().removeAll(selectedReviewers);
                // Route back to TrustedReviewerPage if no more reviewers are untrusted
                if (resultView.getItems().isEmpty() && reviewerNameInput.getText().isEmpty()) {
                    context.router().navigate(MyPages.TRUSTED_REVIEWER);
                }
            }
        }));

        // Button to route back to the TrustedReviewerPage
        Button backButton = UIFactory.createBackButton(context);

        // HBox to contain the add and cancel buttons
        HBox buttonHBox = new HBox(20, addButton, backButton);
        buttonHBox.setAlignment(Pos.BASELINE_CENTER);

        layout.getChildren().addAll(titleLabel, searchOptions, resultView, buttonHBox);
        return layout;
    }

    /**
     * Loads all untrusted reviewers into the resultView
     */
    private void loadUntrustedReviewers() {
        try {
            User currentUser = context.getSession().getActiveUser();
            List<User> untrustedReviewers = context.users().getReviewersNotRatedByUser(currentUser.getId());
            List<User> reviewerList = new ArrayList<>(untrustedReviewers);
            if (sorted && !reviewerList.isEmpty()) {
                reviewerList.sort((u1, u2) -> {
                        int rating1 = context.reviews().calculateAggregatedRating(u1);
                        int rating2 = context.reviews().calculateAggregatedRating(u2);
                        return Integer.compare(rating2, rating1);
                });
            }
            resultView.getItems().clear();
            for (User user : reviewerList) {
                resultView.getItems().add(user.getUserName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the resultView with the results of a search
     * @param list The list containing the search results
     */
    private void updateResults(List<User> list) {
        resultView.getItems().clear();
        for (User u : list) {
            resultView.getItems().add(u.getUserName());
        }
    }
}