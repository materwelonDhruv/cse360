package application.pages.admin;

import application.framework.*;
import database.model.entities.AdminRequest;
import database.model.entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import utils.requests.AdminActions;
import utils.requests.RequestState;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.Objects;

import static utils.permissions.RolesUtil.*;

/**
 * A page that displays details on a user, allowing creation, updating, and deletion of user roles.
 * The page also allows for deletion of users, and for modification of an admins own roles.
 * Non-admin users are redirected to this page to create or reopen an AdminRequest, but they are
 * not allowed to make instantaneous changes like an admin could. Instead, they select the changes
 * they want, then an AdminRequest is created according to the changes they selected.
 *
 * @author Mike, Tyler
 */
@Route(MyPages.ADMIN_USER_MODIFY)
@View(title = "Modify User")
public class AdminUserModifyPage extends BasePage {
    // TextField for the reason the request is being made.
    private final TextField reasonField = UIFactory.createTextField("Reason For Request");
    private static User admin;
    // The target user that is to be modified.
    private static User targetUser;
    // The existing AdminRequest to be reopened.
    private static AdminRequest existingRequest;

    private final Roles role = context.getSession().getCurrentRole();

    public AdminUserModifyPage() {
        super();
    }

    public static void setAdmin(User user) {
        admin = user;
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

    /**
     * Sets the existingRequest. May be called before navigating
     * to the page if a request is being reopened.
     * @param request The request to be reopened.
     */
    public static void setExistingRequest(AdminRequest request) {existingRequest = request;}

    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        if (targetUser == null) {
            Label error = UIFactory.createLabel("No user selected for modification.");
            layout.getChildren().add(error);
            return layout;
        }

        Roles currentRole = context.getSession().getCurrentRole();

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
                            if (targetUser.getId() == admin.getId()) {
                                cb.setSelected(true);
                                new Alert(Alert.AlertType.ERROR, "Cannot remove your own role as admin!").show();
                                return;
                            }
                            if (!cb.isSelected()) {
                                if (isLastAdmin(targetUser)) {
                                    cb.setSelected(true);
                                    new Alert(Alert.AlertType.ERROR, "Cannot remove the last admin!").show();
                                    return;
                                }
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(removeRole(roleInt, Roles.ADMIN));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.ADMIN);
                                }
                            } else {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(addRole(roleInt, Roles.ADMIN));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.ADMIN);
                                }
                            }
                            break;
                        case "Instructor":
                            if (!cb.isSelected()) {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(removeRole(roleInt, Roles.INSTRUCTOR));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.INSTRUCTOR);
                                }
                            } else {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(addRole(roleInt, Roles.INSTRUCTOR));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.INSTRUCTOR);
                                }
                            }
                            break;
                        case "Student":
                            if (!cb.isSelected()) {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(removeRole(roleInt, Roles.STUDENT));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.STUDENT);
                                }
                            } else {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(addRole(roleInt, Roles.STUDENT));
                                } else {
                                    createAdminRoleRequest(roleInt, Roles.STUDENT);
                                }
                            }
                            break;
                        case "Reviewer":
                            if (!cb.isSelected()) {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(removeRole(roleInt, Roles.REVIEWER));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.REVIEWER);
                                }
                            } else {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(addRole(roleInt, Roles.REVIEWER));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.REVIEWER);
                                }
                            }
                            break;
                        case "Staff":
                            if (!cb.isSelected()) {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(removeRole(roleInt, Roles.STAFF));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.STAFF);
                                }
                            } else {
                                if (role == Roles.ADMIN) {
                                    targetUser.setRoles(addRole(roleInt, Roles.STAFF));
                                } else if (role == Roles.INSTRUCTOR) {
                                    createAdminRoleRequest(roleInt, Roles.STAFF);
                                }
                            }
                            break;
                    }
                    context.users().update(targetUser);
                }
                event.consume();
            }
        };

        VBox roleBox = new VBox(10, adminCb, instructorCb, studentCb, reviewerCb, staffCb);
        roleBox.setStyle(DesignGuide.CENTER_ALIGN);

        // Set event handlers for checkboxes if the current user is an Admin.
        // Otherwise, remove the option to select the admin role.
        if (currentRole == Roles.ADMIN) {
            adminCb.setOnAction(roleHandler);
            instructorCb.setOnAction(roleHandler);
            studentCb.setOnAction(roleHandler);
            reviewerCb.setOnAction(roleHandler);
            staffCb.setOnAction(roleHandler);
        } else {
            roleBox.getChildren().remove(adminCb);
        }

        // Show existingRequest's reason and selected roles
        if (existingRequest != null) {
            reasonField.setText(existingRequest.getReason());

            if (existingRequest.getType() == AdminActions.UpdateRole) {
                instructorCb.setSelected(hasRole(existingRequest.getContext(), Roles.INSTRUCTOR));
                studentCb.setSelected(hasRole(existingRequest.getContext(), Roles.STUDENT));
                reviewerCb.setSelected(hasRole(existingRequest.getContext(), Roles.REVIEWER));
                staffCb.setSelected(hasRole(existingRequest.getContext(), Roles.STAFF));
            }
        }

        // Change roles button.
        Button changeRolesBtn = UIFactory.createButton("Change User's Roles To Selected",
                e -> e.onAction(a -> {
                    List<Roles> roleList = new ArrayList<>();
                    if (adminCb.isSelected()) roleList.add(Roles.ADMIN);
                    if (studentCb.isSelected()) roleList.add(Roles.STUDENT);
                    if (reviewerCb.isSelected()) roleList.add(Roles.REVIEWER);
                    if (instructorCb.isSelected()) roleList.add(Roles.INSTRUCTOR);
                    if (staffCb.isSelected()) roleList.add(Roles.STAFF);
                    int roleInt = RolesUtil.rolesToInt(roleList.toArray(new Roles[0]));
                    requestRolesChange(targetUser, roleInt);
                }));

        // Request OTP Button.
        Button requestOTPBtn = UIFactory.createButton("Request One Time Password",
                e -> e.onAction(a -> sendAdminRequest(AdminActions.RequestPassword, null))
        );

        // Delete button.
        Button deleteBtn = UIFactory.createButton("Delete",
                e -> e.onAction(a -> handleUserDeletion(targetUser))
        );

        // Back button.
        Button backBtn = UIFactory.createBackButton(context);

        layout.getChildren().addAll(nameLabel, roleBox);
        if (currentRole != Roles.ADMIN) {layout.getChildren().addAll(reasonField, changeRolesBtn, requestOTPBtn);}
        layout.getChildren().addAll(deleteBtn, backBtn);

        // Remove unnecessary elements if a request is being reopened.
        if (existingRequest != null) {
            if (existingRequest.getType() == AdminActions.UpdateRole) {
                layout.getChildren().removeAll(requestOTPBtn, deleteBtn);
            } else {
                layout.getChildren().removeAll(roleBox, changeRolesBtn, deleteBtn);
            }
        }

        return layout;
    }

    private void createAdminRoleRequest(int roleInt, Roles roles) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setContentText("Would you like to send an admin role change request?");
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                AdminRequest adminRequest = new AdminRequest()
                        .setRequester(context.getSession().getActiveUser())
                        .setContext(roles.getBit())
                        .setState(RequestState.Pending)
                        .setTarget(targetUser)
                        .setReason("reason")
                        .setType(AdminActions.UpdateRole);
                context.adminRequests().create(adminRequest);
            }
        });
    }

    /**
     * Helper method to check if the user is the last admin.
     */
    private boolean isLastAdmin(User user) {
        ArrayList<User> users = new ArrayList<>(context.users().getAll());
        long adminCount = users.stream().filter(u -> hasRole(u.getRoles(), Roles.ADMIN)).count();
        return adminCount <= 1;
    }

    /**
     * Helper method to handle deletion of the targetUser. If the current
     * user is not an admin, an AdminRequest will be created instead.
     * @param targetUser the user to be deleted from the database.
     */
    private void handleUserDeletion(User targetUser) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete user " + targetUser.getUserName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (context.getSession().getCurrentRole() == Roles.ADMIN) {
                    if (!hasRole(targetUser.getRoles(), Roles.ADMIN)) {
                        context.users().delete(targetUser.getId());
                        context.router().navigate(MyPages.ADMIN_USER);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "You cannot remove an admin!").show();
                    }
                } else {
                    sendAdminRequest(AdminActions.DeleteUser, null);
                }
            }
        });
    }

    /**
     * Helper method to create an AdminRequest to change the roles of the
     * targetUser to the currently selected roles in the role checkboxes.
     * @param targetUser The user whose roles are to be changed.
     * @param roleInt The roles the targetUser is to be given.
     */
    private void requestRolesChange(User targetUser, int roleInt) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to request these role changes for " + targetUser.getUserName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (!hasRole(targetUser.getRoles(), Roles.ADMIN)) {
                    sendAdminRequest(AdminActions.UpdateRole, roleInt);
                } else {
                    new Alert(Alert.AlertType.ERROR, "You cannot remove an admin!").show();
                }
            }
        });
    }

    /**
     *  If a request is being reopened, that request is updated
     *  with the given action and roleInt. Otherwise, a new
     *  request is created.
     * @param action The action of the request.
     * @param roleInt The roleInt of the request.
     */
    private void sendAdminRequest(AdminActions action, Integer roleInt) {
        String reason;
        if (!Objects.equals(reasonField.getText(), "")) {
            reason = reasonField.getText();
        } else {
            reason = "None";
        }
        if (existingRequest != null) {
            existingRequest.setReason(reason);
            existingRequest.setContext(roleInt);
            existingRequest.setState(RequestState.Pending);
            context.adminRequests().update(existingRequest);
        } else {
            AdminRequest request = new AdminRequest(
                    context.getSession().getActiveUser(),
                    targetUser,
                    action,
                    RequestState.Pending,
                    reason,
                    roleInt
            );
            context.adminRequests().create(request);
        }
        context.router().navigate(MyPages.ADMIN_PENDING);
    }
}