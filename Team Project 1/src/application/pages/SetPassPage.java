package application.pages;

import application.framework.*;
import application.framework.builders.CopyButtonBuilder;
import database.model.entities.OneTimePassword;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/*******
 * <p> Title: ResetPasswordPage class </p>
 * <p> Description: SetPassPage represents the page where an admin can generate a one-time password
 * for a selected user.</p>
 * @author Riley
 * @author Dhruv
 * @version 1.00    2025-02-28 Created class
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

    /*
     * Creates the window (view) adding the elements to allow admin user to set a one time password for
     * a specified user
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label titleLabel = UIFactory.createLabel("Set user's one-time password",
                l -> l.style(DesignGuide.TITLE_LABEL));

        // Create and populate the user selection ChoiceBox.
        ChoiceBox<String> userDropBox = new ChoiceBox<>();
        ObservableList<String> userList = FXCollections.observableArrayList();
        context.users().getAll().forEach(u -> userList.add(u.getUserName()));
        userDropBox.setItems(userList);

        userDropBox.setOnAction(e -> {
            targetUser = userDropBox.getValue();
            targetID = context.users().getByUsername(targetUser).getId();
        });

        Label passLabel = UIFactory.createLabel("");

        Button setOTPButton = UIFactory.createButton("Set One-Time Password",
                e -> handleSetOneTimePassword(passLabel));

        Button copyButton = UIFactory.createCopyButton("Copy Password To Clipboard", passLabel::getText, CopyButtonBuilder::onCopy);

        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.ADMIN_HOME, context));

        layout.getChildren().addAll(titleLabel, userDropBox, passLabel, setOTPButton, copyButton, backButton);
        return layout;
    }

    /**
     * @param passLabel Gets the selected user, then generates a one time password for the admin to give a user
     */
    private void handleSetOneTimePassword(Label passLabel) {
        if (targetUser == null || targetID == 0) {
            passLabel.setText("Please select a user first.");
            return;
        }

        // Generate one-time password using current active user's ID as the issuer.
        OneTimePassword newPass = new OneTimePassword(context.getSession().getActiveUser().getId(), targetID);
        context.oneTimePasswords().create(newPass);
        passLabel.setText(newPass.getPlainOtp());
        System.out.println("New password: " + newPass.getPlainOtp());
    }
}