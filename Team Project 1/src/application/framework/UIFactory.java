package application.framework;

import application.AppContext;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import utils.permissions.Roles;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class to reduce JavaFX boilerplate code.
 * <p>
 * This class provides various methods for creating commonly used JavaFX components (such as buttons, text fields,
 * and labels) using builder pattern techniques. The goal is to reduce the repetitive code for UI component creation
 * and allow for cleaner, more customizable UI element setups.
 * </p>
 */
public final class UIFactory {
    private static final Map<Roles, MyPages> ROLE_PAGE_MAP = new HashMap<>();

    static {
        ROLE_PAGE_MAP.put(Roles.ADMIN, MyPages.ADMIN_HOME);
        ROLE_PAGE_MAP.put(Roles.INSTRUCTOR, MyPages.INSTRUCTOR_HOME);
        ROLE_PAGE_MAP.put(Roles.STUDENT, MyPages.USER_QUESTION_DISPLAY);
    }

    private UIFactory() {
    }

    /**
     * Returns the page associated with a specific role.
     * <p>
     * This method maps user roles to pages. If a role is not found in the map, the default page is returned.
     * </p>
     *
     * @param role The {@link Roles} of the user.
     * @return The {@link MyPages} associated with the given role.
     */
    private static MyPages getPageForRole(Roles role) {
        return ROLE_PAGE_MAP.getOrDefault(role, MyPages.USER_HOME);
    }

