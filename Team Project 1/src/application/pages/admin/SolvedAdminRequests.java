package application.pages.admin;

import application.framework.*;
import database.model.entities.AdminRequest;
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
import utils.requests.RequestState;

import java.sql.SQLException;
import java.util.ArrayList;

@Route(MyPages.ADMIN_SOLVED)
@View(title = "Solved Admin Requests")
public class SolvedAdminRequests extends BasePage {
    private final Roles role = context.getSession().getCurrentRole();

    public Pane createView() {
        BorderPane view = new BorderPane();
        view.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        Label title = new Label("Solved Admin Requests");
        ListView<HBox> solvedAdminRequests = setupSolvedRequests();
        VBox titleVBox = new VBox(10);
        titleVBox.getChildren().addAll(title, solvedAdminRequests);
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

    private ListView<HBox> setupSolvedRequests() {
        ListView<HBox> tempView = new ListView<>();
        HBox row = new HBox(10);
        Label title = new Label("...");
        Label requesterName = new Label("...");
        Label infoLabel = new Label("...");
        Label targetUsername = new Label("...");
        TextArea description = new TextArea("...");
        ArrayList<AdminRequest> solvedRequests = new ArrayList<>();
        try {
            solvedRequests = setupSolvedRequestsArrayList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (AdminRequest m : solvedRequests) {
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

            targetUsername.setText(m.getTarget().getUserName());
        }
        return tempView;
    }

    private ArrayList<AdminRequest> setupSolvedRequestsArrayList() throws SQLException {
        ArrayList<AdminRequest> allRequests = (ArrayList<AdminRequest>) context.adminRequests().getAll();
        ArrayList<AdminRequest> solvedRequests = new ArrayList<>();
        for (AdminRequest m : allRequests) {
            if (m.getState() == RequestState.Denied) {
                solvedRequests.add(m);
            }
        }
        return solvedRequests;
    }
}
