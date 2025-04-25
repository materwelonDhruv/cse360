package application.pages.staff;

import application.framework.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Provides an interface allowing staff to navigate to any non-admin
 * page that they choose.
 *
 * @author Tyler
 */
@Route(MyPages.STAFF_NAVIGATION)
@View(title = "Navigate To A Page")
public class StaffNavigationPage extends BasePage {
    /**
     * Creates the layout for the StaffNavigationPage
     *
     * @return layout
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Label for the title of the page
        Label titleLabel = UIFactory.createLabel("Navigate To A Page");
        titleLabel.getStyleClass().add("heading");

        // ListView for the names of pages that the staff can navigate to
        ListView<String> pageView = new ListView<>();

        // Add valid pages into the pageView
        int homePageOrdinal = MyPages.USER_HOME.ordinal();
        for (MyPages page : MyPages.values()) {
            if (page.ordinal() < homePageOrdinal) {
                continue;
            }
            pageView.getItems().add(page.name());
        }

        // Button to route to the selected page
        Button navigateButton = UIFactory.createButton("Navigate To Selected Page", e -> e.onAction(a -> {
            String selectedPage = pageView.getSelectionModel().getSelectedItem();
            if (selectedPage != null) {
                context.router().navigate(MyPages.valueOf(selectedPage));
            }
        }));

        // Button to route back to the TrustedReviewerPage
        Button backButton = UIFactory.createBackButton(context);

        // HBox to contain the add and cancel buttons
        HBox buttonHBox = new HBox(20, navigateButton, backButton);
        buttonHBox.setAlignment(Pos.BASELINE_CENTER);

        layout.getChildren().addAll(titleLabel, pageView, buttonHBox);
        return layout;
    }
}