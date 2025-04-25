package application.framework.builders;

import application.framework.DesignGuide;
import application.framework.UIFactory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

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
    private final TextArea inputArea = new TextArea();
    private final Button okBtn;

    /**
     * functional validator; null means "no validation"
     */
    private Predicate<String> validator = null;
    private String validatorMessage = "Invalid input";

    public InputDialogBuilder() {
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        dialog.getDialogPane().setPrefWidth(300);

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(20, 20, 10, 20));

        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        cc.setFillWidth(true);
        pane.getColumnConstraints().add(cc);

        Label lbl = new Label("Input");
        lbl.setStyle(DesignGuide.BOLD_TEXT);
        pane.add(lbl, 0, 0);

        inputArea.setWrapText(true);
        inputArea.setPrefRowCount(5);
        inputArea.setMaxWidth(Double.MAX_VALUE);
        pane.add(inputArea, 0, 1);
        GridPane.setHgrow(inputArea, Priority.ALWAYS);
        GridPane.setVgrow(inputArea, Priority.ALWAYS);

        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(bt -> bt == ButtonType.OK ? inputArea.getText() : null);
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
        inputArea.setPromptText(p);
        return this;
    }

    /**
     * Pre‑fills the text field.
     */
    public InputDialogBuilder setDefaultText(String txt) {
        inputArea.setText(txt);
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

        // initial disable state
        okBtn.setDisable(!validator.test(inputArea.getText()));

        // listen for text changes and enable/disable OK button
        inputArea.textProperty().addListener((obs, old, val) -> {
            okBtn.setDisable(!validator.test(val));
        });

        inputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!validator.test(newValue)) {
                inputArea.setStyle(DesignGuide.INVALID_INPUT);
                inputArea.setTooltip(new Tooltip(validatorMessage));
            } else {
                inputArea.setStyle("");
                inputArea.setTooltip(null);
            }
        });

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
    public InputDialogBuilder with(Consumer<TextArea> cfg) {
        cfg.accept(inputArea);
        return this;
    }
}