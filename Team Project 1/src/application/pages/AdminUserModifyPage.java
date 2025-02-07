package src.application.pages;

import src.application.AppContext;
import src.application.pages.AdminUserPage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.User;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

import java.sql.SQLException;

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

        //Label to display the user information
        Label userNameLabel = new Label("Name: " + user.getUserName());
        CheckBox userRoleUser = new CheckBox("User");
        CheckBox userRoleAdmin = new CheckBox("Admin");
        //Button to Delete this User from the Database
        Button userButton = new Button("Delete");
        //Instantiate and declare Alert confirmation box
        Alert confirm = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete User " + user.getUserName() + "?");
        Button backButton = new Button("Back");
        // Set backButton to redirect to home page
        backButton.setOnAction(a -> {
            try {
                new AdminUserPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        // Set userButton to delete user with confirmation box
        userButton.setOnAction(a -> {
            confirm.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK) {
                    int roleInt = user.getRoles();
                    Roles[] roles = RolesUtil.parseRoles(roleInt);
                    //Delete user if they have admin role
                    if (hasRole(roles, Roles.ADMIN)) {
                        //TODO: add delete
                    }
                    try {
                        new AdminUserPage().show(primaryStage);
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