package application.pages;

import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import validators.PasswordValidator;

/**
 * ResetPasswordPage class represents the page where an admin can reset a user's password.
 * It validates the new password and provides a back button to return to the admin home.
 */
@Route(MyPages.RESET_PASSWORD)
@View(title = "Reset Password")
public class ResetPasswordPage extends BasePage {

    public ResetPasswordPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label resetPassLabel = UIFactory.createLabel(
                "Reset your password",
                DesignGuide.TITLE_LABEL,
                null
        );

        TextField newPassField = UIFactory.createTextField("Enter new password", 250);

        Button resetPasswordButton = UIFactory.createButton("Reset Password", e -> {
            String password = newPassField.getText();
            try {
                PasswordValidator.validatePassword(password);
                // Password reset logic here (e.g., update the DB)
                System.out.println("Password validated and reset successfully.");
            } catch (IllegalArgumentException ex) {
                ((Button) e.getSource()).setText(ex.getMessage());
            }
        });

        Button backButton = UIFactory.createButton("Back", e -> {
            context.router().navigate(MyPages.ADMIN_HOME);
        });

        layout.getChildren().addAll(resetPassLabel, newPassField, resetPasswordButton, backButton);
        return layout;
    }
}