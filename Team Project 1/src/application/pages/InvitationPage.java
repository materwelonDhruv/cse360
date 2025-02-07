package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.Invite;

/**
 * InvitePage class represents the page where an admin can generate an
 * invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {


	public void show(Stage primaryStage) {
		VBox layout = new VBox();
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		// Label to display the title of the page
		Label userLabel = new Label("Invite ");
		userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Button to generate the invitation code
		Button showCodeButton = new Button("Generate Invitation Code");

		// Label to display the generated invitation code
		Label inviteCodeLabel = new Label("");
		;
		inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

		showCodeButton.setOnAction(_ -> {
			// Generate the invitation code using the databaseHelper and set it to the label
			Invite invitationCode = new Invite();
			inviteCodeLabel.setText(invitationCode.getCode());
		});

		layout.getChildren().addAll(userLabel, showCodeButton, inviteCodeLabel);
		Scene inviteScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(inviteScene);
		primaryStage.setTitle("Invite Page");

	}
}