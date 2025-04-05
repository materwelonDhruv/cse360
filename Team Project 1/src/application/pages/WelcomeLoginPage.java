package application.pages;

import application.framework.*;
import database.model.entities.User;
import database.repository.DataAccessException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
@Route(MyPages.WELCOME_LOGIN)
@View(title = "Role Select")
public class WelcomeLoginPage extends BasePage {

    /**
     * Creates and returns the main UI layout for the welcome screen.
     * This includes:
     * - A welcome message displaying the logged-in username.
     * - A dropdown menu (MenuButton) for role selection.
     * - A quit button to close the application safely.
     *
     * @return A Pane containing the UI elements for the welcome screen.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(5);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Get the active user from session
        User user = context.getSession().getActiveUser();
        String username = (user != null) ? user.getUserName() : "Guest";

        Label welcomeLabel = UIFactory.createLabel("Welcome " + username + "!!");

        Button quitButton = UIFactory.createButton("Quit",
                e -> e.onAction(a -> {
                    try {
                        context.closeConnection();
                    } catch (SQLException ex) {
                        throw new DataAccessException("Cannot close in WelcomePage", ex);
                    }
                    Platform.exit();
                }));

        // For multiple roles, use a dropdown (MenuButton) for selection
        MenuButton roleMenu = UIFactory.createNavMenu(context, "Select Role");


        layout.getChildren().addAll(welcomeLabel);
        if (roleMenu != null) {
            layout.getChildren().add(roleMenu);
        }
        layout.getChildren().add(quitButton);

        return layout;
    }
}