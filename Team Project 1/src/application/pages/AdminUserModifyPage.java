package src.application.pages;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.User;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

import java.sql.SQLException;
import java.util.ArrayList;

import static src.utils.permissions.RolesUtil.*;

/**
 * AdminPage class represents the user interface for the admin user.
 * This Page shows detailed user information and allows modification of users
 */
public class AdminUserModifyPage {
    /**
     * Displays the admin page in the provided primary stage.
     *
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    private final User user;
    private final AppContext context;

    public AdminUserModifyPage(User user) throws SQLException {
        this.context = AppContext.getInstance();
        this.user = user;
    }

    public void show(Stage primaryStage, User currentUser) {
        VBox layout = new VBox();

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        int roleInt = user.getRoles();
        Roles[] roles = RolesUtil.intToRoles(roleInt);
        //Label to display the user information
        Label userNameLabel = new Label("Name: " + user.getUserName());
        //Instantiate and declare Checkboxes
        CheckBox userRoleUser = new CheckBox("User");
        CheckBox userRoleAdmin = new CheckBox("Admin");
        CheckBox userRoleStudent = new CheckBox("Student");
        CheckBox userRoleInstructor = new CheckBox("Instructor");
        CheckBox userRoleStaff = new CheckBox("Staff");
        CheckBox userRoleReviewer = new CheckBox("Reviewer");
        //Button to Delete this User from the Database
        Button userButton = new Button("Delete");
        //Instantiate and declare Alert confirmation boxes
        Alert confirm = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete User " + user.getUserName() + "?");
        Alert adminError = new Alert(AlertType.ERROR, "You cannot remove this admin!");
        Button backButton = new Button("Back");
        // Set backButton to redirect to home page
        backButton.setOnAction(a -> {
            try {
                new AdminUserPage().show(primaryStage, user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        //set initial checkbox values
        userRoleAdmin.selectedProperty().set(hasRole(roles, Roles.ADMIN));
        userRoleReviewer.selectedProperty().set(hasRole(roles, Roles.INSTRUCTOR));
        userRoleStudent.selectedProperty().set(hasRole(roles, Roles.STUDENT));
        userRoleReviewer.selectedProperty().set(hasRole(roles, Roles.REVIEWER));
        userRoleStaff.selectedProperty().set(hasRole(roles, Roles.STAFF));
        //Sets the User Checkbox actions(https://stackoverflow.com/questions/13726824/javafx-event-triggered-when-selecting-a-check-box)
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (event.getSource() instanceof CheckBox checkBox) {
                    if (roles.length == 2) {

                    }
                    //Remove/add roles
                    if ("Admin".equals(checkBox.getText())) {
                        if (!checkBox.isSelected()) {
                            adminRoleRemovalConfirm(primaryStage, currentUser);
                            user.setRoles(removeRole(roleInt, Roles.ADMIN));
                        } else {
                            user.setRoles(addRole(roleInt, Roles.ADMIN));
                        }
                    } else if ("Instructor".equals(checkBox.getText())) {
                        if (!checkBox.isSelected()) {
                            user.setRoles(removeRole(roleInt, Roles.INSTRUCTOR));
                        } else {
                            user.setRoles(addRole(roleInt, Roles.INSTRUCTOR));
                        }
                    } else if ("Student".equals(checkBox.getText())) {
                        if (!checkBox.isSelected()) {
                            user.setRoles(removeRole(roleInt, Roles.STUDENT));
                        } else {
                            user.setRoles(addRole(roleInt, Roles.STUDENT));
                        }
                    } else if ("Reviewer".equals(checkBox.getText())) {
                        if (!checkBox.isSelected()) {
                            user.setRoles(removeRole(roleInt, Roles.REVIEWER));
                        } else {
                            user.setRoles(addRole(roleInt, Roles.REVIEWER));
                        }
                    } else if ("Staff".equals(checkBox.getText())) {
                        if (!checkBox.isSelected()) {
                            user.setRoles(removeRole(roleInt, Roles.STAFF));
                        } else {
                            user.setRoles(addRole(roleInt, Roles.STAFF));
                        }
                    }
                    context.users().update(user);
                }
            }
        };

        //Sets the Admin Checkbox actions
        userRoleAdmin.setOnAction(event);
        //Sets the Instructor Checkbox actions
        userRoleInstructor.setOnAction(event);
        //Sets the Student Checkbox actions
        userRoleStudent.setOnAction(event);
        //Sets the Reviewer Checkbox actions
        userRoleReviewer.setOnAction(event);
        //Sets the Staff Checkbox actions
        userRoleStaff.setOnAction(event);
        // Set userButton to delete user with confirmation box
        userButton.setOnAction(a -> {
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    //Delete user if they don't have admin role
                    if (!hasRole(roles, Roles.ADMIN)) {
                        context.users().delete(user.getId());
                    }
                    //if they have the admin role, throw an error
                    else {
                        adminError.show();
                    }
                    try {
                        new AdminUserPage().show(primaryStage, user);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (response == ButtonType.CANCEL) {
                    confirm.close();
                }
            });
        });
        userNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        layout.getChildren().addAll(userNameLabel, userButton, userRoleAdmin, userRoleStudent, userRoleInstructor, userRoleReviewer, userRoleStaff, backButton);
        Scene adminScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }

    public void adminRoleRemovalConfirm(Stage primaryStage, User currentUser) {
        int roleInt = user.getRoles();
        ArrayList<User> list = (ArrayList<User>) context.users().getAll();
        //count all admins in the system
        int count = (int) list.stream().filter(value -> hasRole(value.getRoles(), Roles.ADMIN)).count();
        //declare and instantiate  Alerts for later use
        Alert adminConfirm = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete admin from User " + user.getUserName() + "?");
        Alert adminError = new Alert(AlertType.ERROR, "You cannot remove the last admin!");

        adminConfirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                //Delete role if they aren't the last admin
                if (count > 1) {
                    user.setRoles(removeRole(roleInt, Roles.ADMIN));
                    context.users().update(user);
                    if (currentUser.getId() == user.getId()) {
                        new UserHomePage().show(primaryStage, user, Roles.USER);
                    }
                }
                //if
                else {
                    adminError.show();
                }
            } else if (response == ButtonType.CANCEL) {
                adminConfirm.close();
            }
        });
    }

    public void oneRoleRemoval(Stage primaryStage, User currentUser) {
        int roleInt = user.getRoles();
        ArrayList<User> list = (ArrayList<User>) context.users().getAll();
        //count all admins in the system
        int count = (int) list.stream().filter(value -> hasRole(value.getRoles(), Roles.ADMIN)).count();
        //declare and instantiate  Alerts for later use
        Alert adminError = new Alert(AlertType.ERROR, "You cannot remove the last role from this user!");
        adminError.show();
    }
}