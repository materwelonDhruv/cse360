package src.application.pages;

import src.application.AppContext;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import src.database.model.entities.User;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;
import src.database.repository.DataAccessException;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or
 * quit the application.
 */
public class WelcomeLoginPage {

	private final AppContext context;

	public WelcomeLoginPage() throws SQLException {
		this.context = AppContext.getInstance();
	}

	public void show(Stage primaryStage, User user) {

		VBox layout = new VBox(5);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		String username = user.getUserName();

		Label welcomeLabel = new Label("Welcome " + username + "!!");
		welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		//get all the roles assigned to the user
		int roleInt = user.getRoles();
		Roles[] roles = RolesUtil.parseRoles(roleInt);

		Button continueButton = new Button("Continue to your page");

		//continue to your page if only 1 role assigned
		if (roles.length == 1) {
			if (roles[0] == Roles.ADMIN) {
				continueButton.setOnAction(e -> {
					try {
						new AdminHomePage().show(primaryStage);
					} catch (SQLException ex) {
						throw new RuntimeException(ex);
					}
				});
			} else {
				continueButton.setOnAction(e -> {
					new UserHomePage().show(primaryStage);
				});
			}
		}
		// Dropdown menu to choose from all the assigned roles
		MenuButton roleMenu = new MenuButton("Select Role");

		//the role selected by the user from the menu bar
		Roles[] selectedRole = new Roles[1];

		for (Roles role : roles) {
			MenuItem roleItem = new MenuItem(role.toString());
			roleItem.setOnAction(e -> {
				selectedRole[0] = role;
				roleMenu.setText(role.toString());
			});
			roleMenu.getItems().add(roleItem);
		}

		// Button to quit the application
		Button quitButton = new Button("Quit");
		quitButton.setOnAction(_ -> {
			try {
				context.closeConnection();
			} catch (SQLException e) {
				throw new DataAccessException("Cannot close in WelcomePage", e);
			}
			Platform.exit(); // Exit the JavaFX application
		});

		//continue button which changes depending on the selectedRole
		continueButton.setOnAction(e -> {
			if (selectedRole[0] == Roles.ADMIN) {
				try {
					new AdminHomePage().show(primaryStage, user);
				} catch (SQLException ex) {
					throw new RuntimeException(ex);
				}
			} else {
				new UserHomePage().show(primaryStage);
			}
		});

		layout.getChildren().addAll(welcomeLabel, continueButton, quitButton, roleMenu);

		// Set the scene to primary stage
		Scene welcomeScene = new Scene(layout, 800, 400);
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Role Select");
	}

}