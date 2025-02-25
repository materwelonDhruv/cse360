package application.pages;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import application.AppContext;
import application.pages.AdminHomePage;
import application.pages.AdminUserModifyPage;
import database.model.entities.User;
import database.repository.repos.Users;
import utils.permissions.Roles;


import java.sql.SQLException;
import java.util.ArrayList;

import static utils.permissions.RolesUtil.*;

public class AdminUserPage {
    private final AppContext context;

    public AdminUserPage() throws SQLException {
        context = AppContext.getInstance();
    }

    /**
     * Displays a list of all potential users, and
     *
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox();

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // label to display message for the admin
        Label adminLabel = new Label("Choose a User to Modify");
        // Declare a TableView object
        TableView<User> userBox = new TableView<>();
        Button backButton = new Button("Back");
        // Set backButton to redirect to home page
        backButton.setOnAction(a -> {
            try {
                new AdminHomePage().show(primaryStage, user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Users userList;
        //Load users from database
        userList = context.users();
        //Cast the ArrayList to ObservableList
        ObservableList<User> obUserList = FXCollections.observableArrayList();
        obUserList.addAll(userList.getAll());
        userBox.setItems(obUserList);
        //Code for detecting a double click on a TableView (https://stackoverflow.com/questions/26563390/detect-doubleclick-on-row-of-tableview-javafx)
        userBox.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    User rowUser = row.getItem();
                    //load specific modification page for user
                    try {
                        new AdminUserModifyPage(rowUser).show(primaryStage, user);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
        //Fetch usernames from class and display on columns
        TableColumn<User, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        //Fetch Role values and display on column (https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/cell/PropertyValueFactory.html)
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> param) {
                User user = param.getValue();
                int roles = user.getRoles();
                Roles[] roleList = intToRoles(roles);
                String out = "";
                for (Roles value : roleList) {
                    out = out.concat(roleName(value));
                }
                return new SimpleStringProperty(out);
            }
        });
        TableColumn<User, String> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<User, String> fnameCol = new TableColumn<>("First Name");
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<User, String> lnameCol = new TableColumn<>("Last Name");
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        //Load the columns into TableView
        userBox.getColumns().setAll(userNameCol, roleCol, emailCol, fnameCol, lnameCol);

        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        layout.getChildren().addAll(adminLabel, userBox, backButton);
        Scene adminScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}