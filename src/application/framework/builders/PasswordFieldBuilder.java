package application.framework.builders;

import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;

/**
 * Builder class for creating and customizing a {@link PasswordField}.
 *
 * @author Dhruv
 */
public class PasswordFieldBuilder {
    private final PasswordField passwordField;

    /**
     * Initializes the builder with the given prompt text for the password field.
     */
    public PasswordFieldBuilder(String prompt) {
        passwordField = new PasswordField();
        passwordField.setPromptText(prompt);
    }

    /**
     * Sets the maximum width for the password field.
     */
    public PasswordFieldBuilder maxWidth(double width) {
        passwordField.setMaxWidth(width);
        return this;
    }

    /**
     * Sets the minimum width for the password field.
     */
    public PasswordFieldBuilder minWidth(double width) {
        passwordField.setMinWidth(width);
        return this;
    }

    /**
     * Sets the maximum number of characters allowed in the password field.
     */
    public PasswordFieldBuilder maxChars(int max) {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > max) {
                passwordField.setText(oldValue);
                passwordField.setStyle("-fx-border-color: red;");
                passwordField.setTooltip(new Tooltip("Maximum " + max + " characters allowed"));
            }
        });
        return this;
    }

    /**
     * Sets the minimum number of characters required in the password field.
     */
    public PasswordFieldBuilder minChars(int min) {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < min) {
                passwordField.setStyle("-fx-border-color: red;");
                passwordField.setTooltip(new Tooltip("Minimum " + min + " characters required"));
            } else {
                passwordField.setStyle("");
            }
        });
        return this;
    }

    /**
     * Builds and returns the {@link PasswordField} instance.
     */
    public PasswordField build() {
        return passwordField;
    }
}
