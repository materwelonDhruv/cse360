package application.pages;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a
 * new account or logging into an existing account.
 * Provides two buttons for navigation.
 */
@Route(MyPages.SETUP_LOGIN)
@View(title = "Account Setup")
public class SetupLoginSelectionPage extends BasePage {

    public SetupLoginSelectionPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Button setupButton = UIFactory.createButton("SetUp", e -> {
            context.router().navigate(MyPages.SETUP_ACCOUNT);
        });
        Button loginButton = UIFactory.createButton("Login", e -> {
            context.router().navigate(MyPages.USER_LOGIN);
        });

        layout.getChildren().addAll(setupButton, loginButton);
        return layout;
    }
}