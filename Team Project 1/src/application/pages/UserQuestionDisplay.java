package application.pages;

import application.framework.*;
import database.model.entities.Answer;
import database.model.entities.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * UserQuestionDisplay page shows the current user's questions
 * (and reserved space for answers). Double-clicking a question is intended
 * to load its detail page.
 */
@Route(MyPages.USER_QUESTION_DISPLAY)
@View(title = "User Questions")
public class UserQuestionDisplay extends BasePage {

    public UserQuestionDisplay() {
        super();
    }

    @Override
    public Pane createView() {
        // Main container with consistent styling from DesignGuide
        VBox container = new VBox(15);
        container.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Create a split pane: left for Questions, right for Answers (future use)
        SplitPane splitPane = new SplitPane();

        // Create table for user's questions
        TableView<Question> questionTable = new TableView<>();
        ObservableList<Question> obQuestions = FXCollections.observableArrayList(
                context.questions().getQuestionsByUser(context.getSession().getActiveUser().getId())
        );
        questionTable.setItems(obQuestions);

        // Set up double-click on a row to load question details
        questionTable.setRowFactory(tv -> {
            TableRow<Question> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Question q = row.getItem();
                    System.out.println("Load details for: " + q.getTitle());
                    // TODO: Navigate to question detail page
                }
            });
            return row;
        });

        // Populate columns using UIFactory (if needed, otherwise create directly)
        TableColumn<Question, String> idCol = new TableColumn<>("QuestionID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("questionID"));
        TableColumn<Question, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Question, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        questionTable.getColumns().addAll(idCol, titleCol, timeCol);

        // Placeholder for Answer table; will be populated later
        TableView<Answer> answerTable = new TableView<>();

        // Add tables to the split pane
        splitPane.getItems().addAll(questionTable, answerTable);

        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_HOME, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));

        toolbar.getChildren().addAll(backButton, logoutButton);

        container.getChildren().addAll(splitPane, toolbar);
        return container;
    }
}