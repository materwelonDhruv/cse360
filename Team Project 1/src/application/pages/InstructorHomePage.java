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

/**
 * The InstructorHomePage class provides an interface for instructors to manage
 * reviewer requests.
 * Instructors can approve or reject pending reviewer requests and navigate to
 * other sections.
 *
 * @author Atharva
 */
@Route(MyPages.INSTRUCTOR_HOME)
@View(title = "Instructor Page")
public class InstructorHomePage extends BasePage {
    private TableView<ReviewerRequest> requestTable;
    private ObservableList<ReviewerRequest> requestsData;

    /**
     * Creates and returns the main UI layout for the instructor's home page.
     * The page includes:
     * - A welcome message for instructors.
     * - A table displaying pending reviewer requests.
     * - Buttons to approve/reject requests, refresh, logout, change roles, and
     * navigate to the question display.
     *
     * @return A Pane containing the instructor's dashboard.
     */
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
        requesterCol
                .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequester().getUserName()));

        TableColumn<ReviewerRequest, String> dateCol = new TableColumn<>("Request Date");
        dateCol.setPrefWidth(200);
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt().toString()));

        TableColumn<ReviewerRequest, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final HBox buttonBox = new HBox(10);

            {
                buttonBox.setAlignment(Pos.CENTER);

                Button approveButton = UIFactory.createButton("Approve", e -> e.onAction(a -> {
                    ReviewerRequest request = getTableView().getItems().get(getIndex());
                    handleRequestAction(request, true);
                }));

                Button rejectButton = UIFactory.createButton("Reject", e -> e.onAction(a -> {
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

        // Button to go the question_display page
        Button questionDisplayButton = UIFactory.createHomepageButton(context);

        // Add a refresh button
        Button refreshButton = UIFactory.createButton("Refresh Requests", e -> {
            refreshRequestsTable();
        });

        //Button to navigate to remove reviewers role
        Button manageReviewerButton = UIFactory.createButton("Manage Reviewer Roles", b ->
                b.routeToPage(MyPages.REMOVE_REVIEWER, context));

        // Add logout button
        Button logoutButton = UIFactory.createLogoutButton(context);


        //menu button to operate requests to admin
        MenuButton requests_for_admin = new MenuButton("Admin Requests");
        MenuItem solved_requests = new MenuItem("Solved Requests");
        solved_requests.setOnAction(e -> context.router().navigate(MyPages.ADMIN_SOLVED));

        MenuItem pending_requests = new MenuItem("Pending Requests");
        pending_requests.setOnAction(e -> context.router().navigate(MyPages.ADMIN_PENDING));

        MenuItem create_requests = new MenuItem("Create Request");
        create_requests.setOnAction(e -> context.router().navigate(MyPages.ADMIN_USER));

        requests_for_admin.getItems().addAll(create_requests, pending_requests, solved_requests);


        // RoleMenu to change role
        MenuButton roleMenu = UIFactory.createNavMenu(context, "Select Role");

        Button privateMessageButton = new Button("Private Messages");
        privateMessageButton.setOnAction(e -> {
            context.router().navigate(MyPages.USER_QUESTION_DISPLAY);
        });

        // Button container for bottom of the page
        HBox buttonContainerBelow = new HBox(10, requests_for_admin, roleMenu, logoutButton);
        buttonContainerBelow.setAlignment(Pos.CENTER);
        buttonContainerBelow.setPadding(new Insets(10, 0, 0, 0));

        //Button container for top of the page
        HBox buttonContainerAbove = new HBox(10, questionDisplayButton, privateMessageButton, manageReviewerButton);
        buttonContainerAbove.setAlignment(Pos.CENTER);
        buttonContainerAbove.setPadding(new Insets(10, 0, 0, 0));

        // Add everything to the layout
        layout.getChildren().addAll(
                welcomeLabel,
                buttonContainerAbove,
                requestsLabel,
                requestTable,
                refreshButton,
                buttonContainerBelow);

        return layout;
    }

    /**
     * Handles the approval or rejection of a reviewer request.
     * If approved, the user is assigned the REVIEWER role.
     *
     * @param request  The reviewer request to process.
     * @param approved True if the request is approved, false if rejected.
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
                            "You have rejected the reviewer request from " + request.getRequester().getUserName()
                                    + ".");
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
     * Refreshes the reviewer requests table with the latest data.
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