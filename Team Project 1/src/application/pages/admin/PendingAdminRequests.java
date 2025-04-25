package application.pages.admin;

import application.framework.*;
import database.model.entities.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.requests.AdminActions;

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
        Button backButton = UIFactory.createHomepageButton("Back", context);
        if (role == Roles.INSTRUCTOR) {
            backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.INSTRUCTOR_HOME, context));
        } else if (role == Roles.ADMIN) {
            backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.ADMIN_HOME, context));
        } else {
            backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_HOME, context));
        }
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
        for (AdminRequest m : pendingRequests) {
            HBox row = new HBox(10);
            Label title = new Label("...");
            Label requesterName = new Label("...");
            Label infoLabel = new Label("...");
            Label targetUsername = new Label("...");
            TextArea description = new TextArea("...");

            title.setText(m.toString());
            requesterName.setText(m.getRequester().getUserName());
            switch (m.getType()) {
                case AdminActions.DeleteUser:
                    infoLabel.setText("Delete");
                    break;
                case AdminActions.UpdateRole:
                    infoLabel.setText("Change Role");
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
                Button acceptButton = setupAcceptButton(m);
                Button rejectButton = setupRejectButton(m);
                buttonVBox.getChildren().addAll(acceptButton, rejectButton);
                row.getChildren().add(buttonVBox);
                buttonVBox.setAlignment(Pos.CENTER_RIGHT);
            }
            targetUsername.setText(m.getTarget().getUserName());
            description.setText(m.getReason());
            tempView.getItems().add(row);
        }
        return tempView;
    }

    private Button setupAcceptButton(AdminRequest m) {
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(event -> {
            switch (m.getType()) {
                case AdminActions.DeleteUser:
                    context.users().delete(m.getTarget().getId());
                    break;
                case AdminActions.UpdateRole:
                    context.users().getById(m.getTarget().getId()).setRoles(m.getContext());
                    break;
                case AdminActions.RequestPassword:
                    sendOTP(m);
                    break;
                default:
                    break;
            }
        });
        return acceptButton;
    }

    private Button setupRejectButton(AdminRequest m) {
        Button rejectButton = new Button("Reject");
        return rejectButton;
    }

    private ArrayList<AdminRequest> setupPendingRequestsArrayList() throws SQLException {
        ArrayList<AdminRequest> pendingRequests = (ArrayList<AdminRequest>) context.adminRequests().getAll();
        System.out.println(pendingRequests);
        return pendingRequests;
    }

    private void sendOTP(AdminRequest m) {
        // Generate one-time password using current active user's ID as the issuer.
        OneTimePassword newPass = new OneTimePassword(m.getId(), m.getTarget().getId());
        context.oneTimePasswords().create(newPass);
        System.out.println("New password: " + newPass.getPlainOtp());
        Message otpMessage = new Message(m.getId(), newPass.getPlainOtp());
        StaffMessage sm = new StaffMessage(otpMessage, context.getSession().getActiveUser(), m.getRequester());
        context.staffMessages().create(sm);
    }
}
