package src.application.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.OneTimePassword;
import src.database.model.entities.User;

import java.sql.SQLException;

/**
 * InvitePage class represents the page where an admin can generate an
 **/

public class SetPassPage {

    private final AppContext context;
    private String targetUser;
    private int targetID;

    public SetPassPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage, User user) {

        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label userLabel = new Label("Set user's one-time password");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10");


        // Button to generate the invitation code
        Button setUserOTP = new Button("Set Password");

        // Button to copy the invitation code
        Button copyPassToClipboard = new Button("Copy password To Clipboard");
        copyPassToClipboard.setStyle("-fx-font-size: 12px");
        ChoiceBox<String> userDropBox = new ChoiceBox<>();
        ObservableList<String> obUserList = FXCollections.observableArrayList();
        for (User otherUSer : context.users().getAll()) {
            obUserList.add(otherUSer.getUserName());
            System.out.println(otherUSer.getId());
        }
        userDropBox.getItems().addAll(obUserList);

        userDropBox.setOnAction(_ -> {
            targetUser = userDropBox.getValue();
            System.out.println(targetUser);
            targetID = context.users().getByUsername(targetUser).getId();
        });

        Label passLabel = new Label("");
        setUserOTP.setOnAction(_ -> {
            OneTimePassword newPass = new OneTimePassword(user.getId(), targetID);
            context.oneTimePasswords().create(newPass);
            passLabel.setText(newPass.getPlainOtp());
            System.out.println("New password: " + newPass.getPlainOtp());

        });

        passLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10");
        // Action for copying the code to clipboard

        copyPassToClipboard.setOnAction(_ -> {
            // Copy the generated code into the user's clipboard and change text for feedback
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(passLabel.getText());
            clipboard.setContent(content);
            copyPassToClipboard.setText("Copied!");
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
        layout.getChildren().addAll(userLabel, userDropBox, passLabel, setUserOTP, copyPassToClipboard, backButton);


        // Set the scene
        Scene inviteScene = new Scene(layout, 800, 400);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Set user password");
    }
}