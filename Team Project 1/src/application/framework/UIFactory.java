package application.framework;

import application.AppContext;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class to reduce JavaFX boilerplate.
 */
public final class UIFactory {

    private UIFactory() {
    }

    // TextField creation using builder lambdas
    @SafeVarargs
    public static TextField createTextField(String prompt, Consumer<TextFieldBuilder>... configs) {
        TextFieldBuilder builder = new TextFieldBuilder(prompt);
        for (Consumer<TextFieldBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    // Button creation using builder lambdas
    @SafeVarargs
    public static Button createButton(String text, Consumer<ButtonBuilder>... configs) {
        ButtonBuilder builder = new ButtonBuilder(text);
        for (Consumer<ButtonBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    // PasswordField creation using builder lambdas
    @SafeVarargs
    public static PasswordField createPasswordField(String prompt, Consumer<PasswordFieldBuilder>... configs) {
        PasswordFieldBuilder builder = new PasswordFieldBuilder(prompt);
        for (Consumer<PasswordFieldBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    // Label creation using builder lambdas
    @SafeVarargs
    public static Label createLabel(String text, Consumer<LabelBuilder>... configs) {
        LabelBuilder builder = new LabelBuilder(text);
        for (Consumer<LabelBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    // CheckBox creation using builder lambdas
    @SafeVarargs
    public static CheckBox createCheckBox(String text, Consumer<CheckBoxBuilder>... configs) {
        CheckBoxBuilder builder = new CheckBoxBuilder(text);
        for (Consumer<CheckBoxBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    // CopyButton creation using builder lambdas
    @SafeVarargs
    public static Button createCopyButton(String initialText, Supplier<String> textSupplier, Consumer<CopyButtonBuilder>... configs) {
        CopyButtonBuilder builder = new CopyButtonBuilder(initialText, textSupplier);
        for (Consumer<CopyButtonBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    // --- Builder Classes ---

    public static class TextFieldBuilder {
        private final TextField textField;
        private double maxWidth = -1;
        private double minWidth = -1;
        private int maxChars = -1;
        private int minChars = -1;

        public TextFieldBuilder(String prompt) {
            textField = new TextField();
            textField.setPromptText(prompt);
        }

        public TextFieldBuilder maxWidth(double width) {
            this.maxWidth = width;
            return this;
        }

        public TextFieldBuilder minWidth(double width) {
            this.minWidth = width;
            return this;
        }

        public TextFieldBuilder maxChars(int max) {
            this.maxChars = max;
            return this;
        }

        public TextFieldBuilder minChars(int min) {
            this.minChars = min;
            return this;
        }

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

    public static class ButtonBuilder {
        private final Button button;

        public ButtonBuilder(String text) {
            button = new Button(text);
        }

        public ButtonBuilder onAction(EventHandler<javafx.event.ActionEvent> handler) {
            button.setOnAction(handler);
            return this;
        }

        public ButtonBuilder routeToPage(MyPages page, AppContext context) {
            button.setOnAction(e -> context.router().navigate(page));
            return this;
        }

        public Button build() {
            return button;
        }

        public Button getSource() {
            return button;
        }

        public ButtonBuilder disable() {
            button.setDisable(true);
            return this;
        }
    }

    public static class PasswordFieldBuilder {
        private final PasswordField passwordField;

        public PasswordFieldBuilder(String prompt) {
            passwordField = new PasswordField();
            passwordField.setPromptText(prompt);
        }

        public PasswordFieldBuilder maxWidth(double width) {
            passwordField.setMaxWidth(width);
            return this;
        }

        public PasswordFieldBuilder minWidth(double width) {
            passwordField.setMinWidth(width);
            return this;
        }

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

        public PasswordField build() {
            return passwordField;
        }
    }

    public static class LabelBuilder {
        private final Label label;

        public LabelBuilder(String text) {
            label = new Label(text);
            label.setStyle(DesignGuide.TITLE_LABEL);
        }

        public LabelBuilder style(String style) {
            label.setStyle(style);
            return this;
        }

        public LabelBuilder textFill(Color color) {
            label.setTextFill(color);
            return this;
        }

        public Label build() {
            return label;
        }
    }

    public static class CheckBoxBuilder {
        private final CheckBox checkBox;

        public CheckBoxBuilder(String text) {
            checkBox = new CheckBox(text);
        }

        public CheckBoxBuilder selected(boolean selected) {
            checkBox.setSelected(selected);
            return this;
        }

        public CheckBox build() {
            return checkBox;
        }
    }

    public static class CopyButtonBuilder extends ButtonBuilder {
        private final Button button;
        private final Supplier<String> textSupplier;

        public CopyButtonBuilder(String initialText, Supplier<String> textSupplier) {
            super(initialText);
            button = super.getSource();
            this.textSupplier = textSupplier;
        }

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

        public Button build() {
            return button;
        }
    }
}