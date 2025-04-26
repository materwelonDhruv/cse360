package application.framework.builders;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Builder class for creating and customizing a {@link TextField}.
 *
 * @author Dhruv
 */
public class TextFieldBuilder {
    private final TextField textField;
    private double maxWidth = -1;
    private double minWidth = -1;
    private int maxChars = -1;
    private int minChars = -1;

    /**
     * Initializes the builder with the given prompt text for the text field.
     */
    public TextFieldBuilder(String prompt) {
        textField = new TextField();
        textField.setPromptText(prompt);
    }

    /**
     * Sets the maximum width of the text field.
     */
    public TextFieldBuilder maxWidth(double width) {
        this.maxWidth = width;
        return this;
    }

    /**
     * Sets the minimum width of the text field.
     */
    public TextFieldBuilder minWidth(double width) {
        this.minWidth = width;
        return this;
    }

    /**
     * Sets the maximum number of characters allowed in the text field.
     */
    public TextFieldBuilder maxChars(int max) {
        this.maxChars = max;
        return this;
    }

    /**
     * Sets the minimum number of characters required in the text field.
     */
    public TextFieldBuilder minChars(int min) {
        this.minChars = min;
        return this;
    }

    /**
     * Sets the default text for the text field.
     */
    public TextFieldBuilder defaultText(String text) {
        textField.setText(text);
        return this;
    }

    /**
     * Sets the placeholder text for the text field.
     */
    public TextFieldBuilder placeholder(String text) {
        textField.setPromptText(text);
        return this;
    }

    public TextFieldBuilder onAction(EventHandler<ActionEvent> handler) {
        textField.setOnAction(handler);
        return this;
    }

    /**
     * Builds and returns the {@link TextField} instance.
     */
    public TextField build() {
        if (maxWidth > 0) {
            textField.setMaxWidth(maxWidth);
        }
        if (minWidth > 0) {
            textField.setMinWidth(minWidth);
        }
        if (maxChars > 0) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > maxChars) {
                    textField.setText(oldValue);
                    textField.setStyle("-fx-border-color: red;");
                    textField.setTooltip(new Tooltip("Maximum " + maxChars + " characters allowed"));
                }
            });
        }
        if (minChars > 0) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() < minChars) {
                    textField.setStyle("-fx-border-color: red;");
                    textField.setTooltip(new Tooltip("Minimum " + minChars + " characters required"));
                } else {
                    textField.setStyle("");
                }
            });
        }
        return textField;
    }
}
