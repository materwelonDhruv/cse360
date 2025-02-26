package application.pages;

import application.framework.BasePage;
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

@src.application.framework.Route(src.application.framework.MyPages.ADMIN_USER)
@src.application.framework.View(title = "Admin User Page")
public class AdminUserPage extends BasePage {

    public AdminUserPage() {
        super();
    }

    /**
     * Displays a list of all users for modification.
     * Double-clicking a row sets the target user in AdminUserModifyPage
     * and navigates to that page.
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(src.application.framework.DesignGuide.MAIN_PADDING + " " + src.application.framework.DesignGuide.CENTER_ALIGN);

        // Header label
        Button backButton = src.application.framework.UIFactory.createButton("Back", e -> context.router().navigate(src.application.framework.MyPages.ADMIN_HOME));
        var header = src.application.framework.UIFactory.createLabel("Choose a User to Modify", src.application.framework.DesignGuide.TITLE_LABEL, null);

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
                out.append(roleName(r)).append(" ");
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
                        context.router().navigate(src.application.framework.MyPages.ADMIN_USER_MODIFY);
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