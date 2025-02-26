package application.pages;

import application.framework.BasePage;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a
 * new account or logging into an existing account.
 * Provides two buttons for navigation.
 */
@src.application.framework.Route(src.application.framework.MyPages.SETUP_LOGIN)
@src.application.framework.View(title = "Account Setup")
public class SetupLoginSelectionPage extends BasePage {

    public SetupLoginSelectionPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        Button setupButton = src.application.framework.UIFactory.createButton("SetUp", e -> {
            context.router().navigate(src.application.framework.MyPages.SETUP_ACCOUNT);
        });
        Button loginButton = src.application.framework.UIFactory.createButton("Login", e -> {
            context.router().navigate(src.application.framework.MyPages.USER_LOGIN);
        });

        layout.getChildren().addAll(setupButton, loginButton);
        return layout;
    }
}