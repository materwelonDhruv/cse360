package application.pages;

import application.framework.*;
import database.model.entities.Answer;
import database.model.entities.Message;
import database.model.entities.Question;
import database.model.entities.User;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

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

    //Question and Question title TextFields
    private final TextField questionTitleInput = UIFactory.createTextField("Enter the title", f ->
            f.minWidth(200).maxWidth(600).minChars(5).maxChars(10));
    private final TextField questionInput = UIFactory.createTextField("Enter question", f ->
            f.minWidth(200).maxWidth(600).minChars(10).maxChars(MAX_LENGTH));

    //Answer TextField
    private final TextField answerInput = UIFactory.createTextField("Enter answer", f ->
            f.minWidth(500).maxWidth(1200).minChars(10).maxChars(600));

    //Question and Answers list to store and
    //interact with each element in the list -- questions and answers
    private final ListView<Pair<Integer, String>> questionListView = new ListView<>();
    private final ListView<Pair<Integer, String>> answerListView = new ListView<>();

    //keeping track of selected element in listViews
    private int currentlySelectedQuestionId = -1;

    //keeping track of whether all questions are being shown or only unresolved
    private Boolean showingAllQuestions = true;

    //Questions and Answer Stages
    private Stage questionStage;
    private Stage answerStage;

    public UserHomePage() {
        super();

    }

    @Override
    public Pane createView() {
        loadQuestions();

        VBox layout = new VBox(10);

        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Retrieve the active user from session.
        User user = context.getSession().getActiveUser();
        if (user == null) {
            return new VBox(new Label("No active user found."));
        }


        // Greeting and role display.
        int roleInt = user.getRoles();
        Roles[] allRoles = RolesUtil.intToRoles(roleInt);

        // Assume primary role is the first one.
        Roles userCurrentRole = context.getSession().getCurrentRole();
        Label userLabel = UIFactory.createLabel("Hello, " + user.getFirstName() + "!" + "      " +
                "Role: " + userCurrentRole, f ->
                f.style("-fx-font-weight: bold;-fx-font-size: 16px;"));

        // Create Question Display buttons.
        Button questionDisplayButton = UIFactory.createButton("Your Homepage", e -> e.routeToPage(MyPages.USER_QUESTION_DISPLAY, context));

        //Add button to add a question
        Button addQuestionButton = UIFactory.createButton("Add", e -> e.onAction(a -> ShowQuestionWindow()));

        //Edit button to edit a question
        Button editQuestionButton = UIFactory.createButton("Edit", e -> e.onAction(a -> {
            Pair<Integer, String> selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                editQuestionWindow(selectedQuestion.getKey());
            }
        }));

        //Delete button to delete a question
        Button deleteQuestionButton = UIFactory.createButton("Delete", e -> e.onAction
                (a -> deleteQuestion()));

        //Toggle unresolved questions only button
        Button unresolvedQuestionsButton = UIFactory.createButton("Show Unresolved only");
        unresolvedQuestionsButton.setOnAction(a -> {
            showingAllQuestions = !showingAllQuestions;
            loadQuestions();
            if (showingAllQuestions) {
                unresolvedQuestionsButton.setText("Show Unresolved Only");
            } else {
                unresolvedQuestionsButton.setText("Show All");
            }
        });

        //Creating log out button
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));

        //Add spacer for better UI
        Region spacer = new Region();
        spacer.setPrefWidth(250);
        //Button Bar above ListView for horizontal orientation
        HBox buttonBar = new HBox(10, questionDisplayButton, addQuestionButton, editQuestionButton, deleteQuestionButton, unresolvedQuestionsButton, spacer, logoutButton);

        //Call the Question stage and Answer stage
        createQuestionStage(user.getId());

        //Go to answer list on double-click
        questionListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pair<Integer, String> selectedItem = questionListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    currentlySelectedQuestionId = selectedItem.getKey();
                    createAnswerStage(currentlySelectedQuestionId);
                    showAnswerWindow(currentlySelectedQuestionId);
                }
            }
        });

        //On
        questionListView.setCellFactory(lv -> new ListCell<Pair<Integer, String>>() {
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getValue()); // Display only the answer content
                }
            }
        });

        layout.getChildren().addAll(userLabel, buttonBar, questionListView);

        // If more than one role, add a role selection dropdown and a Go button.
        if (allRoles.length > 1) {
            final Roles[] selectedRole = new Roles[1];

            MenuButton roleMenu = new MenuButton("Select Role");

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

            Button goButton = UIFactory.createButton("Go", e -> e.onAction(a -> {
                if (selectedRole[0] != null && RolesUtil.hasRole(selectedRole, Roles.ADMIN)) {
                    context.router().navigate(MyPages.ADMIN_HOME);
                } else if (selectedRole[0] != null) {
                    context.getSession().setCurrentRole(selectedRole[0]);
                    context.router().navigate(MyPages.USER_HOME);
                }
            }));
            buttonBar.getChildren().addAll(roleMenu, goButton);
        }
        return layout;
    }


