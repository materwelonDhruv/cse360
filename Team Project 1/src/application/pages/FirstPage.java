package src.application.pages;

import src.application.AppContext;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup
 * screen.
 */
public class FirstPage {

	// Reference to the DatabaseHelper for database interactions
	private final AppContext context;

	public FirstPage() throws SQLException {
		this.context = AppContext.getInstance();
	}

	/**
	 * Displays the first page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {
		VBox layout = new VBox(5);

		// Label to display the welcome message for the first user
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		Label userLabel = new Label(
				"Hello..You are the first person here. \nPlease select continue to setup administrator access");
		userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		Button continueButton = new Button("Continue");
		// Button to navigate to the SetupAdmin page

		continueButton.setOnAction(_ -> {
            try {
                new AdminSetupPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

		layout.getChildren().addAll(userLabel, continueButton);
		Scene firstPageScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(firstPageScene);
		primaryStage.setTitle("First Page");
		primaryStage.show();
	}
}