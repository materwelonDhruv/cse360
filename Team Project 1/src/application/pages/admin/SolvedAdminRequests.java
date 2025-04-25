package application.pages.admin;

import application.framework.*;
import database.model.entities.AdminRequest;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import application.framework.BasePage;
import application.framework.MyPages;
import application.framework.Route;
import application.framework.View;
import database.model.entities.AdminRequest;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import application.framework.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import utils.permissions.Roles;
import utils.requests.AdminActions;
import utils.requests.RequestState;

import java.sql.SQLException;
import java.util.List;

/**
 *  Displays the list of all closed/solved admin requests and allows Instructors
 *  to reopen a closed admin request and update its description.
 *
 * @author Tyler
 */
@Route(MyPages.ADMIN_SOLVED)
@View(title = "Solved Admin Requests")
public class SolvedAdminRequests extends BasePage {
    // ListView containing all solved admin requests
    private final ListView<Pair<Integer,VBox>> requestView = new ListView<>();

    /**
     * Creates the layout for the SolvedAdminRequests Page.
     * @return A Pane containing the layout of the page.
     */
    @Override
    public Pane createView() {
        context.adminRequests().setState(3, RequestState.Accepted);
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Solved Admin Requests");

        // Set up requestView
        requestView.setPlaceholder(UIFactory.createLabel("No Solved Admin Requests"));
        requestView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<Integer, VBox> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item.getValue());
                }
            }
        });

        loadSolvedRequests();

        // Button to reopen the selected admin request
        Button reopenRequestButton = UIFactory.createButton("Reopen Selected Request", e -> e.onAction(a -> {
            Pair<Integer,VBox> selectedRequest = requestView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                try {
                    AdminRequest request = context.adminRequests().getById(selectedRequest.getKey());
                    if (request.getType() != AdminActions.DeleteUser) {
                        AdminUserModifyPage.setTargetUser(request.getTarget());
                        AdminUserModifyPage.setExistingRequest(request);
                        context.router().navigate(MyPages.ADMIN_USER_MODIFY);
                    } else {
                        Alert warning = new Alert(Alert.AlertType.WARNING, "Delete Requests may not be reopened.");
                        warning.showAndWait();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }));

        // Button to navigate to the previous page
        Button backButton = UIFactory.createBackButton(context);

        layout.getChildren().addAll(titleLabel, requestView);
        if (context.getSession().getCurrentRole() == Roles.INSTRUCTOR) layout.getChildren().add(reopenRequestButton);
        layout.getChildren().add(backButton);
        return layout;
    }

    /**
     * Loads all solved requests from the database into the requestView
     * sorted by newest to oldest.
     */
    private void loadSolvedRequests() {
        requestView.getItems().clear();
        List<AdminRequest> solvedRequests = context.adminRequests().filterFetch(RequestState.Accepted);
        for (AdminRequest request : solvedRequests) {
            requestView.getItems().addFirst(new Pair<>(request.getId(), createRequestVBox(request)));
        }
    }

    /**
     * Creates a VBox containing the title and description of the given request.
     *
     * @param request the {@link AdminRequest} to create a VBox for.
     * @return the VBox containing the request information.
     */
    private VBox createRequestVBox(AdminRequest request) {
        Label requesterLabel = new Label("Requester:");
        requesterLabel.setStyle("-fx-font-weight: bold");
        Label targetLabel = new Label("Target:");
        targetLabel.setStyle("-fx-font-weight: bold");
        Label actionTypeLabel = new Label("Action:");
        actionTypeLabel.setStyle("-fx-font-weight: bold");
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: bold");
        Label requesterNameLabel = new Label(request.getRequester().getUserName());
        Label targetNameLabel = new Label(request.getTarget().getUserName());
        Label actionTypeNameLabel = new Label(request.getType().name());
        Label statusNameLabel = new Label(request.getState().name());
        HBox topLabels = new HBox(10,
                requesterLabel,
                requesterNameLabel,
                targetLabel,
                targetNameLabel,
                actionTypeLabel,
                actionTypeNameLabel,
                statusLabel,
                statusNameLabel
        );
        Label reasonLabel = new Label("Reason:");
        reasonLabel.setStyle("-fx-font-weight: bold");
        Label reasonNameLabel = new Label(request.getReason());
        HBox reasonBox = new HBox(10, reasonLabel, reasonNameLabel);
        return new VBox(5,topLabels, reasonBox);
    }
}
