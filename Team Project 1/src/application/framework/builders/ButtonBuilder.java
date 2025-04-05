package application.framework.builders;

import application.AppContext;
import application.framework.MyPages;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Builder class for creating and customizing a {@link Button}.
 *
 * @author Dhruv
 */
public class ButtonBuilder {
    private final Button button;

    /**
     * Initializes the builder with the given text for the button.
     */
    public ButtonBuilder(String text) {
        button = new Button(text);
    }

    /**
     * Sets the action handler for the button when clicked.
     */
    public ButtonBuilder onAction(EventHandler<ActionEvent> handler) {
        button.setOnAction(handler);
        return this;
    }

    /**
     * Sets the action to navigate to a specific page when the button is clicked.
     */
    public ButtonBuilder routeToPage(MyPages page, AppContext context) {
        button.setOnAction(e -> context.router().navigate(page));
        return this;
    }

    /**
     * Builds and returns the {@link Button} instance.
     */
    public Button build() {
        return button;
    }

    /**
     * Returns the {@link Button} instance created by the builder.
     */
    public Button getSource() {
        return button;
    }

    /**
     * Disables the button.
     */
    public ButtonBuilder disable() {
        button.setDisable(true);
        return this;
    }
}
