package src.application.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.application.AppContext;
import src.database.model.entities.Answer;
import src.database.model.entities.Question;
import src.database.model.entities.User;
import src.utils.permissions.Roles;

import java.sql.SQLException;
import java.util.List;

public class UserQuestionDisplay {
    private final AppContext context;

    public UserQuestionDisplay() throws SQLException {
        this.context = AppContext.getInstance();
    }

    public void show(Stage primaryStage, User user, Roles userCurrentRole) throws SQLException {
        //Setup all elements
        VBox scene = new VBox(15);
        SplitPane splitPane = new SplitPane();
        TableView<Question> userQuestionTable = new TableView<>();
        TableView<Answer> PMTable = new TableView<>();
        HBox hBox = new HBox();
        scene.getChildren().addAll(splitPane, hBox);

        //Setup back button
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new UserHomePage().show(primaryStage, user, userCurrentRole);
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

        //Setup Question list
        List<Question> questionList = context.questions().getQuestionsByUser(user.getId());
        //Setup Question table
        ObservableList<Question> obQuestionList = FXCollections.observableArrayList();
        obQuestionList.addAll(questionList);
        userQuestionTable.setItems(obQuestionList);
        //Double click functionality TODO: Make this go to user's question
        userQuestionTable.setRowFactory(tv -> {
            TableRow<Question> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Question rowQuestion = row.getItem();
                    //load specific Question page for user
                    //try {
                    System.out.println("placeholder");
                    //} catch (SQLException e) {
                    //   throw new RuntimeException(e);
                    //}
                }
            });
            return row;
        });
        //Populate columns
        TableColumn<Question, String> questionIDCol = new TableColumn<>("QuestionID");
        questionIDCol.setCellValueFactory(new PropertyValueFactory<>("questionID"));
        TableColumn<Question, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Question, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        userQuestionTable.getColumns().addAll(questionIDCol, titleCol, timeCol);
        //Add tables to scene
        splitPane.getItems().addAll(userQuestionTable, PMTable);
        Scene userScene = new Scene(scene, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}
