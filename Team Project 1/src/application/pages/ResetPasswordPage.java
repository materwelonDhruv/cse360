package application.pages;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import validators.PasswordValidator;

/**
 * <p> ResetPasswordPage class represents the page where an admin can reset a user's password.
 * It validates the new password and provides a back button to return to the admin home.</p>
 *
 * @author Riley
 */

@Route(MyPages.RESET_PASSWORD)
@View(title = "Reset Password")
public class ResetPasswordPage extends BasePage {

    public ResetPasswordPage() {
        super();
    }

    /*
     * Creates the window (view) adding the elements to allow user to reset their password
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label resetPassLabel = UIFactory.createLabel("Reset your password");

        TextField newPassField = UIFactory.createPasswordField("Enter new password",
                f -> f.minChars(8).maxChars(30));

        Button resetPasswordButton = UIFactory.createButton("Reset Password",
                e -> e.onAction(a -> handleResetPassword(newPassField, a))
        );

        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.ADMIN_HOME, context));

        layout.getChildren().addAll(resetPassLabel, newPassField, resetPasswordButton, backButton);
        return layout;
    }

    /**
     * @param newPassField
     * @param event        Handles new password by validating the password, implements logic to update the password in
     *                     the database
     */
    private void handleResetPassword(TextField newPassField, javafx.event.ActionEvent event) {
        String password = newPassField.getText();
        try {
            PasswordValidator.validatePassword(password);
            // Password reset logic here (e.g., update the DB)
            System.out.println("Password validated and reset successfully.");
        } catch (IllegalArgumentException ex) {
            ((Button) event.getSource()).setText(ex.getMessage());
        }
    }
}