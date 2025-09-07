package application.pages;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the admin setup.
 *
 * @author Dhruv
 */
@Route(MyPages.FIRST)
@View(title = "Welcome")
public class FirstPage extends BasePage {

    public FirstPage() {
        super();
    }

    /**
     * @return layout
     * Builds the layout for FirstPage
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label label = UIFactory.createLabel(
                "Hello..You are the first person here.\nPlease select continue to setup administrator access"
        );

        Button continueBtn = UIFactory.createButton("Continue", e -> e.routeToPage(MyPages.ADMIN_SETUP, context));

        layout.getChildren().addAll(label, continueBtn);
        return layout;
    }
}