    /**
     * Creates a {@link TextField} with customized properties.
     * <p>
     * This method uses the builder pattern to set up the text field with the provided configuration options.
     * </p>
     *
     * @param prompt  The placeholder text for the text field.
     * @param configs Customizations for the {@link TextFieldBuilder}.
     * @return A {@link TextField} with the specified properties.
     */
    @SafeVarargs
    public static TextField createTextField(String prompt, Consumer<TextFieldBuilder>... configs) {
        TextFieldBuilder builder = new TextFieldBuilder(prompt);
        for (Consumer<TextFieldBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a {@link Button} with customized properties.
     * <p>
     * This method uses the builder pattern to set up the button with the provided configuration options.
     * </p>
     *
     * @param text    The text to display on the button.
     * @param configs Customizations for the {@link ButtonBuilder}.
     * @return A {@link Button} with the specified properties.
     */
    @SafeVarargs
    public static Button createButton(String text, Consumer<ButtonBuilder>... configs) {
        ButtonBuilder builder = new ButtonBuilder(text);
        for (Consumer<ButtonBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a {@link PasswordField} with customized properties.
     * <p>
     * This method uses the builder pattern to set up the password field with the provided configuration options.
     * </p>
     *
     * @param prompt  The placeholder text for the password field.
     * @param configs Customizations for the {@link PasswordFieldBuilder}.
     * @return A {@link PasswordField} with the specified properties.
     */
    @SafeVarargs
    public static PasswordField createPasswordField(String prompt, Consumer<PasswordFieldBuilder>... configs) {
        PasswordFieldBuilder builder = new PasswordFieldBuilder(prompt);
        for (Consumer<PasswordFieldBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a {@link Label} with customized properties.
     * <p>
     * This method uses the builder pattern to set up the label with the provided configuration options.
     * </p>
     *
     * @param text    The text to display on the label.
     * @param configs Customizations for the {@link LabelBuilder}.
     * @return A {@link Label} with the specified properties.
     */
    @SafeVarargs
    public static Label createLabel(String text, Consumer<LabelBuilder>... configs) {
        LabelBuilder builder = new LabelBuilder(text);
        for (Consumer<LabelBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a {@link CheckBox} with customized properties.
     * <p>
     * This method uses the builder pattern to set up the checkbox with the provided configuration options.
     * </p>
     *
     * @param text    The label text for the checkbox.
     * @param configs Customizations for the {@link CheckBoxBuilder}.
     * @return A {@link CheckBox} with the specified properties.
     */
    @SafeVarargs
    public static CheckBox createCheckBox(String text, Consumer<CheckBoxBuilder>... configs) {
        CheckBoxBuilder builder = new CheckBoxBuilder(text);
        for (Consumer<CheckBoxBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a {@link Button} that copies text to the clipboard.
     * <p>
     * This method uses the builder pattern to create a button that copies text to the clipboard when clicked.
     * </p>
     *
     * @param initialText  The text displayed on the button initially.
     * @param textSupplier The supplier for the text to be copied to the clipboard.
     * @param configs      Customizations for the {@link CopyButtonBuilder}.
     * @return A {@link Button} with the specified properties.
     */
    @SafeVarargs
    public static Button createCopyButton(String initialText, Supplier<String> textSupplier, Consumer<CopyButtonBuilder>... configs) {
        CopyButtonBuilder builder = new CopyButtonBuilder(initialText, textSupplier);
        for (Consumer<CopyButtonBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Shows an alert dialog with the specified type, title, and content.
     *
     * @param alertType The type of the alert.
     * @param title     The title of the alert.
     * @param content   The content text of the alert.
     * @return An {@link Optional} containing the {@link ButtonType} that was clicked, or empty if closed without selection.
     */
    public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new AlertBuilder(alertType)
                .title(title)
                .header(null)
                .content(content)
                .build();
        return alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog with the specified title and content.
     *
     * @param title   The title of the confirmation dialog.
     * @param content The content text of the confirmation dialog.
     * @return {@code true} if the user confirms, {@code false} if the user cancels.
     */
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new AlertBuilder(Alert.AlertType.CONFIRMATION)
                .title(title)
                .header(null)
                .content(content)
                .build();
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Creates a navigation menu for the user interface.
     * <p>
     * This method generates a menu button that contains navigation options for the app, based on user roles, excluding their current role.
     * </p>
     *
     * @param context  The application context.
     * @param menuText The text to display on the menu button.
     * @param configs  Customizations for the {@link NavMenuBuilder}.
     * @return A {@link MenuButton} with the specified properties.
     */
    @SafeVarargs
    public static MenuButton createNavMenu(AppContext context, String menuText, Consumer<NavMenuBuilder>... configs) {
        NavMenuBuilder builder = new NavMenuBuilder(context, menuText);
        for (Consumer<NavMenuBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a homepage button for navigating to the user's homepage.
     * <p>
     * If the current page is already the homepage, it will navigate to the user's specific homepage based on their currently selected role. If not, it will navigate to the default homepage.
     *
     * @param text    The text to display on the homepage button.
     * @param context The application context.
     * @param configs Customizations for the {@link HomepageButtonBuilder}.
     * @return A {@link Button} for navigating to the homepage.
     */
    @SafeVarargs
    public static Button createHomepageButton(String text, AppContext context, Consumer<HomepageButtonBuilder>... configs) {
        HomepageButtonBuilder builder = new HomepageButtonBuilder(text, context);
        for (Consumer<HomepageButtonBuilder> config : configs) {
            config.accept(builder);
        }
        return builder.build();
    }

    /**
     * Creates a logout button that clears the current role and navigates to the login page.
     *
     * @param context The application context.
     * @return A {@link Button} for logging out.
     */
    public static Button createLogoutButton(AppContext context) {
        return UIFactory.createButton("Logout", e -> e.onAction(a -> {
            context.getSession().setCurrentRole(null);
            context.router().navigate(MyPages.USER_LOGIN);
        }));
    }

    /**
     * Creates a back button that navigates to the previous page.
     *
     * @param context The application context.
     * @return A {@link Button} for navigating back.
     */
    public static Button createBackButton(AppContext context) {
        return UIFactory.createButton("Back", e -> e.onAction(a -> {
            MyPages previousPage = context.router().getPreviousPage();
            if (previousPage != null) {
                context.router().navigate(previousPage);
            } else {
                context.router().navigate(MyPages.USER_HOME);
            }
        }));
    }


    // --- Builder Classes ---

    /**
     * Builder class for creating and customizing a {@link TextField}.
     */
    public static class TextFieldBuilder {
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

    /**
     * Builder class for creating and customizing a {@link Button}.
     */
    public static class ButtonBuilder {
        private final Button button;

        /**
         * Initializes the builder with the given text for the button.
         */
        public ButtonBuilder(String text) {
            button = new Button(text);
        }

        /**
         * Sets the action handler for the button when clicked.
         */
        public ButtonBuilder onAction(EventHandler<javafx.event.ActionEvent> handler) {
            button.setOnAction(handler);
            return this;
        }

        /**
         * Sets the action to navigate to a specific page when the button is clicked.
         */
        public ButtonBuilder routeToPage(MyPages page, AppContext context) {
            button.setOnAction(e -> context.router().navigate(page));
            return this;
        }

        /**
         * Builds and returns the {@link Button} instance.
         */
        public Button build() {
            return button;
        }

        /**
         * Returns the {@link Button} instance created by the builder.
         */
        public Button getSource() {
            return button;
        }

        /**
         * Disables the button.
         */
        public ButtonBuilder disable() {
            button.setDisable(true);
            return this;
        }
    }

    /**
     * Builder class for creating and customizing a {@link PasswordField}.
     */
    public static class PasswordFieldBuilder {
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

    /**
     * Builder class for creating and customizing a {@link Label}.
     */
    public static class LabelBuilder {
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

    /**
     * Builder class for creating and customizing a {@link CheckBox}.
     */
    public static class CheckBoxBuilder {
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

    /**
     * Builder class for creating and customizing a {@link Button} that copies text to the clipboard.
     */
    public static class CopyButtonBuilder extends ButtonBuilder {
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

    /**
     * Builder class for creating and customizing an {@link Alert}.
     */
    public static class AlertBuilder {
        private final Alert alert;

        /**
         * Initializes the builder with the given alert type.
         */
        public AlertBuilder(Alert.AlertType type) {
            alert = new Alert(type);
        }

        /**
         * Sets the title of the alert.
         */
        public AlertBuilder title(String title) {
            alert.setTitle(title);
            return this;
        }

        /**
         * Sets the header text for the alert.
         */
        public AlertBuilder header(String header) {
            alert.setHeaderText(header);
            return this;
        }

        /**
         * Sets the content text for the alert.
         */
        public AlertBuilder content(String content) {
            alert.setContentText(content);
            return this;
        }

        /**
         * Builds and returns the {@link Alert} instance.
         */
        public Alert build() {
            return alert;
        }
    }

    /**
     * Builder class for creating and customizing a navigation menu.
     */
    public static class NavMenuBuilder {
        private final MenuButton menuButton;

        /**
         * Initializes the builder with the given application context and menu text.
         */
        public NavMenuBuilder(AppContext context, String menuText) {
            Roles currentRole = context.getSession().getCurrentRole();
            Roles[] allRoles = utils.permissions.RolesUtil.intToRoles(context.getSession().getActiveUser().getRoles());
            List<Roles> menuRoles = new ArrayList<>();
            if (currentRole == null) {
                menuRoles.addAll(Arrays.asList(allRoles));
            } else {
                for (Roles role : allRoles) {
                    if (!role.equals(currentRole)) {
                        menuRoles.add(role);
                    }
                }
            }

            menuButton = new MenuButton(menuText);
            for (Roles role : menuRoles) {
                MenuItem roleItem = new MenuItem(role.toString());
                roleItem.setOnAction(e -> {
                    MyPages page = UIFactory.getPageForRole(role);
                    context.getSession().setCurrentRole(role);
                    context.router().navigate(page);
                });
                menuButton.getItems().add(roleItem);
            }
        }

        /**
         * Sets the text of the menu button.
         */
        public NavMenuBuilder text(String text) {
            menuButton.setText(text);
            return this;
        }

        /**
         * Builds and returns the {@link MenuButton} instance.
         */
        public MenuButton build() {
            return menuButton;
        }
    }

    /**
     * Builder class for creating a homepage button that navigates to the user's homepage.
     */
    public static class HomepageButtonBuilder extends ButtonBuilder {
        /**
         * Initializes the builder with the given text and application context.
         */
        public HomepageButtonBuilder(String text, AppContext context) {
            super(text);
            Button button = super.getSource();
            button.setOnAction(e -> {
                if (context.router().getCurrentPage() == MyPages.USER_HOME) {
                    Roles currentRole = context.getSession().getCurrentRole();
                    MyPages homepage = UIFactory.getPageForRole(currentRole);
                    context.router().navigate(homepage);
                } else {
                    context.router().navigate(MyPages.USER_HOME);
                }

            });
        }
    }
}