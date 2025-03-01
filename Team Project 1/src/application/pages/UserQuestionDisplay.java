package application.pages;

import application.framework.*;
import database.model.entities.PrivateMessage;
import database.model.entities.Question;
import database.model.entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * UserQuestionDisplay page shows the current user's questions
 * (and reserved space for answers). Double-clicking a question is intended
 * to load its detail page.
 */
@Route(MyPages.USER_QUESTION_DISPLAY)
@application.framework.View(title = "User Questions")
public class UserQuestionDisplay extends BasePage {

    public UserQuestionDisplay() {
        super();
    }

    @Override
    public Pane createView() {
        // Main container with consistent styling from DesignGuide
        VBox container = new VBox(15);
        container.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Create a split pane: left for Questions, right for Private Messages (future use)
        SplitPane splitPane = new SplitPane();

        User user = context.getSession().getActiveUser();

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
                    if (q != null) {
                        //createAnswerStage(q.getId());
                        //showAnswerWindow(q.getId());
                    }
                }
            });
            return row;
        });

        // Populate columns using CellValueFactory (if needed, otherwise use PropertyValueFactory)
        TableColumn<Question, String> idCol = new TableColumn<>("QuestionID");
        idCol.setCellValueFactory(param -> {
            Question q = param.getValue();
            int questionInt = q.getId();
            return new SimpleStringProperty(String.valueOf(questionInt).trim());
        });

        TableColumn<Question, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Question, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(param -> {
            Question q = param.getValue();
            Timestamp timestamp = q.getMessage().getCreatedAt();
            return new SimpleStringProperty(timestamp.toString());
        });

        questionTable.getColumns().addAll(idCol, titleCol, timeCol);

        //Private message table declaration
        TableView<PrivateMessage> privateMessageTable = new TableView<>();
        ArrayList<PrivateMessage> tempList;
        ObservableList<PrivateMessage> obPMs = FXCollections.observableArrayList();
        for (Question q : context.questions().getQuestionsByUser(context.getSession().getActiveUser().getId())) {
            tempList = (ArrayList<PrivateMessage>) context.privateMessages().getRepliesToQuestion(q.getId());
            obPMs.addAll(tempList);
        }
        privateMessageTable.setItems(obPMs);

        //Set up double click functionality for privateMessageTable
        privateMessageTable.setRowFactory(tv -> {
            TableRow<PrivateMessage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    PrivateMessage pm = row.getItem();
                    PrivateMessageConvoPage.setTargetPM(pm);
                    context.router().navigate(MyPages.PRIVATE_CONVERSATION);
                }
            });
            return row;
        });

        //Populate PM table w/ QuestionIds
        TableColumn<PrivateMessage, String> pmCol = new TableColumn<>("QuestionID");
        pmCol.setCellValueFactory(param -> {
            PrivateMessage pm = param.getValue();
            int questionInt = pm.getQuestionId();
            return new SimpleStringProperty(String.valueOf(questionInt).trim());
        });
        //Populate PM table w/ usernames
        TableColumn<PrivateMessage, String> pmUserCol = new TableColumn<>("Username");
        pmUserCol.setCellValueFactory(param -> {
            PrivateMessage pm = param.getValue();
            int userId = pm.getMessage().getUserId();
            String userName = context.users().getById(userId).getUserName();
            return new SimpleStringProperty(userName.trim());
        });
        //Add PM columns
        privateMessageTable.getColumns().addAll(pmCol, pmUserCol);

        //Set up SentPMTable
        //Private message table declaration
        TableView<PrivateMessage> sentPrivateMessageTable = new TableView<>();
        ObservableList<PrivateMessage> obSPMs = FXCollections.observableArrayList();
        for (PrivateMessage pm : context.privateMessages().getPrivateMessagesByUser(user.getId())) {
            if (pm.doesQuestionIdExist()) {
                obSPMs.add(pm);
            }
        }
        sentPrivateMessageTable.setItems(obSPMs);

        //Set up double click functionality for sentPrivateMessageTable
        sentPrivateMessageTable.setRowFactory(tv -> {
            TableRow<PrivateMessage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    PrivateMessage pm = row.getItem();
                    PrivateMessageConvoPage.setTargetPM(pm);
                    context.router().navigate(MyPages.PRIVATE_CONVERSATION);
                }
            });
            return row;
        });

        //Populate SPM table w/ QuestionIds
        TableColumn<PrivateMessage, String> spmCol = new TableColumn<>("QuestionID");
        spmCol.setCellValueFactory(param -> {
            PrivateMessage pm = param.getValue();
            int questionInt = pm.getQuestionId();
            return new SimpleStringProperty(String.valueOf(questionInt).trim());
        });
        //Populate SPM table w/ usernames
        TableColumn<PrivateMessage, String> spmUserCol = new TableColumn<>("Username");
        spmUserCol.setCellValueFactory(param -> {
            PrivateMessage pm = param.getValue();
            int userId = pm.getMessage().getUserId();
            String userName = context.users().getById(userId).getUserName();
            return new SimpleStringProperty(userName.trim());
        });
        sentPrivateMessageTable.getColumns().addAll(spmCol, spmUserCol);

        // Add tables to the split pane
        SplitPane sp = new SplitPane();
        sp.getItems().addAll(privateMessageTable, sentPrivateMessageTable);
        splitPane.getItems().addAll(questionTable, sp);

        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_HOME, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));

        toolbar.getChildren().addAll(backButton, logoutButton);

        container.getChildren().addAll(splitPane, toolbar);
        return container;
    }
}