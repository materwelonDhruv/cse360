package application.framework;

import application.AppContext;
import application.framework.builders.*;
import javafx.scene.control.*;
import utils.permissions.Roles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class to reduce JavaFX boilerplate code.
 * <p>
 * This class provides various methods for creating commonly used JavaFX components (such as buttons, text fields,
 * and labels) using builder pattern techniques. The goal is to reduce the repetitive code for UI component creation
 * and allow for cleaner, more customizable UI element setups.
 * </p>
 *
 * @author Dhruv
 */
public final class UIFactory {
    /**
     * A map that associates user roles with their corresponding pages.
     */
    private static final Map<Roles, MyPages> ROLE_PAGE_MAP = new HashMap<>();

    static {
        ROLE_PAGE_MAP.put(Roles.ADMIN, MyPages.ADMIN_HOME);
        ROLE_PAGE_MAP.put(Roles.INSTRUCTOR, MyPages.INSTRUCTOR_HOME);
        ROLE_PAGE_MAP.put(Roles.REVIEWER, MyPages.REVIEW_HOME);
        ROLE_PAGE_MAP.put(Roles.STUDENT, MyPages.USER_QUESTION_DISPLAY);
        ROLE_PAGE_MAP.put(Roles.STAFF, MyPages.STAFF_HOME);
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
    public static MyPages getPageForRole(Roles role) {
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
     */
    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new AlertBuilder(alertType)
                .title(title)
                .header(null)
                .content(content)
                .build();
        alert.showAndWait();
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
     * @param context The application context.
     * @param configs Customizations for the {@link HomepageButtonBuilder}.
     * @return A {@link Button} for navigating to the homepage.
     */
    @SafeVarargs
    public static Button createHomepageButton(AppContext context, Consumer<HomepageButtonBuilder>... configs) {
        HomepageButtonBuilder builder = new HomepageButtonBuilder(context);
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

    /**
     * Opens a modal input dialog with a single text field with validation.
     *
     * @param title       dialog window title
     * @param placeholder placeholder text for the field
     * @param defaultText optional pre‑fill text (may be null)
     * @param validator   validator
     * @param errorMsg    error message to show when validation fails
     * @param configs     additional customisations for the {@link InputDialogBuilder}
     * @return an {@link Optional} containing the user input when OK was pressed, otherwise empty
     */
    @SafeVarargs
    public static Optional<String> showTextInput(String title,
                                                 String placeholder,
                                                 String defaultText,
                                                 Predicate<String> validator,
                                                 String errorMsg,
                                                 Consumer<InputDialogBuilder>... configs) {
        InputDialogBuilder builder = new InputDialogBuilder()
                .setTitle(title)
                .setPlaceholder(placeholder)
                .attachValidator(validator, errorMsg);
        if (defaultText != null) builder.setDefaultText(defaultText);
        for (Consumer<InputDialogBuilder> cfg : configs) cfg.accept(builder);
        return builder.showAndWaitValidated();
    }

    /**
     * Opens a modal input dialog with a single text field with validation.
     * <p>
     * Don't use this method and set validator via configs. Use {@link #showTextInput(String, String, String, Predicate, String, Consumer[])} instead.
     *
     * @param title       dialog window title
     * @param placeholder placeholder text for the field
     * @param defaultText optional pre‑fill text (may be null)
     * @param configs     additional customisations for the {@link InputDialogBuilder}
     * @return an {@link Optional} containing the user input when OK was pressed, otherwise empty
     */
    @SafeVarargs
    public static Optional<String> showTextInput(String title,
                                                 String placeholder,
                                                 String defaultText,
                                                 Consumer<InputDialogBuilder>... configs) {
        InputDialogBuilder builder = new InputDialogBuilder()
                .setTitle(title)
                .setPlaceholder(placeholder);
        if (defaultText != null) builder.setDefaultText(defaultText);
        for (Consumer<InputDialogBuilder> cfg : configs) cfg.accept(builder);
        return builder.showAndWait();
    }
}