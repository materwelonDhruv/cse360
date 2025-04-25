package application.pages.admin;

import application.framework.*;
import database.model.entities.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import utils.requests.AdminActions;
import utils.requests.RequestState;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class to display all pending AdminRequests
 */
@Route(MyPages.ADMIN_PENDING)
@View(title = "Pending Admin Requests")
public class PendingAdminRequests extends BasePage {
    private final Roles role = context.getSession().getCurrentRole();
    private final User user = context.getSession().getActiveUser();
    private final Roles currentRole = context.getSession().getCurrentRole();
    private ArrayList<Message> adminRequests;

    public Pane createView() {
        BorderPane view = new BorderPane();
        view.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        Label title = new Label("Pending Admin Requests");
        ListView<HBox> pendingAdminRequests = setupPendingRequests();
        VBox titleVBox = new VBox(10);
        titleVBox.getChildren().addAll(title, pendingAdminRequests);
        view.setCenter(titleVBox);
        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Button backButton = UIFactory.createBackButton(context);
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        toolbar.getChildren().addAll(backButton, logoutButton);
        view.setBottom(toolbar);
        return view;
    }

    private ListView<HBox> setupPendingRequests() {
        ListView<HBox> tempView = new ListView<>();
        ArrayList<AdminRequest> pendingRequests = new ArrayList<>();
        try {
            pendingRequests = setupPendingRequestsArrayList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(pendingRequests.size());
        for (AdminRequest a : pendingRequests) {
            HBox row = buildRequestRow(a, tempView);
            tempView.getItems().add(row);
        }
        return tempView;
    }

    private Button setupAcceptButton(AdminRequest m, ListView<HBox> listView) {
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(event -> {
            switch (m.getType()) {
                case AdminActions.DeleteUser:
                    context.users().delete(m.getTarget().getId());
                    break;
                case AdminActions.UpdateRole:
                    User target = m.getTarget();
                    target.setRoles(m.getContext());
                    context.users().update(target);
                    m.setState(RequestState.Accepted);
                    break;
                case AdminActions.RequestPassword:
                    sendOTP(m);
                    break;
                default:
                    break;
            }
            m.setState(RequestState.Accepted);
            context.adminRequests().update(m);
            updateListView(listView);
        });
        return acceptButton;
    }

    private Button setupRejectButton(AdminRequest m, ListView<HBox> listView) {
        Button rejectButton = new Button("Reject");
        rejectButton.setOnAction(event -> {
            m.setState(RequestState.Denied);
            context.adminRequests().update(m);
            updateListView(listView);
        });
        return rejectButton;
    }

    private ArrayList<AdminRequest> setupPendingRequestsArrayList() throws SQLException {
        ArrayList<AdminRequest> pendingRequests = (ArrayList<AdminRequest>) context.adminRequests().filterFetch(RequestState.Pending);
        System.out.println(pendingRequests);
        return pendingRequests;
    }

    private void sendOTP(AdminRequest m) {
        // Generate one-time password using current active user's ID as the issuer.
        OneTimePassword newPass = new OneTimePassword(m.getRequester().getId(), m.getTarget().getId());
        context.oneTimePasswords().create(newPass);
        System.out.println("New password: " + newPass.getPlainOtp());
        Message otpMessage = new Message(m.getRequester().getId(), newPass.getPlainOtp());
        User requester = m.getRequester();
        requester.setRoles(RolesUtil.addRole(requester.getRoles(), Roles.STAFF));
        context.users().update(requester);
        StaffMessage sm = new StaffMessage(otpMessage, context.getSession().getActiveUser(), m.getRequester());
        context.staffMessages().create(sm);
        requester.setRoles(RolesUtil.removeRole(requester.getRoles(), Roles.STAFF));
        context.users().update(requester);
        m.setContext(newPass.getId());
    }

    private void updateListView(ListView<HBox> listView) {
        listView.getItems().clear();
        try {
            ArrayList<AdminRequest> updatedRequests = setupPendingRequestsArrayList();
            for (AdminRequest a : updatedRequests) {
                HBox row = buildRequestRow(a, listView);  // extracted for reuse
                listView.getItems().add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox buildRequestRow(AdminRequest a, ListView<HBox> listView) {
        HBox row = new HBox(10);
        Label title = new Label("...");
        title.setPrefWidth(150);
        title.setWrapText(true);
        Label requesterName = new Label("Requester: " + a.getRequester().getUserName());
        Label infoLabel = new Label("...");
        Label targetUsername = new Label("Target: " + a.getTarget().getUserName());

        title.setText(a.getReason());
        row.getChildren().addAll(title, requesterName, infoLabel, targetUsername);
        switch (a.getType()) {
            case AdminActions.DeleteUser:
                infoLabel.setText("Delete");
                break;
            case AdminActions.UpdateRole:
                infoLabel.setText("Change Role");
                Label newRoleLabel = new Label();

                Roles[] currentRoles = RolesUtil.intToRoles(a.getTarget().getRoles());
                Roles[] requestedRoles = RolesUtil.intToRoles(a.getContext());

                StringBuilder roles = new StringBuilder();

                // Detect removed roles
                for (Roles r : currentRoles) {
                    if (!RolesUtil.hasRole(a.getContext(), r)) {
                        roles.append("remove ").append(r).append(" ");
                    }
                }
                // Detect added roles
                for (Roles r : requestedRoles) {
                    if (!RolesUtil.hasRole(a.getTarget().getRoles(), r)) {
                        roles.append("add ").append(r).append(" ");
                    }
                }
                newRoleLabel.setWrapText(true);
                newRoleLabel.setPrefWidth(150);
                VBox.setVgrow(newRoleLabel, Priority.ALWAYS);
                newRoleLabel.setText(roles.toString().trim() + "\n");
                row.getChildren().add(newRoleLabel);
                break;
            case AdminActions.RequestPassword:
                infoLabel.setText("Password");
                break;
            default:
                infoLabel.setText("Null");
                break;
        }
        if (currentRole == Roles.ADMIN) {
            VBox buttonVBox = new VBox(10);
            Button acceptButton = setupAcceptButton(a, listView);
            Button rejectButton = setupRejectButton(a, listView);
            buttonVBox.getChildren().addAll(acceptButton, rejectButton);
            row.getChildren().add(buttonVBox);
            buttonVBox.setAlignment(Pos.CENTER_RIGHT);
        }
        return row;
    }
}
