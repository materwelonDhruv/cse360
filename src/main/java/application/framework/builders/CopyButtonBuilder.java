package application.framework.builders;

import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.function.Supplier;

/**
 * Builder class for creating and customizing a {@link Button} that copies text to the clipboard.
 *
 * @author Dhruv
 */
public class CopyButtonBuilder extends ButtonBuilder {
    private final Button button;
    private final Supplier<String> textSupplier;

    /**
     * Initializes the builder with the given initial text and text supplier.
     */
    public CopyButtonBuilder(String initialText, Supplier<String> textSupplier) {
        super(initialText);
        button = super.getSource();
        this.textSupplier = textSupplier;
    }

    /**
     * Sets the action for copying the text to the clipboard when the button is clicked.
     */
    public CopyButtonBuilder onCopy() {
        button.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(textSupplier.get());
            clipboard.setContent(content);
            button.setDisable(true);
            button.setText("Copied!");
        });
        return this;
    }
}
