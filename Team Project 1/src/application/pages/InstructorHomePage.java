package application.pages;

import application.framework.*;
import database.model.entities.ReviewerRequest;
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

import java.util.List;

@Route(MyPages.INSTRUCTOR_HOME)
@View(title = "Instructor Page")
public class InstructorHomePage extends BasePage {
    private TableView<ReviewerRequest> requestTable;
    private ObservableList<ReviewerRequest> requestsData;

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label welcomeLabel = UIFactory.createLabel("Hello, Instructor!");
        welcomeLabel.getStyleClass().add("heading");

        Label requestsLabel = UIFactory.createLabel("Pending Reviewer Requests");
        requestsLabel.getStyleClass().add("subheading");

        // Create the table for displaying reviewer requests
        requestTable = new TableView<>();
        requestTable.setPrefHeight(300);
        VBox.setVgrow(requestTable, Priority.ALWAYS);

        // Set up the table columns
        TableColumn<ReviewerRequest, String> requesterCol = new TableColumn<>("Requester");
        requesterCol.setPrefWidth(150);
        requesterCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRequester().getUserName()));

        TableColumn<ReviewerRequest, String> dateCol = new TableColumn<>("Request Date");
        dateCol.setPrefWidth(200);
        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCreatedAt().toString()));

        TableColumn<ReviewerRequest, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final HBox buttonBox = new HBox(10);

            {
                buttonBox.setAlignment(Pos.CENTER);

                Button approveButton = UIFactory.createButton("Approve", e -> e.onAction(a ->
                {
                    ReviewerRequest request = getTableView().getItems().get(getIndex());
                    handleRequestAction(request, true);
                }));

                Button rejectButton = UIFactory.createButton("Reject", e -> e.onAction(a ->
                {
                    ReviewerRequest request = getTableView().getItems().get(getIndex());
                    handleRequestAction(request, false);
                }));

                buttonBox.getChildren().addAll(approveButton, rejectButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        requestTable.getColumns().addAll(requesterCol, dateCol, actionsCol);

        // Get pending reviewer requests for this instructor
        User instructor = context.getSession().getActiveUser();
        List<ReviewerRequest> pendingRequests = context.reviewerRequests().getRequestsByInstructor(instructor.getId());

        // Filter to only show pending requests (where status is null)
        pendingRequests.removeIf(request -> request.getStatus() != null);

        requestsData = FXCollections.observableArrayList(pendingRequests);
        requestTable.setItems(requestsData);

        // Add placeholder text when there are no requests
        requestTable.setPlaceholder(new Label("No pending reviewer requests"));

        // Add a refresh button
        Button refreshButton = UIFactory.createButton("Refresh Requests", e -> {
            refreshRequestsTable();
        });

        // Add logout button
        Button logoutButton = UIFactory.createButton("Logout", e -> {
            e.routeToPage(MyPages.USER_LOGIN, context);
        });

        // Button container
        HBox buttonContainer = new HBox(10, refreshButton, logoutButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10, 0, 0, 0));

        // Add everything to the layout
        layout.getChildren().addAll(
                welcomeLabel,
                requestsLabel,
                requestTable,
                buttonContainer
        );

        return layout;
    }

    /**
     * Handle approve/reject actions for reviewer requests
     */
    private void handleRequestAction(ReviewerRequest request, boolean approved) {
        try {
            if (approved) {
                // Accept the request
                ReviewerRequest updatedRequest = context.reviewerRequests().acceptRequest(request.getId());

                if (updatedRequest != null) {
                    // Update the user's role
                    User requester = request.getRequester();

                    // Add the REVIEWER role to the user
                    Roles[] all_roles = RolesUtil.intToRoles(requester.getRoles());

                    if (!RolesUtil.hasRole(all_roles, Roles.REVIEWER)) {
                        requester.setRoles(RolesUtil.addRole(requester.getRoles(), Roles.REVIEWER));
                        context.users().update(requester);
                    }

                    UIFactory.showAlert(Alert.AlertType.INFORMATION, "Request Approved",
                            "You have approved " + requester.getUserName() + " as a reviewer.");
                }
            } else {
                // Reject the request
                ReviewerRequest updatedRequest = context.reviewerRequests().rejectRequest(request.getId());

                if (updatedRequest != null) {
                    UIFactory.showAlert(Alert.AlertType.INFORMATION, "Request Rejected",
                            "You have rejected the reviewer request from " + request.getRequester().getUserName() + ".");
                }
            }

            // Refresh the table
            refreshRequestsTable();

        } catch (Exception e) {
            e.printStackTrace();
            UIFactory.showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while processing the request: " + e.getMessage());
        }
    }

    /**
     * Refresh the requests table with current data
     */
    private void refreshRequestsTable() {
        User instructor = context.getSession().getActiveUser();
        List<ReviewerRequest> pendingRequests = context.reviewerRequests().getRequestsByInstructor(instructor.getId());

        // Filter to only show pending requests (where status is null)
        pendingRequests.removeIf(request -> request.getStatus() != null);

        requestsData.clear();
        requestsData.addAll(pendingRequests);
    }
}