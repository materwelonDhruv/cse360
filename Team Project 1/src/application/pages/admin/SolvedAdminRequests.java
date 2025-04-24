package application.pages.admin;

import application.framework.BasePage;
import application.framework.MyPages;
import application.framework.Route;
import application.framework.View;
import javafx.scene.layout.Pane;
import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *  Displays the list of all closed/solved admin requests and allows Instructors
 *  to reopen a closed admin request and update its description.
 *
 * @author Tyler
 */
@Route(MyPages.ADMIN_SOLVED)
@View(title = "Solved Admin Requests")
public class SolvedAdminRequests extends BasePage {
    @Override
    public Pane createView() {
        return null;
    }
}
    /**
     * Creates the layout for the SolvedAdminRequests Page.
     * @return A Pane containing the layout of the page.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Solved Admin Requests");

        // ListView to display the solved requests
        ListView<String> requestView = new ListView<>();

        // Button to navigate to the previous page
        Button backButton = UIFactory.createBackButton(context);

        layout.getChildren().addAll(titleLabel, requestView, backButton);
        return layout;
    }
}
