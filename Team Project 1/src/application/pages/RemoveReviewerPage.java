package application.pages;

import application.framework.*;
import database.model.entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * The RemoveReviewerPage class provides a user interface for staff and instructor users to manage and remove
 * reviewer roles from users. The page displays a list of users who have the reviewer role and
 * provides an option to remove that role.
 * <p>
 * Staff and Instructor users can use this page to correct role assignments (reviewer) when necessary.
 * </p>
 *
 * @author Dhruv
 */
@Route(MyPages.REMOVE_REVIEWER)
@View(title = "Manage Reviewer Roles")
public class RemoveReviewerPage extends BasePage {
    private TableView<User> reviewersTable;
    private ObservableList<User> reviewersData;

    /**
     * Creates and returns the main UI layout for the manage reviewers page.
     * The page includes:
     * - A title indicating the page purpose.
     * - A table displaying all users with reviewer privileges.
     * - Buttons for navigation.
     *
     * @return A Pane containing the manage reviewers interface.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Manage Reviewers");
        titleLabel.getStyleClass().add("heading");

        Label instructionLabel = UIFactory.createLabel("All users with reviewer privileges:");
        instructionLabel.getStyleClass().add("subheading");

        // Create the table for displaying reviewers
        reviewersTable = new TableView<>();
        reviewersTable.setPrefHeight(400);
        VBox.setVgrow(reviewersTable, Priority.ALWAYS);

        // Set up the table columns
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setPrefWidth(150);
        usernameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUserName()));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFirstName() + " " + data.getValue().getLastName()));

        TableColumn<User, String> rolesCol = new TableColumn<>("Roles");
        rolesCol.setPrefWidth(200);
        rolesCol.setCellValueFactory(data -> {
            Roles[] roles = RolesUtil.intToRoles(data.getValue().getRoles());
            StringBuilder rolesText = new StringBuilder();
            for (int i = 0; i < roles.length; i++) {
                if (i > 0) rolesText.append(", ");
                rolesText.append(roles[i].name());
            }
            return new SimpleStringProperty(rolesText.toString());
        });

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button removeButton = UIFactory.createButton("Remove Reviewer", e -> e.onAction(a -> {
                User user = getTableView().getItems().get(getIndex());
                handleRemoveReviewerRole(user);
            }));

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });

        reviewersTable.getColumns().addAll(usernameCol, nameCol, rolesCol, actionsCol);

        // Load all users with reviewer role
        loadReviewers();

        // Add placeholder text when there are no reviewers
        reviewersTable.setPlaceholder(new Label("No users with reviewer privileges found"));

        // Add back button to return to respective role's home page
        Button backButton = UIFactory.createBackButton(context);

        // Add homepage button
        Button homeButton = UIFactory.createHomepageButton("Question Display", context);

        // Button container
        HBox buttonContainer = new HBox(10, backButton, homeButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10, 0, 0, 0));

        // Add everything to the layout
        layout.getChildren().addAll(
                titleLabel,
                instructionLabel,
                reviewersTable,
                buttonContainer
        );

        return layout;
    }

    /**
     * Loads all users with reviewer privileges into the table.
     */
    private void loadReviewers() {
        List<User> allUsers = context.users().getAll();
        List<User> reviewers = new ArrayList<>();

        // Filter users to only include those with REVIEWER role
        for (User user : allUsers) {
            Roles[] roles = RolesUtil.intToRoles(user.getRoles());
            if (RolesUtil.hasRole(roles, Roles.REVIEWER)) {
                reviewers.add(user);
            }
        }

        reviewersData = FXCollections.observableArrayList(reviewers);
        reviewersTable.setItems(reviewersData);
    }

    /**
     * Handles the removal of the reviewer role from a user.
     *
     * @param user The user from whom to remove the reviewer role.
     */
    private void handleRemoveReviewerRole(User user) {
        try {
            // Remove the REVIEWER role
            int updatedRoles = RolesUtil.removeRole(user.getRoles(), Roles.REVIEWER);
            user.setRoles(updatedRoles);

            // Update the user in the database
            User updatedUser = context.users().update(user);

            if (updatedUser != null) {
                UIFactory.showAlert(Alert.AlertType.INFORMATION, "Role Removed",
                        "Reviewer role has been removed from " + user.getUserName() + ".");

                // Refresh the table by calling loadReviewers again
                loadReviewers();
            } else {
                UIFactory.showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to update user roles.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIFactory.showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while removing reviewer role: " + e.getMessage());
        }
    }
}