package src.application.framework;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;

import java.util.function.Supplier;

/**
 * Utility class to reduce JavaFX boilerplate.
 */
public final class UIFactory {

    private UIFactory() {
    }

    /**
     * Creates a Button with the given text and optional action handler.
     */
    public static Button createButton(String text, EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        if (handler != null) {
            button.setOnAction(handler);
        }
        return button;
    }

    /**
     * Creates a TextField with a prompt and optional max width.
     */
    public static TextField createTextField(String prompt, double maxWidth) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        if (maxWidth > 0) {
            tf.setMaxWidth(maxWidth);
        }
        return tf;
    }

    /**
     * Creates a PasswordField with a prompt and optional max width.
     */
    public static PasswordField createPasswordField(String prompt, double maxWidth) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        if (maxWidth > 0) {
            pf.setMaxWidth(maxWidth);
        }
        return pf;
    }

    /**
     * Creates a Label with optional style and text fill color.
     */
    public static Label createLabel(String text, String style, Color color) {
        Label label = new Label(text);
        if (style != null && !style.isEmpty()) {
            label.setStyle(style);
        }
        if (color != null) {
            label.setTextFill(color);
        }
        return label;
    }

    /**
     * Creates a generic CheckBox.
     */
    public static CheckBox createCheckBox(String text, boolean selected) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(selected);
        return cb;
    }

    /**
     * Creates a copy button that, when clicked, copies text provided by textSupplier
     * to the clipboard, disables itself, and changes its text to "Copied!".
     */
    public static Button createCopyButton(String initialText, Supplier<String> textSupplier) {
        Button btn = new Button(initialText);
        btn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(textSupplier.get());
            clipboard.setContent(content);
            btn.setDisable(true);
            btn.setText("Copied!");
        });
        return btn;
    }
}