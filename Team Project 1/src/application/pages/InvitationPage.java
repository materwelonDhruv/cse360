package application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import application.AppContext;
import database.model.entities.Invite;
import database.model.entities.User;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * InvitePage class represents the page where an admin can generate an
 **/

public class InvitationPage {

    private final AppContext context;

    public InvitationPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage, User user) {

        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the title of the page
        Label userLabel = new Label("Invite ");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10");

        // Label to display instructions
        Label roleInstructions = new Label("Select the roles that the invited user should receive:");
        roleInstructions.setStyle("-fx-font-size: 14px");

        // Hbox to display the roles
        HBox roles = new HBox(10);
        roles.setStyle("-fx-alignment: center; -fx-padding: 10");

        // CheckBoxes for the roles
        CheckBox admin = new CheckBox("Admin");
        CheckBox student = new CheckBox("Student");
        CheckBox reviewer = new CheckBox("Reviewer");
        CheckBox instructor = new CheckBox("Instructor");
        CheckBox staff = new CheckBox("Staff");

        // Button to generate the invitation code
        Button showCodeButton = new Button("Generate Invitation Code");

        // Label to display the generated invitation code
        Label inviteCodeLabel = new Label("");
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        // Button to copy the invitation code
        Button copyCodeToClipboard = new Button("Copy Code To Clipboard");
        copyCodeToClipboard.setStyle("-fx-font-size: 12px");

        showCodeButton.setOnAction(_ -> {
            // Get the roles to be assigned to the invited user
            List<Roles> roleList = new ArrayList<>();
            if (admin.isSelected()) roleList.add(Roles.ADMIN);
            if (student.isSelected()) roleList.add(Roles.STUDENT);
            if (reviewer.isSelected()) roleList.add(Roles.REVIEWER);
            if (instructor.isSelected()) roleList.add(Roles.INSTRUCTOR);
            if (staff.isSelected()) roleList.add(Roles.STAFF);

            if (!roleList.isEmpty()) {
                // Generate the invitation and set it to the label
                Invite invite = new Invite(user.getId());
                int roleInt = RolesUtil.rolesToInt(roleList.toArray(new Roles[0]));
                invite.setRoles(roleInt);
                context.invites().create(invite);
                inviteCodeLabel.setText(invite.getCode());

                // Show copyCodeToClipboard button or reset its text if it is already shown
                if (copyCodeToClipboard.getParent() == null) {
                    layout.getChildren().add(copyCodeToClipboard);
                } else {
                    copyCodeToClipboard.setText("Copy Code To Clipboard");
                }
            } else {
                inviteCodeLabel.setText("Select at least one role!");
            }
        });

        // Action for copying the code to clipboard
        copyCodeToClipboard.setOnAction(_ -> {
            // Copy the generated code into the user's clipboard and change text for feedback
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(inviteCodeLabel.getText());
            clipboard.setContent(content);
            copyCodeToClipboard.setText("Copied!");
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
        layout.getChildren().addAll(userLabel, roleInstructions, roles, showCodeButton, inviteCodeLabel, backButton);
        roles.getChildren().addAll(admin, student, reviewer, instructor, staff);

        // Set the scene
        Scene inviteScene = new Scene(layout, 800, 400);
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invite Page");
    }
}