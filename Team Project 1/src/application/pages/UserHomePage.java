package application.pages;

import application.AppContext;
import application.framework.*;
import database.model.entities.Message;
import database.model.entities.Question;
import database.model.entities.User;
import database.repository.repos.Questions;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.io.IOException;
import java.util.List;

/**
 * This page displays a simple welcome message for the user and provides navigation.
 * It shows the user's current role and, if multiple roles exist, a dropdown to select another.
 */
@Route(MyPages.USER_HOME)
@View(title = "User Page")
public class UserHomePage extends BasePage {
    //max length for number of characters in the text field
    private static final int MAX_LENGTH = 300;
    private final int currentlySelectedQuestionId = -1;
    private final TextField questionTitleInput = UIFactory.createTextField("Enter the title", 400);
    private final TextField questionInput = UIFactory.createTextField("Enter question", 600);
    private final TextField answerInput = UIFactory.createTextField("Enter answer", 80);
    //Question and Answers list to store and
    //interact with each element in the list -- questions and answers
    private final ListView<Pair<Integer, String>> questionListView = new ListView<>();
    private final ListView<Pair<Integer, String>> answerListView = new ListView<>();
    Questions questions;
    private Stage answerStage;
    private Stage questionStage;

    public UserHomePage(AppContext context) throws IOException {
        super();

    }

    @Override
    public Pane createView() {
        loadQuestions();

        VBox layout = new VBox(10);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Retrieve the active user from session.
        User user = SessionContext.getActiveUser();
        if (user == null) {
            return new VBox(UIFactory.createLabel("No active user found."));
        }


        // Greeting and role display.
        Label userLabel = UIFactory.createLabel("Hello, " + user.getUserName() + "!");
        int roleInt = user.getRoles();
        Roles[] allRoles = RolesUtil.intToRoles(roleInt);
        // Assume primary role is the first one.
        Roles userCurrentRole = (allRoles.length > 0) ? allRoles[0] : null;
        Label roleLabel = UIFactory.createLabel("Role: " + userCurrentRole);

        // Create Logout and Question Display buttons.
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        Button questionDisplayButton = UIFactory.createButton("Your Homepage",
                e -> e.routeToPage(MyPages.USER_QUESTION_DISPLAY, context));

        //Add button to add a question
        Button addQuestionButton = UIFactory.createButton("Add question", e -> ShowQuestionWindow());

        createQuestionStage(user.getId());

        layout.getChildren().addAll(userLabel, roleLabel, addQuestionButton, logoutButton, questionDisplayButton, questionListView);

        // If more than one role, add a role selection dropdown and a Go button.
        if (allRoles.length > 1) {
            MenuButton roleMenu = new MenuButton("Select Role");
            final Roles[] selectedRole = new Roles[1];
            for (Roles rol : allRoles) {
                if (!rol.equals(userCurrentRole)) {
                    MenuItem roleItem = new MenuItem(rol.toString());
                    roleItem.setOnAction(e -> {
                        selectedRole[0] = rol;
                        roleMenu.setText(rol.toString());
                    });
                    roleMenu.getItems().add(roleItem);
                }
            }
            Button goButton = UIFactory.createButton("Go", e ->
                    e.routeToPage(
                            (selectedRole[0] != null && RolesUtil.hasRole(selectedRole, Roles.ADMIN))
                                    ? MyPages.ADMIN_HOME
                                    : MyPages.USER_HOME,
                            context
                    )
            );
            layout.getChildren().addAll(roleMenu, goButton);
        }
        return layout;
    }

    private void createQuestionStage(int userID) {
        questionStage = new Stage();
        questionStage.initModality(Modality.NONE);

        //UI for question window
        Label questionContent = UIFactory.createLabel("Question", "-fx-font-size: 16px;", null);
        Label questonTitle = UIFactory.createLabel("Title", "-fx-font-size: 16px;", null);
        Button createButton = UIFactory.createButton("Add Question", e -> addQuestion(userID));
        Button closeButton = UIFactory.createButton("Close", e -> questionStage.close());


        VBox questionLayout = new VBox(10, questionTitleInput, questionTitleInput, questionContent, questionInput, createButton, closeButton);
        questionStage.setScene(new Scene(questionLayout, 300, 400));
    }

//        //Again, open AnswerEdit window if double click is detected
//        answerListView.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                Pair<Integer, String> selectedItem = answerListView.getSelectionModel().getSelectedItem();
//                if (selectedItem != null) {
//                    editAnswer(selectedItem.getKey(), selectedItem.getValue()); // Open the edit window
//                }
//            }
//        });

    //Method to load all the questions from the database
    // And adding it to the question list view
    private void loadQuestions() {
        questionListView.getItems().clear();
        List<Question> questionList = context.questions().getAll(); // Use Questions class
        for (Question q : questionList) {
            questionListView.getItems().add(new Pair<>(q.getId(), q.getTitle()));
        }
    }

    private void addQuestion(int userID) {
        String title = questionTitleInput.getText();
        String content = questionInput.getText().trim();
        if (!content.isEmpty()) {
            Message message = new Message(userID, content);
            Question newQuestion = new Question(message, title);
            Question createdQuestion = context.questions().create(newQuestion);
            questionListView.getItems().add(new Pair<>(createdQuestion.getId(), createdQuestion.getTitle()));
            questionInput.clear();
        }
    }

    private void deleteQuestion(int userID) {
        Pair<Integer, String> selectedItem = questionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            questions.delete(selectedItem.getKey());// Use Questions class
            questionListView.getItems().remove(selectedItem);
        }
    }

    //Opening thr Question window
    private void ShowQuestionWindow() {
        questionStage.setTitle("Create Question");
        questionStage.show();
    }


}