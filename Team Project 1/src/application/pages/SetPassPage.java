package application.pages;

import application.framework.*;
import database.model.entities.OneTimePassword;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * SetPassPage represents the page where an admin can generate a one-time password
 * for a selected user.
 */
@Route(MyPages.SET_PASS)
@View(title = "Set User Password")
public class SetPassPage extends BasePage {

    // Fields to hold the selected target user's info.
    private String targetUser;
    private int targetID;

    public SetPassPage() {
        super();
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Set user's one-time password", DesignGuide.TITLE_LABEL, null);

        // Create and populate the user selection ChoiceBox.
        ChoiceBox<String> userDropBox = new ChoiceBox<>();
        ObservableList<String> userList = FXCollections.observableArrayList();
        context.users().getAll().forEach(u -> userList.add(u.getUserName()));
        userDropBox.setItems(userList);

        userDropBox.setOnAction(e -> {
            targetUser = userDropBox.getValue();
            targetID = context.users().getByUsername(targetUser).getId();
        });

        Label passLabel = UIFactory.createLabel("", null, null);

        Button setOTPButton = UIFactory.createButton("Set Password", e -> {
            // Generate one-time password using current active user's ID as the issuer.
            OneTimePassword newPass = new OneTimePassword(SessionContext.getActiveUser().getId(), targetID);
            context.oneTimePasswords().create(newPass);
            passLabel.setText(newPass.getPlainOtp());
            System.out.println("New password: " + newPass.getPlainOtp());
        });
        
        Button copyButton = UIFactory.createCopyButton("Copy Password To Clipboard", passLabel::getText);

        Button backButton = UIFactory.createButton("Back", e -> {
            context.router().navigate(MyPages.ADMIN_HOME);
        });

        layout.getChildren().addAll(titleLabel, userDropBox, passLabel, setOTPButton, copyButton, backButton);
        return layout;
    }
}