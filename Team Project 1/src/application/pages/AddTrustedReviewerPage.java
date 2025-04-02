package application.pages;

import application.framework.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This page allows students to search for reviewers that are not on their trusted reviewer list and
 * select as many as they want to add to that list.
 */
@Route(MyPages.ADD_TRUSTED_REVIEWER)
@View(title = "Add Trusted Reviewers")
public class AddTrustedReviewerPage extends BasePage {
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);



        layout.getChildren().addAll();
        return layout;
    }
}