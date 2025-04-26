package application.pages.admin;

import application.framework.*;
import database.model.entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;

import static utils.permissions.RolesUtil.intToRoles;
import static utils.permissions.RolesUtil.roleName;

/**
 * Displays a list of all users for modification.
 * Double-clicking a row sets the target user in AdminUserModifyPage
 * and navigates to that page.
 *
 * @author Mike
 */
@Route(MyPages.ADMIN_USER)
@View(title = "Admin User Page")

public class AdminUserPage extends BasePage {
    User user = context.getSession().getActiveUser();
    Roles role = context.getSession().getCurrentRole();

    /**
     * Constructor using BasePage
     */
    public AdminUserPage() {
        super();
    }


    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Header label
        Button backButton = UIFactory.createBackButton(context);
        var header = UIFactory.createLabel("Choose a User to Modify");

        // Create TableView to display users
        TableView<User> userTable = new TableView<>();
        ObservableList<User> users = FXCollections.observableArrayList(context.users().getAll());
        userTable.setItems(users);

        // Define columns
        TableColumn<User, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(param -> {
            User u = param.getValue();
            int rolesInt = u.getRoles();
            Roles[] roleList = intToRoles(rolesInt);
            StringBuilder out = new StringBuilder();
            for (Roles r : roleList) {
                out.append(roleName(r)).append(", ");
            }
            return new SimpleStringProperty(out.toString().trim());
        });

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> fnameCol = new TableColumn<>("First Name");
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> lnameCol = new TableColumn<>("Last Name");
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        userTable.getColumns().setAll(userNameCol, roleCol, emailCol, fnameCol, lnameCol);

        // Set row factory: on double-click, set target user and navigate to modify page.
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    User rowUser = row.getItem();
                    try {
                        AdminUserModifyPage.setTargetUser(rowUser);
                        AdminUserModifyPage.setAdmin(user);
                        context.router().navigate(MyPages.ADMIN_USER_MODIFY);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return row;
        });

        layout.getChildren().addAll(header, userTable, backButton);
        return layout;
    }
}