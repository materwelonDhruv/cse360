package application.framework.builders;

import javafx.scene.control.Alert;

/**
 * Builder class for creating and customizing an {@link Alert}.
 *
 * @author Dhruv
 */
public class AlertBuilder {
    private final Alert alert;

    /**
     * Initializes the builder with the given alert type.
     */
    public AlertBuilder(Alert.AlertType type) {
        alert = new Alert(type);
    }

    /**
     * Sets the title of the alert.
     */
    public AlertBuilder title(String title) {
        alert.setTitle(title);
        return this;
    }

    /**
     * Sets the header text for the alert.
     */
    public AlertBuilder header(String header) {
        alert.setHeaderText(header);
        return this;
    }

    /**
     * Sets the content text for the alert.
     */
    public AlertBuilder content(String content) {
        alert.setContentText(content);
        return this;
    }

    /**
     * Builds and returns the {@link Alert} instance.
     */
    public Alert build() {
        return alert;
    }
}
