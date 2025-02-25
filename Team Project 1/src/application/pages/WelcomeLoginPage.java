package application.pages;

import application.AppContext;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import database.model.entities.User;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import database.repository.DataAccessException;

import java.sql.SQLException;


/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or
 * quit the application.
 */
public class WelcomeLoginPage {

    private final AppContext context;

    public WelcomeLoginPage() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage, User user) {

        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        String username = user.getUserName();

        Label welcomeLabel = new Label("Welcome " + username + "!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        //get all the roles assigned to the user
        int roleInt = user.getRoles();
        Roles[] roles = RolesUtil.intToRoles(roleInt);

        //continue button to go to user page
        Button continueButton = new Button("Continue to your page");

        // Button to quit the application
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(_ -> {
            try {
                context.closeConnection();
            } catch (SQLException e) {
                throw new DataAccessException("Cannot close in WelcomePage", e);
            }
            Platform.exit(); // Exit the JavaFX application
        });

        //Just show continue button to your page if only 1 role assigned
        if (roles.length == 1) {
            if (RolesUtil.hasRole(roles, Roles.ADMIN)) {
                continueButton.setOnAction(e -> {
                    try {
                        new AdminHomePage().show(primaryStage, user);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } else {
                continueButton.setOnAction(e -> {
                    new UserHomePage().show(primaryStage, user, roles[0]);
                });
            }
        }
        // Dropdown menu to choose from all the assigned roles
        MenuButton roleMenu = new MenuButton("Select Role");

        //the role selected by the user from the menu bar
        //Show menu bar only if more than 1 role assigned
        Roles[] selectedRole = new Roles[1];
        if (roles.length > 1) {
            for (Roles role : roles) {
                MenuItem roleItem = new MenuItem(role.toString());
                roleItem.setOnAction(e -> {
                    selectedRole[0] = role;
                    roleMenu.setText(role.toString());
                });
                roleMenu.getItems().add(roleItem);
            }
        }

        //Only change the action if more than 1 role is assigned
        //continue button which changes depending on the selectedRole
        if (roles.length > 1) {
            continueButton.setOnAction(e -> {
                if (RolesUtil.hasRole(selectedRole, Roles.ADMIN)) {
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
        }

        // Add components to the layout
        layout.getChildren().addAll(welcomeLabel, continueButton);
        if (roles.length > 1) {
            layout.getChildren().add(roleMenu);
        }

        // Set the scene to primary stage
        Scene welcomeScene = new Scene(layout, 800, 400);
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Role Select");
    }

}