//--------------------------------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------//
//Question Stage and Methods

    //Creating a stage for question
    private void createQuestionStage(int userID) {
        questionStage = new Stage();
        questionStage.initModality(Modality.NONE);

        //UI for question window
        Label questionContent = UIFactory.createLabel("Question", f -> f.style("-fx-font-weight: bold;"));
        Label questionTitle = UIFactory.createLabel("Title", f -> f.style("-fx-font-weight: bold;"));
        Button createButton = UIFactory.createButton("Add Question", e -> e.onAction(a -> addQuestion(userID)));
        Button closeButton = UIFactory.createButton("Close", e -> e.onAction(a -> questionStage.close()));

        // Opening the answer window after a double click is detected on the list item


        VBox questionLayout = new VBox(10, questionTitle, questionTitleInput, questionContent, questionInput, createButton, closeButton);
        questionStage.setScene(new Scene(questionLayout, 300, 400));
    }


    //Method to load all the questions from the database
    // And adding it to the question list view
    private void loadQuestions() {
        questionListView.getItems().clear();
        List<Question> questionList;
        // Use Questions class
        if (showingAllQuestions) {
            questionList = context.questions().getAll();
        } else {
            questionList = context.questions().getUnansweredQuestions(); // Need a getUnresolvedQuestions() method
        }

        for (Question q : questionList) {
            questionListView.getItems().add(new Pair<>(q.getId(), q.getTitle()));
        }
    }

    //Method to add a question with the userID
    private void addQuestion(int userID) {
        String title = questionTitleInput.getText();
        String content = questionInput.getText().trim();
        if (!content.isEmpty()) {
            Message message = new Message(userID, content);
            Question newQuestion = new Question(message, title);

            Question createdQuestion = null;
            try {
                createdQuestion = context.questions().create(newQuestion);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
            questionListView.getItems().add(new Pair<>(createdQuestion.getId(), createdQuestion.getTitle()));
            questionInput.clear();
        }
    }

    private void deleteQuestion() {
        Pair<Integer, String> selectedItem = questionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            context.questions().delete(selectedItem.getKey());// Use Questions class
            questionListView.getItems().remove(selectedItem);
        }
    }

    private void editQuestionWindow(int questionId) {
        Stage editorStage = new Stage();
        editorStage.initModality(Modality.APPLICATION_MODAL);

        //labels for editing title and content
        Label editQuestionTitle = UIFactory.createLabel("Edit Question Title");
        Label editQuestionContent = UIFactory.createLabel("Edit Question Content");

        String currentTitle = context.questions().getById(questionId).getTitle().trim();
        String currentContent = context.questions().getById(questionId).getMessage().getContent().trim();

        TextField editQuestionTitleField = UIFactory.createTextField("New title", f -> f.defaultText(currentTitle));
        TextField editQuestionField = UIFactory.createTextField("New content", f -> f.defaultText(currentContent));

        //save button to save the updated content
        Button saveButton = UIFactory.createButton("Save", e -> e.onAction(a -> {
            String newContent = editQuestionField.getText();
            context.questions().updateQuestionContent(questionId, newContent);
            loadQuestions(); // Refresh the question list
            editorStage.close();
        }));

        //cancel button to close the edit window
        Button cancelButton = UIFactory.createButton("Cancel", e -> e.onAction(
                a -> {
                    editQuestionTitleField.clear();
                    editQuestionField.clear();
                    editorStage.close();
                }));

        VBox editorLayout = new VBox(10, editQuestionTitle, editQuestionTitleField, editQuestionContent, editQuestionField, saveButton, cancelButton);
        editorStage.setScene(new Scene(editorLayout, 400, 200));
        editorStage.show();
    }

    //Opening thr Question window
    private void ShowQuestionWindow() {
        questionStage.setTitle("Create Question");
        questionStage.show();
    }

