package application.pages.admin;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;

/**
 *
 */
@Route(MyPages.ADMIN_PENDING)
@View(title = "Pending Admin Requests")
public class PendingAdminRequests extends BasePage {
    private final Roles currentRole = context.getSession().getCurrentRole();

    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        Label title = new Label("Pending Admin Requests");
        ListView<VBox> pendingAdminRequests = setupPendingRequests();
        layout.getChildren().addAll(title, pendingAdminRequests);
        return layout;
    }

    private ListView<VBox> setupPendingRequests() {
        ListView<VBox> tempView = new ListView<>();
        VBox infoVBox = new VBox(10);
        //TODO: add the title/description of the request from the DB
        VBox buttonVBox = new VBox(10);

        if (currentRole == Roles.ADMIN) {
            Button acceptButton = setupAcceptButton();
            Button rejectButton = setupRejectButton();
            buttonVBox.getChildren().addAll(acceptButton, rejectButton);
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

}
