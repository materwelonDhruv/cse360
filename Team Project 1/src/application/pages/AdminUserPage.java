package src.application.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.sql.SQLException;
import java.util.ArrayList;

import src.application.AppContext;
import src.database.model.entities.User;

public class AdminUserPage {
	private final AppContext context;
	public AdminUserPage() throws SQLException {
        this.context = AppContext.getInstance();
    }
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display message for the admin
	    Label adminLabel = new Label("Choose a User to Modify");
	    // Declare a listview object
	    TableView<User> userBox = new TableView<>();
	    ArrayList<User> userList = new ArrayList<>();
        //Load users from database
        userList = (ArrayList<User>) context.users().getAll();
        ObservableList<User> obUserList = FXCollections.observableArrayList();
        obUserList.addAll(userList);
        userBox.setItems(obUserList);
        TableColumn<User,String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        TableColumn<User,String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("Role"));
        userBox.getColumns().setAll(userNameCol,roleCol);
        userBox.getSelectionModel();
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().addAll(adminLabel,userBox);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}