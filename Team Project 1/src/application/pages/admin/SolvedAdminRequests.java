package application.pages.admin;

import application.framework.BasePage;
import application.framework.MyPages;
import application.framework.Route;
import application.framework.View;
import database.model.entities.AdminRequest;
import javafx.scene.layout.Pane;
import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import utils.requests.RequestState;

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
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Solved Admin Requests");

        loadSolvedRequests();

        // Button to reopen the selected admin request
        Button reopenRequestButton = UIFactory.createButton("Reopen Selected Request", e -> e.onAction(a -> {
            Pair<Integer,VBox> selectedRequest = requestView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                //TODO: navigate to usermodifypage?
            }
        }));

        // Button to navigate to the previous page
        Button backButton = UIFactory.createBackButton(context);

        layout.getChildren().addAll(titleLabel, requestView, reopenRequestButton, backButton);
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
     * Creates a VBox containing the title and description of the given request
     *
     * @param request the {@link AdminRequest} to create a VBox for
     * @return the VBox containing the request information
     */
    private VBox createRequestVBox(AdminRequest request) {
        // Label for the Request's title
        //Label titleLabel = new Label(request.getTitle() + " | " + Helpers.formatTimestamp(announcement.getMessage().getCreatedAt()));
        //titleLabel.setStyle("-fx-font-weight: bold");

        // Label for the announcement's content
        //Label contentLabel = new Label(announcement.getMessage().getContent());

        return new VBox();
    }
}