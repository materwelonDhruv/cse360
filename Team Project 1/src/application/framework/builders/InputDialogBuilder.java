package application.framework.builders;

import application.framework.UIFactory;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Builder for a pop‑out dialog containing a single {@link TextField} and OK/Cancel buttons.
 * <p>
 * Allows custom title, header, placeholder, pre‑filled text, field width, and validator.  Call {@link #showAndWait()}
 * to block and retrieve the user‑entered value.
 * </p>
 */
public class InputDialogBuilder {
    private final Dialog<String> dialog = new Dialog<>();
    private final TextField inputField = new TextField();
    /**
     * functional validator; null means "no validation"
     */
    private Predicate<String> validator = null;
    private String validatorMessage = "Invalid input";

    public InputDialogBuilder() {
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(20, 150, 10, 10));
        pane.add(new Label("Input:"), 0, 0);
        pane.add(inputField, 1, 0);
        dialog.getDialogPane().setContent(pane);

        // disable OK until validation passes (if validator set)
        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (validator != null) {
            okBtn.disableProperty().bind(
                    Bindings.createBooleanBinding(
                            () -> !validator.test(inputField.getText()),
                            inputField.textProperty())
            );
        }

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? inputField.getText() : null);
    }

    /**
     * Sets the window title.
     */
    public InputDialogBuilder setTitle(String t) {
        dialog.setTitle(t);
        return this;
    }

    /**
     * Sets optional header text.
     */
    public InputDialogBuilder setHeader(String h) {
        dialog.setHeaderText(h);
        return this;
    }

    /**
     * Placeholder text shown when the field is empty.
     */
    public InputDialogBuilder setPlaceholder(String p) {
        inputField.setPromptText(p);
        return this;
    }

    /**
     * Pre‑fills the text field.
     */
    public InputDialogBuilder setDefaultText(String txt) {
        inputField.setText(txt);
        return this;
    }

    /**
     * Max width of the text field.
     */
    public InputDialogBuilder setFieldWidth(double w) {
        inputField.setMaxWidth(w);
        return this;
    }

    /**
     * Adds an optional validator. Provide a predicate that returns true for valid input.
     * If validation fails the OK button stays disabled and {@link #showAndWaitValidated()} will
     * throw {@link IllegalArgumentException} with the supplied message.
     */
    public InputDialogBuilder attachValidator(Predicate<String> v, String errMessage) {
        this.validator = v;
        this.validatorMessage = errMessage == null ? "Invalid input" : errMessage;
        return this;
    }

    public Dialog<String> build() {
        return dialog;
    }

    /**
     * builds, shows and waits, returning the string or empty if cancelled.
     * <p>
     * No need to call this. Automatically called by {@link UIFactory#showTextInput}.
     */
    public Optional<String> showAndWait() {
        return dialog.showAndWait();
    }

    /**
     * Same as {@link #showAndWait()} but enforces validation.
     * If the user presses OK with invalid input an {@link IllegalArgumentException} is thrown.
     * <p>
     * No need to call this. Automatically called by {@link UIFactory#showTextInput}.
     */
    public Optional<String> showAndWaitValidated() {
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent() && validator != null && !validator.test(res.get())) {
            throw new IllegalArgumentException(validatorMessage);
        }
        return res;
    }

    // Allow additional customisations via lambdas
    public InputDialogBuilder with(Consumer<TextField> cfg) {
        cfg.accept(inputField);
        return this;
    }
}