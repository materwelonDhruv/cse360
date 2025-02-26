package application.pages;

import application.framework.BasePage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the admin setup.
 */
@src.application.framework.Route(src.application.framework.MyPages.FIRST)
@src.application.framework.View(title = "Welcome")
public class FirstPage extends BasePage {

    public FirstPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        Label label = src.application.framework.UIFactory.createLabel(
                "Hello..You are the first person here.\nPlease select continue to setup administrator access",
                src.application.framework.DesignGuide.TITLE_LABEL,
                null
        );

        Button continueBtn = src.application.framework.UIFactory.createButton("Continue", e -> {
            context.router().navigate(src.application.framework.MyPages.ADMIN_SETUP);
        });

        layout.getChildren().addAll(label, continueBtn);
        return layout;
    }
}