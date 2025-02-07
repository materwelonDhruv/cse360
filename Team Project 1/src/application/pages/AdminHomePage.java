package src.application.pages;

import src.application.AppContext;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.User;

import java.sql.SQLException;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */
public class AdminHomePage {
	/**
	 * Displays the admin page in the provided primary stage.
	 *
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	private final AppContext context;

	public AdminHomePage() throws SQLException {
		this.context = AppContext.getInstance();
	}
	public void show(Stage primaryStage, User user) {
		VBox layout = new VBox();

		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		// label to display the welcome message for the admin
		Label adminLabel = new Label("Hello, Admin!");
		Button userButton = new Button("Show Users");
		Button logoutButton = new Button("Logout");
		Button inviteButton = new Button("Invite");
		Button otpButton = new Button("Set user OTP");

		userButton.setOnAction(a -> {
			try {
				new AdminUserPage().show(primaryStage, user);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		inviteButton.setOnAction(_ -> {
			try {
				new InvitationPage().show(primaryStage, user);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		logoutButton.setOnAction(a -> {
			try {
				new UserLoginPage().show(primaryStage);
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		});

		adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		layout.getChildren().addAll(adminLabel, userButton, inviteButton,otpButton, logoutButton);
		Scene adminScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(adminScene);
		primaryStage.setTitle("Admin Page");
	}
}