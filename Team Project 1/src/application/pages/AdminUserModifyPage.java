package src.application.pages;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import src.application.AppContext;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.User;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static src.utils.permissions.RolesUtil.hasRole;

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
    public void show(Stage primaryStage) {
        VBox layout = new VBox();

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        AtomicInteger roleInt = new AtomicInteger(user.getRoles());
        Roles[] roles = RolesUtil.parseRoles(roleInt.get());
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
        Alert adminError = new Alert(AlertType.CONFIRMATION, "You cannot remove this admin!");
        Button backButton = new Button("Back");
        // Set backButton to redirect to home page
        backButton.setOnAction(a -> {
            try {
                new AdminUserPage().show(primaryStage, user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        userRoleAdmin.selectedProperty().set(hasRole(roles, Roles.ADMIN));
        //Sets the User Checkbox actions(https://stackoverflow.com/questions/13726824/javafx-event-triggered-when-selecting-a-check-box)
        userRoleUser.selectedProperty().set(hasRole(roles, Roles.USER));
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                if(event.getSource() instanceof CheckBox){
                    CheckBox checkBox = (CheckBox) event.getSource();
                    //Swap User role from
                    if("User".equals(checkBox.getText())){
                        if(hasRole(roles, Roles.USER)){

                        }
                    } else if ("Admin".equals(checkBox.getText())) {
                        if(hasRole(roles, Roles.ADMIN)){

                        }
                    }else if ("Instructor".equals(checkBox.getText())) {
                        if(hasRole(roles, Roles.INSTRUCTOR)){

                        }
                    }else if ("Student".equals(checkBox.getText())) {
                        if(hasRole(roles, Roles.STUDENT)){

                        }
                    }else if ("Reviewer".equals(checkBox.getText())) {
                        if(hasRole(roles, Roles.REVIEWER)){

                        }
                    }else if ("Staff".equals(checkBox.getText())) {
                        if(hasRole(roles, Roles.STAFF)){

                        }
                    }
                }
            }
        };
        //Sets the AdminCheckBox actions

        //Sets the Instructor Checkbox actions

        //Sets the Student Checkbox actions

        //Sets the Reviewer Checkbox actions

        //Sets the Staff Checkbox actions

        // Set userButton to delete user with confirmation box
        userButton.setOnAction(a -> {
            confirm.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK) {
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
                }else if(response == ButtonType.CANCEL) {
                    confirm.close();
                }
            });
        });
        userNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        layout.getChildren().addAll(userNameLabel, userButton, userRoleUser, userRoleAdmin, backButton);
        Scene adminScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}