package application.framework.builders;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder for a simple pop‑out dialog containing a single {@link TextField} and OK/Cancel buttons.
 * <p>
 * Allows custom title, header, placeholder, pre‑filled text and field width. Call {@link #showAndWait()}
 * to block and retrieve the user‑entered value.
 * </p>
 *
 * @author Dhruv
 */
public class InputDialogBuilder {
    private final Dialog<String> dialog = new Dialog<>();
    private final TextField inputField = new TextField();

    public InputDialogBuilder() {
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(20, 150, 10, 10));
        pane.add(new Label("Input:"), 0, 0);
        pane.add(inputField, 1, 0);
        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? inputField.getText() : null);
    }

    /**
     * Sets the window title.
     */
    public InputDialogBuilder title(String t) {
        dialog.setTitle(t);
        return this;
    }

    /**
     * Sets optional header text.
     */
    public InputDialogBuilder header(String h) {
        dialog.setHeaderText(h);
        return this;
    }

    /**
     * Placeholder text shown when the field is empty.
     */
    public InputDialogBuilder placeholder(String p) {
        inputField.setPromptText(p);
        return this;
    }

    /**
     * Pre‑fills the text field.
     */
    public InputDialogBuilder defaultText(String txt) {
        inputField.setText(txt);
        return this;
    }

    /**
     * Max width of the text field.
     */
    public InputDialogBuilder fieldWidth(double w) {
        inputField.setMaxWidth(w);
        return this;
    }

    public Dialog<String> build() {
        return dialog;
    }

    /**
     * Convenience – builds, shows and waits, returning the string or empty if cancelled.
     */
    public Optional<String> showAndWait() {
        return dialog.showAndWait();
    }

    // Allow additional customisations via lambdas
    public InputDialogBuilder with(Consumer<TextField> cfg) {
        cfg.accept(inputField);
        return this;
    }
}