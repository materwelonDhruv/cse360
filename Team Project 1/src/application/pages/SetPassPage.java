package application.pages;

import application.framework.BasePage;
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
@src.application.framework.Route(src.application.framework.MyPages.SET_PASS)
@src.application.framework.View(title = "Set User Password")
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
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        Label titleLabel = src.application.framework.UIFactory.createLabel("Set user's one-time password", src.application.framework.DesignGuide.TITLE_LABEL, null);

        // Create and populate the user selection ChoiceBox.
        ChoiceBox<String> userDropBox = new ChoiceBox<>();
        ObservableList<String> userList = FXCollections.observableArrayList();
        context.users().getAll().forEach(u -> userList.add(u.getUserName()));
        userDropBox.setItems(userList);

        userDropBox.setOnAction(e -> {
            targetUser = userDropBox.getValue();
            targetID = context.users().getByUsername(targetUser).getId();
        });

        Label passLabel = src.application.framework.UIFactory.createLabel("", null, null);

        Button setOTPButton = src.application.framework.UIFactory.createButton("Set Password", e -> {
            // Generate one-time password using current active user's ID as the issuer.
            OneTimePassword newPass = new OneTimePassword(src.application.framework.SessionContext.getActiveUser().getId(), targetID);
            context.oneTimePasswords().create(newPass);
            passLabel.setText(newPass.getPlainOtp());
            System.out.println("New password: " + newPass.getPlainOtp());
        });

        Button copyButton = src.application.framework.UIFactory.createCopyButton("Copy Password To Clipboard", passLabel::getText);

        Button backButton = src.application.framework.UIFactory.createButton("Back", e -> {
            context.router().navigate(src.application.framework.MyPages.ADMIN_HOME);
        });

        layout.getChildren().addAll(titleLabel, userDropBox, passLabel, setOTPButton, copyButton, backButton);
        return layout;
    }
}