//------------------------------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------//
//Answer stage and methods

    //Creating the Answer Stage
    private void createAnswerStage(int questionId) {
        answerStage = new Stage();
        answerStage.initModality(Modality.NONE);

        Question queContent = null;
        if (context.questions().getById(questionId) != null) {
            queContent = context.questions().getById(questionId);
        }

        //Question for the answers/ Labels
        assert queContent != null;
        Label questionContent = UIFactory.createLabel("Question: " + queContent.getMessage().getContent(),
                f -> f.style("-fx-font-weight: bold; -fx-font-size: 13pt;"));
        Label answerLabelList = UIFactory.createLabel("Answers:", f -> f.style("-fx-font-weight: bold;-fx"));

        //Adding answer UI
        Button addAnswerButton = UIFactory.createButton("Add Answer", e -> e.onAction(
                a -> addAnswer()));

        //Close answerStage UI
        Button closeButton = UIFactory.createButton("Close", e -> e.onAction(
                a -> {
                    answerInput.clear();
                    answerStage.close();
                }));

        //Edit Answer Button
        Button editAnswerButton = UIFactory.createButton("Edit Answer", e -> e.onAction(a -> {
            Pair<Integer, String> selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer != null) {
                editAnswerWindow(selectedAnswer.getKey());
            }
        }));

        //Delete Answer Button
        Button deleteAnswerButton = UIFactory.createButton("Delete Answer", e -> e.onAction(a -> deleteAnswer()));

        //Change the listview to only show the content without the ID
        answerListView.setCellFactory(lv -> new ListCell<Pair<Integer, String>>() {
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getValue()); // Display only the answer content
                }
            }
        });

        HBox addUI = new HBox(10, addAnswerButton, answerInput);
        HBox editUI = new HBox(10, answerLabelList, editAnswerButton, deleteAnswerButton);

        //Layout to show every UI
        VBox answerLayout = new VBox(questionContent, addUI, editUI, answerListView, closeButton);
        answerStage.setScene(new Scene(answerLayout, 600, 500));
    }


    //Method to load all the answer given the question ID
    private void loadAnswers(int questionID) {
        answerListView.getItems().clear();
        List<Answer> answerList = context.answers().getRepliesToQuestion(questionID);
        for (Answer a : answerList) {
            answerListView.getItems().add(new Pair<>(a.getId(), a.getMessage().getContent()));
        }
    }


    //method to add an answer
    private void addAnswer() {
        if (currentlySelectedQuestionId == -1) return;
        String content = answerInput.getText().trim();
        if (!content.isEmpty()) {
            Message message = new Message(context.getSession().getActiveUser().getId(), content);
            Answer newAnswer = new Answer(message, currentlySelectedQuestionId, null, false);

            Answer createdAnswer = null;
            try {
                createdAnswer = context.answers().create(newAnswer);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
            answerListView.getItems().add(new Pair<>(createdAnswer.getId(), createdAnswer.getMessage().getContent()));
            answerInput.clear();

        }
    }

    //method to delete answer
    private void deleteAnswer() {
        Pair<Integer, String> selectedItem = answerListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            context.answers().delete(selectedItem.getKey()); // Use the Answers instance to delete
            answerListView.getItems().remove(selectedItem); // Remove the item from the ListView
        }
    }

    //edit answer window to edit an answer
    private void editAnswerWindow(int answerId) {
        Stage answerEditStage = new Stage();
        answerEditStage.initModality(Modality.APPLICATION_MODAL);
        answerEditStage.setTitle("Edit Answer");

        //getting current content
        String currentContent = context.answers().getById(answerId).getMessage().getContent().trim();

        //UI for cancelling or saving a new answer
        Label label = UIFactory.createLabel("Edit Answer:");
        TextField answerInputField = UIFactory.createTextField("", f ->
                f.defaultText(currentContent));

        Button saveButton = UIFactory.createButton("Save", e -> e.onAction(a -> {
            String newContent = answerInputField.getText();
            context.answers().updateAnswerContent(answerId, newContent);
            loadAnswers(currentlySelectedQuestionId);
            answerEditStage.close();
        }));
        //Button to close the answer stage
        Button cancelButton = UIFactory.createButton("Cancel", e -> e.onAction(a -> {
            answerInputField.clear();
            answerEditStage.close();
        }));

        VBox layout = new VBox(10, label, answerInputField, saveButton, cancelButton);
        Scene scene = new Scene(layout, 300, 150);
        answerEditStage.setScene(scene);
        answerEditStage.showAndWait();
    }

    //Opening thr answers window
    private void showAnswerWindow(int questionID) {
        answerStage.setTitle(context.questions().getById(questionID).getTitle());
        loadAnswers(questionID);
        answerStage.show();
    }

}