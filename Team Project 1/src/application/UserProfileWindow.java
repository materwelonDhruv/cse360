package application;

import application.framework.DesignGuide;
import application.framework.UIFactory;
import database.model.entities.Answer;
import database.model.entities.Question;
import database.model.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utils.permissions.RolesUtil.removeRole;

/**
 * <p> Pop-up window displaying a users's profile including their
 * contact information and questions/reviews</p>
 *
 * @author Riley
 */
public class UserProfileWindow {
    /**
     * @param context  used to access data and services
     * @param userId   ID of user visiting the page used if user is viewing a review page and wants to add reviewer to their trusted reviewer list
     * @param targetId ID of targeted user on page user is visiting to dynamically change elements to reflect a user's
     *                 information and allow a user adding them to their trusted reviewer list if they are a reviewer
     *                 Pop-up window displaying a reviewer's information, reviews, and a button allowing a visiting user to add the
     *                 reviewer to their trusted reviewer list
     */
    public static void createUserProfileStage(AppContext context, int userId, int targetId) {
        Label title = new Label();
        title.setText("");
        Stage userProfile = new Stage();
        userProfile.initModality(Modality.NONE);
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        //Labels for displaying information about the reviewer
        Label email = new Label();
        email.setText("");
        email.setText(context.users().getById(targetId).getEmail());

        //Labels for displaying a reviewer or student's page
        Label reviewsLabel = new Label("My Reviews");
        Label questionsLabel = UIFactory.createLabel("My Questions");

        //Fields to allow users to see reviewers reviews or students questions
        ListView<String> displayTable = new ListView<>();
        //Populates table with reviewer's reviews
        if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.REVIEWER)) {
            displayTable.getItems().clear();
            ObservableList<String> reviews = FXCollections.observableArrayList();
            for (Answer a : context.answers().getAnswersByUser(targetId)) {
                if (a.getMessage().getContent().contains("φ"))
                    reviews.add(a.getMessage().getContent());
            }
            for (String s : reviews) {
                displayTable.getItems().add(s);
            }
        }
        //Populates table with student's questions
        else if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.STUDENT)) {
            displayTable.getItems().clear();
            for (Question q : context.questions().getQuestionsByUser(targetId)) {
                String qTitle = q.getTitle();
                displayTable.getItems().add(qTitle);
            }
        }

        User user = context.users().getById(userId);
        User reviewer = context.users().getById(targetId);
        Button addTrustedButton = UIFactory.createButton("Add as trusted reviewer");
        Button removeReviewerButton = UIFactory.createButton("Remove reviewer");
        removeReviewerButton.setOnAction(e -> {
            User targetUser = context.users().getById(targetId);
            int roleInt = targetUser.getRoles();
            targetUser.setRoles(removeRole(roleInt, Roles.REVIEWER));
            context.users().update(targetUser);
            new Alert(Alert.AlertType.INFORMATION, "Reviewer role removed.").show();
            userProfile.close();
        });

        try {
            List<User> untrustedReviewers = context.users().getReviewersNotRatedByUser(userId);
            List<Integer> untrustedReviewerIds = new ArrayList<>();
            for (User u : untrustedReviewers) {
                untrustedReviewerIds.add(u.getId());
            }
            if (!untrustedReviewerIds.contains(targetId)) {
                addTrustedButton.setDisable(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        addTrustedButton.setOnAction(e -> {
            context.reviews().setRating(reviewer, user, Integer.MAX_VALUE);
            addTrustedButton.setDisable(true);
        });

        //Sets users appropriate role
        if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.ADMIN)) {
            title.setText(context.users().getById(targetId).getFirstName() + " | " + "Admin");
            layout.getChildren().addAll(title, email);
        } else if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.REVIEWER)) {
            title.setText(context.users().getById(targetId).getFirstName() + " | " + "Reviewer");
            layout.getChildren().addAll(title, email, addTrustedButton, reviewsLabel, displayTable);
        } else if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.STUDENT)) {
            title.setText(context.users().getById(targetId).getFirstName() + " | " + "Student");
            layout.getChildren().addAll(title, email, questionsLabel, displayTable);
        } else if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.STAFF)) {
            title.setText(context.users().getById(targetId).getFirstName() + " | " + "Staff");
            layout.getChildren().addAll(title, email);
        } else if (RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.INSTRUCTOR)) {
            title.setText(context.users().getById(targetId).getFirstName() + " | " + "Instructor");
            layout.getChildren().addAll(title, email);
        } else {
            title.setText(context.users().getById(targetId).getFirstName() + " | " + "User");
            layout.getChildren().addAll(title, email);
        }
        if (RolesUtil.hasRole(context.users().getById(userId).getRoles(), Roles.STAFF) && RolesUtil.hasRole(context.users().getById(targetId).getRoles(), Roles.REVIEWER)) {
            layout.getChildren().add(removeReviewerButton);
        }

        Scene scene = new Scene(layout, 400, 300);
        userProfile.setScene(scene);
        userProfile.setTitle(context.users().getById(targetId).getFirstName() + "'s Profile");

        userProfile.show();


    }

}
