package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.User;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

import java.sql.SQLException;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {

    public void show(Stage primaryStage, User user, Roles userCurrentRole) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display Hello user
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        //Label to display the current role selected by the user
        Label roleLabel = new Label("Role: " + userCurrentRole);
        roleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        //get all the roles assigned to the user
        int roleInt = user.getRoles();
        Roles[] allRoles = RolesUtil.intToRoles(roleInt);

        // Dropdown menu to choose from all the assigned roles
        MenuButton roleMenu = new MenuButton("Select Role");

        //Don't add items to Menu Bar if only 1 role assigned
        Roles[] selectedRole = new Roles[1];
        if (allRoles.length > 1) {
            for (Roles rol : allRoles) {
                if (rol != userCurrentRole) {
                    MenuItem roleItem = new MenuItem(rol.toString());
                    roleItem.setOnAction(e -> {
                        selectedRole[0] = rol;
                        roleMenu.setText(rol.toString());
                    });
                    roleMenu.getItems().add(roleItem);
                }
            }
        }

        //go button to jump to another page according to the role selected by the user
        Button goButton = new Button("Go");
        goButton.setOnAction(e -> {
            if (RolesUtil.hasRole(selectedRole[0], Roles.ADMIN)) {
                try {
                    new AdminHomePage().show(primaryStage, user);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                if (selectedRole[0] != null) {
                    new UserHomePage().show(primaryStage, user, selectedRole[0]);
                }
            }
        });


        //logout button to Log Out from the account
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            try {
                new UserLoginPage().show(primaryStage);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        layout.getChildren().addAll(userLabel, roleLabel, logoutButton);

        //Don't show the menubar and goButton if only 1 role is assigned
        if (allRoles.length > 1) {
            layout.getChildren().addAll(roleMenu, goButton);
        }

        Scene userScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");

    }
}