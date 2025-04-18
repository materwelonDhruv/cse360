package application.pages;

import application.framework.*;
import database.model.entities.User;
import database.repository.repos.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

/**
 * The RemoveReviewerPage class provides a user interface for staff users to manage and remove
 * reviewer roles from users. The page displays a list of users who have the reviewer role and
 * provides an option to remove that role.
 * <p>
 * Staff users can use this page to correct role assignments when necessary.
 * </p>
 *
 * @author Dhruv
 */
@Route(MyPages.REMOVE_REVIEWER)
@View(title = "Manage Reviewer Roles")
public class RemoveReviewerPage extends BasePage {

    private ListView<User> reviewerListView;
    private ObservableList<User> reviewerData;
    private Users usersRepo;

    /**
     * Constructs the RemoveReviewerPage and initializes the repository.
     */
    public RemoveReviewerPage() {
        super();
        usersRepo = context.users();
    }

    /**
     * Creates and returns the UI layout for managing reviewer roles.
     *
     * @return A Pane containing the list of reviewers with options to remove the reviewer role.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Remove Reviewer Role", f ->
                f.style(DesignGuide.TITLE_LABEL));

        reviewerListView = new ListView<>();
        reviewerData = FXCollections.observableArrayList();
        loadReviewers();

        reviewerListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox cellContainer = new HBox(10);
            private final Label nameLabel = new Label();
            private final Button removeButton = UIFactory.createButton("Remove Reviewer", e ->
                    e.onAction(a -> removeReviewerRole(getItem())));

            {
                cellContainer.setAlignment(Pos.CENTER_LEFT);
                cellContainer.getChildren().addAll(nameLabel, removeButton);
            }

            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getUserName());
                    setGraphic(cellContainer);
                }
            }
        });
        reviewerListView.setItems(reviewerData);

        Button backButton = UIFactory.createBackButton(context);

        layout.getChildren().addAll(titleLabel, reviewerListView, backButton);
        return layout;
    }

    /**
     * Loads all users with the reviewer role into the reviewer list.
     */
    private void loadReviewers() {
        reviewerData.clear();
        List<User> allUsers = usersRepo.getAll();
        for (User user : allUsers) {
            if (RolesUtil.hasRole(user.getRoles(), Roles.REVIEWER)) {
                reviewerData.add(user);
            }
        }
    }

    /**
     * Removes the reviewer role from the specified user and updates the list.
     *
     * @param user The user from whom the reviewer role is to be removed.
     */
    private void removeReviewerRole(User user) {
        if (user == null) {
            return;
        }
        int updatedRoles = RolesUtil.removeRole(user.getRoles(), Roles.REVIEWER);
        user.setRoles(updatedRoles);
        usersRepo.update(user);
        loadReviewers();
        UIFactory.showAlert(Alert.AlertType.INFORMATION, "Role Removed",
                "Reviewer role has been removed from " + user.getUserName() + ".");
    }
}