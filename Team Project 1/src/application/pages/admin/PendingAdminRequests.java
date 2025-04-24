package application.pages.admin;

import application.framework.*;
import database.model.entities.Message;
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
    private final Roles currentRole = context.getSession().getCurrentRole();

    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        Label title = new Label("Pending Admin Requests");
        ListView<HBox> pendingAdminRequests = setupPendingRequests();
        layout.getChildren().addAll(title, pendingAdminRequests);
        return layout;
    }

    private ListView<HBox> setupPendingRequests() {
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
                Button acceptButton = setupAcceptButton();
                Button rejectButton = setupRejectButton();
                buttonVBox.getChildren().addAll(acceptButton, rejectButton);
                row.getChildren().add(buttonVBox);
                buttonVBox.setAlignment(Pos.CENTER_RIGHT);
            }

            targetUsername.setText(m.getContent());
            description.setText(m.getContent());
        }
        return tempView;
    }

    private Button setupAcceptButton() {
        Button acceptButton = new Button("Accept");
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
}
