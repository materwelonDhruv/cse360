package application.pages;

import application.framework.*;
import application.framework.builders.CopyButtonBuilder;
import database.model.entities.Invite;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * InvitationPage class represents the page where an admin can generate an invitation.
 * The page uses UIFactory and DesignGuide for consistent UI creation and styling.
 *
 * @author Tyler
 */
@Route(MyPages.INVITATION)
@View(title = "Invite Page")
public class InvitationPage extends BasePage {

    public InvitationPage() {
        super();
    }

    @Override
    public Pane createView() {
        // Main vertical layout
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Title and instructions
        Label titleLabel = UIFactory.createLabel("Invite");
        Label instructions = UIFactory.createLabel("Select the roles that the invited user should receive:");

        // Role selection: create checkboxes using UIFactory
        CheckBox adminCb = UIFactory.createCheckBox("Admin");
        CheckBox studentCb = UIFactory.createCheckBox("Student");
        CheckBox reviewerCb = UIFactory.createCheckBox("Reviewer");
        CheckBox instructorCb = UIFactory.createCheckBox("Instructor");
        CheckBox staffCb = UIFactory.createCheckBox("Staff");

        HBox roleBox = new HBox(10);
        roleBox.setStyle(DesignGuide.CENTER_ALIGN);
        roleBox.getChildren().addAll(adminCb, studentCb, reviewerCb, instructorCb, staffCb);

        // Label to display generated invitation code
        Label inviteCodeLabel = UIFactory.createLabel("");

        // Create a reusable copy button instance for the invitation code
        Button copyButton = UIFactory.createCopyButton("Copy Code To Clipboard", inviteCodeLabel::getText, CopyButtonBuilder::onCopy);

        // Button to generate the invitation code
        Button generateBtn = UIFactory.createButton("Generate Invitation Code",
                e -> e.onAction(
                        a -> handleGenerateInvitation(
                                adminCb, studentCb, reviewerCb, instructorCb, staffCb, inviteCodeLabel, copyButton, layout
                        )
                )
        );

        // Back button navigates to Admin Home
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.ADMIN_HOME, context));

        // Assemble layout
        layout.getChildren().addAll(titleLabel, instructions, roleBox, generateBtn, inviteCodeLabel, backButton);
        return layout;
    }

    private void handleGenerateInvitation(CheckBox adminCb, CheckBox studentCb, CheckBox reviewerCb,
                                          CheckBox instructorCb, CheckBox staffCb,
                                          Label inviteCodeLabel, Button copyButton, VBox layout) {
        List<Roles> roleList = new ArrayList<>();
        if (adminCb.isSelected()) roleList.add(Roles.ADMIN);
        if (studentCb.isSelected()) roleList.add(Roles.STUDENT);
        if (reviewerCb.isSelected()) roleList.add(Roles.REVIEWER);
        if (instructorCb.isSelected()) roleList.add(Roles.INSTRUCTOR);
        if (staffCb.isSelected()) roleList.add(Roles.STAFF);

        if (!roleList.isEmpty()) {
            // Use the active user from session as the issuer
            Invite invite = new Invite(context.getSession().getActiveUser().getId());
            int roleInt = RolesUtil.rolesToInt(roleList.toArray(new Roles[0]));
            invite.setRoles(roleInt);
            context.invites().create(invite);
            inviteCodeLabel.setText(invite.getCode());

            // If copy button not yet in layout, add it; else reset its text.
            if (copyButton.getParent() == null) {
                layout.getChildren().add(copyButton);
            } else {
                copyButton.setText("Copy Code To Clipboard");
                copyButton.setDisable(false);
            }
        } else {
            inviteCodeLabel.setText("Select at least one role!");
        }
    }
}