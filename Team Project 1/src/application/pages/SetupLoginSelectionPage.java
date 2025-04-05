package application.pages;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Represents the initial account setup selection page.
 * This page provides users with options to either set up a new account or log in to an existing one.
 * It features two buttons for navigation: one for account setup and one for login.
 */
@Route(MyPages.SETUP_LOGIN)
@View(title = "Account Setup")
public class SetupLoginSelectionPage extends BasePage {

    /**
     * Default constructor for SetupLoginSelectionPage.
     */
    public SetupLoginSelectionPage() {
        super();
    }

    /**
     * Creates the UI layout for the setup/login selection page.
     * Includes buttons to navigate to the account setup or login pages.
     *
     * @return A VBox containing the UI elements for this page.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Button setupButton = UIFactory.createButton("SetUp", e -> e.routeToPage(MyPages.SETUP_ACCOUNT, context));
        Button loginButton = UIFactory.createButton("Login", e -> e.routeToPage(MyPages.USER_LOGIN, context));

        layout.getChildren().addAll(setupButton, loginButton);
        return layout;
    }
}