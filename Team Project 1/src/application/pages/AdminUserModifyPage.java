package application.pages;

import application.framework.*;
import database.model.entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;

import java.util.ArrayList;

import static utils.permissions.RolesUtil.*;

/**
 * A page that displays details on a user, allowing creation, updating, and deletion of user roles.
 * The page also allows for deletion of users, and for modification of an admins own roles.
 */
@Route(MyPages.ADMIN_USER_MODIFY)
@View(title = "Modify User")
public class AdminUserModifyPage extends BasePage {

    // The target user that is to be modified.
    private static User targetUser;

    public AdminUserModifyPage() {
        super();
    }

    /**
     * Sets the target user to be modified. This must be called
     * by the calling page before navigating to this page.
     *
     * @param user The user to modify.
     */
    public static void setTargetUser(User user) {
        targetUser = user;
    }

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        if (targetUser == null) {
            Label error = UIFactory.createLabel("No user selected for modification.");
            layout.getChildren().add(error);
            return layout;
        }

        // Display target user's name.
        Label nameLabel = UIFactory.createLabel("Name: " + targetUser.getUserName(),
                l -> l.style(DesignGuide.TITLE_LABEL));

        // Create role checkboxes using UIFactory.
        CheckBox adminCb = UIFactory.createCheckBox("Admin",
                cb -> cb.selected(hasRole(targetUser.getRoles(), Roles.ADMIN)));
        CheckBox instructorCb = UIFactory.createCheckBox("Instructor",
                cb -> cb.selected(hasRole(targetUser.getRoles(), Roles.INSTRUCTOR)));
        CheckBox studentCb = UIFactory.createCheckBox("Student",
                cb -> cb.selected(hasRole(targetUser.getRoles(), Roles.STUDENT)));
        CheckBox reviewerCb = UIFactory.createCheckBox("Reviewer",
                cb -> cb.selected(hasRole(targetUser.getRoles(), Roles.REVIEWER)));
        CheckBox staffCb = UIFactory.createCheckBox("Staff",
                cb -> cb.selected(hasRole(targetUser.getRoles(), Roles.STAFF)));

        // Define a single event handler to update roles.
        EventHandler<ActionEvent> roleHandler = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                int roleInt = targetUser.getRoles();
                if (event.getSource() instanceof CheckBox cb) {
                    switch (cb.getText()) {
                        case "Admin":
                            if (!cb.isSelected()) {
                                if (isLastAdmin(targetUser)) {
                                    cb.setSelected(true);
                                    new Alert(Alert.AlertType.ERROR, "Cannot remove the last admin!").show();
                                    return;
                                }
                                targetUser.setRoles(removeRole(roleInt, Roles.ADMIN));
                            } else {
                                targetUser.setRoles(addRole(roleInt, Roles.ADMIN));
                            }
                            break;
                        case "Instructor":
                            if (!cb.isSelected()) {
                                targetUser.setRoles(removeRole(roleInt, Roles.INSTRUCTOR));
                            } else {
                                targetUser.setRoles(addRole(roleInt, Roles.INSTRUCTOR));
                            }
                            break;
                        case "Student":
                            if (!cb.isSelected()) {
                                targetUser.setRoles(removeRole(roleInt, Roles.STUDENT));
                            } else {
                                targetUser.setRoles(addRole(roleInt, Roles.STUDENT));
                            }
                            break;
                        case "Reviewer":
                            if (!cb.isSelected()) {
                                targetUser.setRoles(removeRole(roleInt, Roles.REVIEWER));
                            } else {
                                targetUser.setRoles(addRole(roleInt, Roles.REVIEWER));
                            }
                            break;
                        case "Staff":
                            if (!cb.isSelected()) {
                                targetUser.setRoles(removeRole(roleInt, Roles.STAFF));
                            } else {
                                targetUser.setRoles(addRole(roleInt, Roles.STAFF));
                            }
                            break;
                    }
                    context.users().update(targetUser);
                }
                event.consume();
            }
        };

        // Set event handlers for checkboxes.
        adminCb.setOnAction(roleHandler);
        instructorCb.setOnAction(roleHandler);
        studentCb.setOnAction(roleHandler);
        reviewerCb.setOnAction(roleHandler);
        staffCb.setOnAction(roleHandler);

        VBox roleBox = new VBox(10, adminCb, instructorCb, studentCb, reviewerCb, staffCb);
        roleBox.setStyle(DesignGuide.CENTER_ALIGN);

        // Delete button.
        // Delete button.
        Button deleteBtn = UIFactory.createButton("Delete",
                e -> e.onAction(a -> handleUserDeletion(targetUser))
        );

        // Back button.
        Button backBtn = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.ADMIN_USER, context));

        layout.getChildren().addAll(nameLabel, roleBox, deleteBtn, backBtn);
        return layout;
    }

    /**
     * Helper method to check if the user is the last admin.
     */
    private boolean isLastAdmin(User user) {
        ArrayList<User> users = new ArrayList<>(context.users().getAll());
        long adminCount = users.stream().filter(u -> hasRole(u.getRoles(), Roles.ADMIN)).count();
        return adminCount <= 1;
    }

    private void handleUserDeletion(User targetUser) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete user " + targetUser.getUserName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (!hasRole(targetUser.getRoles(), Roles.ADMIN)) {
                    context.users().delete(targetUser.getId());
                    context.router().navigate(MyPages.ADMIN_USER);
                } else {
                    new Alert(Alert.AlertType.ERROR, "You cannot remove an admin!").show();
                }
            }
        });
    }
}