package application.framework.builders;

import javafx.scene.control.CheckBox;

/**
 * Builder class for creating and customizing a {@link CheckBox}.
 *
 * @author Dhruv
 */
public class CheckBoxBuilder {
    private final CheckBox checkBox;

    /**
     * Initializes the builder with the given text for the checkbox.
     */
    public CheckBoxBuilder(String text) {
        checkBox = new CheckBox(text);
    }

    /**
     * Sets the selected state of the checkbox.
     */
    public CheckBoxBuilder selected(boolean selected) {
        checkBox.setSelected(selected);
        return this;
    }

    /**
     * Builds and returns the {@link CheckBox} instance.
     */
    public CheckBox build() {
        return checkBox;
    }
}
