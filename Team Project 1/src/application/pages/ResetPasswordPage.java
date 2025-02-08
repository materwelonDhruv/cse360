package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.Invite;
import src.database.model.entities.User;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;
import src.validators.PasswordValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * InvitePage class represents the page where an admin can generate an
 **/

public class ResetPasswordPage {

	private final AppContext context;

	public ResetPasswordPage() throws SQLException {
		this.context = AppContext.getInstance();
	}

	public void show(Stage primaryStage, User user) {

		VBox layout = new VBox();
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		// Label to display the title of the page
		Label resetPassLabel = new Label("Reset your password ");
		resetPassLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10");

		// Input field for the user's new password
		TextField newPassField = new TextField();
		newPassField.setPromptText("Enter new password");
		newPassField.setMaxWidth(250);

		// Button to generate the invitation code
		Button resetPasswordButton = new Button("Reset Password");
		resetPasswordButton.setOnAction(e -> {
			String password = newPassField.getText();
			String passwordCheck = PasswordValidator.evaluatePassword(password);
			if(!passwordCheck.isEmpty()) {
				resetPasswordButton.setText(passwordCheck);
				return;
			}
		});

		Button backButton = new Button("Back");

		// Action for back button
		backButton.setOnAction(_ -> {
			try {
				new AdminHomePage().show(primaryStage, user);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});


		// Add components to layout
		layout.getChildren().addAll(resetPassLabel,newPassField, resetPasswordButton, backButton);

		// Set the scene
		Scene inviteScene = new Scene(layout, 800, 400);
		primaryStage.setScene(inviteScene);
		primaryStage.setTitle("Invite Page");
	}
}