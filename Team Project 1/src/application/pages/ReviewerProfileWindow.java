package application.pages;

import application.AppContext;
import application.framework.DesignGuide;
import application.framework.UIFactory;
import database.model.entities.Answer;
import database.model.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Pop-up window displaying a reviewer's profile including their
 * contact information and reviews</p>
 *
 * @author Riley
 */
public class ReviewerProfileWindow {
    /**
     * @param context    used to access data and services
     * @param userId     ID of user visiting the page used if user wants to add reviewer to their trusted reviewer list
     * @param reviewerId ID of reviewer on page user is visiting to dynamically change elements to reflect a reviewer's
     *                   information and allow a user adding them to their trusted reviewer list
     *                   Pop-up window displaying a reviewer's information, reviews, and a button allowing a visiting user to add the
     *                   reviewer to their trusted reviewer list
     */
    public void createReviewerProfileStage(AppContext context, int userId, int reviewerId) {

        Stage reviewerProfile = new Stage();
        reviewerProfile.initModality(Modality.NONE);
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        //Labels for displaying information about the reviewer
        Label title = new Label(context.users().getById(reviewerId).getFirstName() + " | Reviewer");
        Label email = new Label(context.users().getById(reviewerId).getEmail());
        Label reviewsLabel = new Label("My Reviews");

        //Button to allow reviewer to edit their profile
        Button editButton = new Button("Edit");
        //Fields to allow users to see reviewers reviews
        ListView<String> reviewTable = new ListView<>();
        ObservableList<String> reviews = FXCollections.observableArrayList();
        for (Answer a : context.answers().getAnswersByUser(reviewerId)) {
            if (a.getMessage().getContent().contains("φ"))
                reviews.add(a.getMessage().getContent());
        }
        for (String s : reviews) {
            reviewTable.getItems().add(s);
        }

        User user = context.users().getById(userId);
        User reviewer = context.users().getById(reviewerId);
        Button addTrustedButton = UIFactory.createButton("Add as trusted reviewer");
        try {
            List<User> untrustedReviewers = context.users().getReviewersNotRatedByUser(userId);
            List<Integer> untrustedReviewerIds = new ArrayList<>();
            for (User u : untrustedReviewers) {
                untrustedReviewerIds.add(u.getId());
            }
            if (!untrustedReviewerIds.contains(reviewerId)) {
                addTrustedButton.setDisable(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        addTrustedButton.setOnAction(e -> {
            context.reviews().setRating(reviewer, user, Integer.MAX_VALUE);
            addTrustedButton.setDisable(true);
        });
        layout.getChildren().addAll(title, email, addTrustedButton, reviewsLabel, reviewTable);
        Scene scene = new Scene(layout, 400, 300);
        reviewerProfile.setScene(scene);
        reviewerProfile.setTitle("Reviewer Profile");
        reviewerProfile.show();

    }

}
