package application.pages;

import application.framework.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This page allows students to search for reviewers that are not on their trusted reviewer list and
 * select as many as they want to add to that list.
 */
@Route(MyPages.ADD_TRUSTED_REVIEWER)
@View(title = "Add Trusted Reviewers")
public class AddTrustedReviewerPage extends BasePage {
    // ListView to contain all reviewers not yet trusted by the student or the search results
    private static final ListView<String> resultView = new ListView<>();
    // TextField for searching reviewers by name
    private final TextField reviewerNameInput = UIFactory.createTextField("Search Reviewers By Name", f ->
            f.minChars(0).maxChars(30));

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Add Trusted Reviewers");

        // Set up resultView
        resultView.setFixedCellSize(26);
        resultView.setPlaceholder(new Label("No Existing Reviewers"));

        // Button to add all selected reviewers into the student's trusted reviewers list
        Button addButton = UIFactory.createButton("Add Selected Reviewers");

        // Button to cancel and route back to the TrustedReviewerPage
        Button cancelButton = UIFactory.createButton("Cancel", e -> e.routeToPage(MyPages.TRUSTED_REVIEWER, context));

        // HBox to contain the add and cancel buttons
        HBox buttonHBox = new HBox(20, addButton, cancelButton);
        buttonHBox.setAlignment(Pos.BASELINE_CENTER);

        layout.getChildren().addAll(titleLabel, reviewerNameInput, resultView, buttonHBox);
        return layout;
    }
}