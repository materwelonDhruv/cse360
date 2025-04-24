package application.pages.admin;

import application.framework.*;
import database.model.entities.Message;
import database.model.entities.OneTimePassword;
import database.model.entities.User;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;

import java.util.ArrayList;

/**
 * Class to display all pending AdminRequests
 */
@Route(MyPages.ADMIN_PENDING)
@View(title = "Pending Admin Requests")
public class PendingAdminRequests extends BasePage {
    private final User user = context.getSession().getActiveUser();
    private final Roles currentRole = context.getSession().getCurrentRole();
    private ArrayList<Message> adminRequests;

    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        Label title = new Label("Pending Admin Requests");
        ListView<HBox> pendingAdminRequests = setupPendingRequests();
        layout.getChildren().addAll(title, pendingAdminRequests);
        return layout;
    }

    private ListView<HBox> setupPendingRequests() {
        //TODO: un-messageify
        ListView<HBox> tempView = new ListView<>();
        HBox row = new HBox(10);
        Label title = new Label("...");
        Label requesterName = new Label("...");
        Label infoLabel = new Label("...");
        Label targetUsername = new Label("...");
        TextArea description = new TextArea("...");
        ArrayList<Message> pendingRequests = setupPendingRequestsArrayList();
        for (Message m : pendingRequests) {
            title.setText(m.getContent());
            requesterName.setText(m.getContent());
            switch (m.getContent()) {
                case "delete":
                    infoLabel.setText("Delete");
                    break;
                case "change role":
                    infoLabel.setText("Change Role");
                    break;
                case "password":
                    infoLabel.setText("Password");
                    break;
                default:
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

            targetUsername.setText(m.getContent());
            description.setText(m.getContent());
        }
        return tempView;
    }

    private Button setupAcceptButton(Message m) {
        //TODO: un-messageify
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(event -> {
            switch (m.getContent()) {
                case "delete":
                    context.users().delete(m.getId());
                    break;
                case "change role":
                    context.users().getById(0).setRoles(m.getId());
                    break;
                case "password":
                    sendOTP();
                    break;
                default:
                    break;
            }
        });
        return acceptButton;
    }

    private Button setupRejectButton() {
        Button rejectButton = new Button("Reject");
        return rejectButton;
    }

    private ArrayList<Message> setupPendingRequestsArrayList() {
        ArrayList<Message> pendingRequests = null;
        return pendingRequests;
    }

    private void sendOTP(Message m) {
        // Generate one-time password using current active user's ID as the issuer.
        //TODO change this to make the issuer the sender of the request
        OneTimePassword newPass = new OneTimePassword(context.getSession().getActiveUser().getId(), m.getId());
        context.oneTimePasswords().create(newPass);
        System.out.println("New password: " + newPass.getPlainOtp());
        Message otpMessage = new Message(user.getId(), "");
        context.privateMessages().create();
    }
}
