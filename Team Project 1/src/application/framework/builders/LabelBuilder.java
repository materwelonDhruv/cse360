package application.framework.builders;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Builder class for creating and customizing a {@link Label}.
 *
 * @author Dhruv
 */
public class LabelBuilder {
    private final Label label;

    /**
     * Initializes the builder with the given text for the label.
     */
    public LabelBuilder(String text) {
        label = new Label(text);
        label.setStyle(application.framework.DesignGuide.TITLE_LABEL);
    }

    /**
     * Sets a custom style for the label.
     */
    public LabelBuilder style(String style) {
        label.setStyle(style);
        return this;
    }

    /**
     * Sets the text color for the label.
     */
    public LabelBuilder textFill(Color color) {
        label.setTextFill(color);
        return this;
    }

    /**
     * Builds and returns the {@link Label} instance.
     */
    public Label build() {
        return label;
    }
}
