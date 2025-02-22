package src.application.pages;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.database.model.entities.Answer;
import src.database.model.entities.Question;
import src.database.model.entities.User;
import src.utils.permissions.Roles;

import java.sql.SQLException;

public class UserQuestionDisplay {
    public void show(Stage primaryStage, User user, Roles userCurrentRole) throws SQLException {
        //Setup all elements
        VBox scene = new VBox(15);
        SplitPane splitPane = new SplitPane();
        TableView<Question> userTable = new TableView<>();
        TableView<Answer> PMTable = new TableView<>();
        HBox hBox = new HBox();
        scene.getChildren().addAll(splitPane, hBox);

        //Setup back button
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            try {
                new SetupLoginSelectionPage().show(primaryStage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        hBox.getChildren().add(backButton);

        //Setup logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            try {
                new UserLoginPage().show(primaryStage);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        hBox.getChildren().add(logoutButton);

        splitPane.getItems().addAll(userTable, PMTable);
        Scene userScene = new Scene(scene, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}
