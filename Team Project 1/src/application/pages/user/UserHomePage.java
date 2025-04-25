package application.pages.user;

import application.framework.*;
import application.pages.PrivateMessagePage;
import application.pages.ReplyList;
import database.model.entities.Answer;
import database.model.entities.Message;
import database.model.entities.Question;
import database.model.entities.User;
import database.repository.repos.Answers;
import database.repository.repos.Questions;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs the UserHomePage and initializes the layout and user interface components.
 * Handles question creation, display, editing, deletion, and filtering.
 * Also manages navigation and user session-based behavior.
 *
 * @author Atharva
 */
@Route(MyPages.USER_HOME)
@View(title = "User Page")
public class UserHomePage extends BasePage {
    //max length for number of characters in the text field
    private static final int MAX_LENGTH = 300;
    //Creates list view to display search results
    private static final ListView<String> resultView = new ListView<>();
    //Question and Question title TextFields
    private final TextField questionTitleInput = UIFactory.createTextField("Enter the title", f ->
            f.minWidth(200).maxWidth(600).minChars(5).maxChars(100));
    private final TextField questionInput = UIFactory.createTextField("Enter question", f ->
            f.minWidth(200).maxWidth(600).minChars(10).maxChars(MAX_LENGTH));
    //Answer TextField
    private final TextField answerInput = UIFactory.createTextField("Enter answer", f ->
            f.minWidth(500).maxWidth(1200).minChars(10).maxChars(600));
    private final TextField searchInput = UIFactory.createTextField("Search", f ->
            f.minWidth(200).maxWidth(1200).minChars(10).maxChars(48));
    //Question and Answers list to store and
    //interact with each element in the list -- questions and answers
    private final ListView<Pair<Integer, String>> questionListView = new ListView<>();
    private final ListView<Pair<Integer, String>> answerListView = new ListView<>();
    private final ListView<Pair<Integer, String>> searchListView = new ListView<>();

    private final Questions questionsRepo;
    private final Answers answersRepo;

    //keeping track of whether the current question is resolved
    private boolean currentlySelectedQuestionResolved = false;

    //keeping track of selected element in listViews
    private int currentlySelectedQuestionId = -1;

    //keeping track of whether resolved questions are being shown or only unresolved
    private boolean showingResolvedQuestions = true;

    //keeping track of whether only the user's questions are being shown
    private boolean showingUserQuestionsOnly = false;

    //Questions and Answer Stages
    private Stage questionStage;
    private Stage answerStage;

    /**
     * Constructs the UserHomePage and loads the list of questions from the database.
     */
    public UserHomePage() {
        super();
        this.questionsRepo = context.questions();
        this.answersRepo = context.answers();

        loadQuestions();
    }

    /**
     * Updates the ListView used for displaying search results.
     *
     * @param list The list of questions to display as search results.
     */
    public static void updateResults(List<Question> list) {
        resultView.getItems().clear();
        for (Question q : list) {
            resultView.getItems().add(q.getTitle());
        }
        int height = list.size();
        resultView.setPrefHeight(Math.min(height * 26, 150)); //Gives 26 height for every element to cleanly display
    }

//--------------------------------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------//
//Question Stage and Methods

    /**
     * Creates and returns the main user interface view (layout) for the user home page.
     * Includes buttons for adding/editing/deleting/filtering questions and viewing answers.
     *
     * @return The main content pane for the user page.
     */
    @Override
    public Pane createView() {
        loadQuestions();
        searchListView.setPrefHeight(150);// Original height for search listview
        questionListView.setPrefHeight(150);
        resultView.getItems().clear();
        VBox layout = new VBox(10);

        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Retrieve the active user from session.
        User user = context.getSession().getActiveUser();
        if (user == null) {
            return new VBox(new Label("No active user found."));
        }

        //List to hold search results
        List<Question> searchList = new ArrayList<Question>();

        questionTitleInput.setOnKeyReleased(event -> {
            String inputText = questionTitleInput.getText();
            if (inputText.length() > 3) {
                try {
                    searchList.clear(); //Clear previous searches
                    resultView.getItems().clear();
                    searchList.addAll(questionsRepo.searchQuestions(inputText)); //Add search results
                    updateResults(searchList);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Greeting and role display.
        int roleInt = user.getRoles();
        Roles[] allRoles = RolesUtil.intToRoles(roleInt);

        // Assume primary role is the first one.
        Roles userCurrentRole = context.getSession().getCurrentRole();
        Label userLabel = UIFactory.createLabel("Hello, " + user.getFirstName() + "!" + "      " +
                "Role: " + userCurrentRole, f ->
                f.style("-fx-font-weight: bold;-fx-font-size: 16px;"));

        // Create Question Display buttons.
        Button questionDisplayButton = UIFactory.createHomepageButton("Your Homepage", context);

        //Create Trusted Reviewer button
        MenuItem trustedReviewersItem = new MenuItem("Manage Trusted Reviewers");
        trustedReviewersItem.setOnAction(e -> context.router().navigate(MyPages.TRUSTED_REVIEWER));

        //Add button to add a question
        Button addQuestionButton = UIFactory.createButton("Add", e -> e.onAction(a -> ShowQuestionWindow()));

        //Edit button to edit a question
        Button editQuestionButton = UIFactory.createButton("Edit", e -> e.onAction(a -> {
            Pair<Integer, String> selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                editQuestionWindow(selectedQuestion.getKey());
            }
        }));
        searchInput.setOnKeyReleased(event -> {
            if (searchInput.getText().length() >= 3) {
                try {
                    searchListView.setVisible(true);
                    List<Question> searchedQuestions = questionsRepo.searchQuestions(searchInput.getText());
                    List<Question> unansweredQuestions = new ArrayList<Question>();
                    List<Question> answeredQuestions = new ArrayList<Question>();
                    List<Integer> allUnansweredQuestionIds = new ArrayList<Integer>();
                    List<User> reviewerList = context.users().getAllReviewers();
                    Pair<Integer, String> question;
                    searchListView.getItems().clear();
                    for (Question q : context.questions().getUnansweredQuestions()) {
                        allUnansweredQuestionIds.add(q.getId());
                    }
                    for (Question q : searchedQuestions) {
                        if (allUnansweredQuestionIds.contains(q.getId())) {
                            unansweredQuestions.add(q);
                        } else {
                            answeredQuestions.add(q);
                        }
                    }
                    for (Question q : answeredQuestions) {
                        question = new Pair<Integer, String>(q.getId(), q.getTitle());
                        searchListView.getItems().add(question);
                    }
                    for (Question q : unansweredQuestions) {
                        question = new Pair<Integer, String>(q.getId(), q.getTitle());
                        searchListView.getItems().add(question);
                    }
                    for (User u : reviewerList) {
                        question = new Pair<Integer, String>(u.getId(), u.getUserName());
                        searchListView.getItems().add(question);
                    }


                    searchListView.setPrefHeight(searchListView.getItems().size() * 26);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                searchListView.setVisible(false);
            }
        });

        //Display only the question title or reviewer's username
        searchListView.setCellFactory(lv -> new ListCell<Pair<Integer, String>>() {
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                //List to hold a list of all reviewers id's to cross-check
                List<Integer> reviewerIds = new ArrayList<Integer>();
                //List to hold a list of all question id's to differentiate between users and questions
                List<Integer> questionIds = new ArrayList<Integer>();
                //List to hold a list of all reviewer usernames to accurately assign reviewer or question appended text
                List<String> reviewerUserNames = new ArrayList<String>();
                for (User u : context.users().getAllReviewers()) {
                    reviewerIds.add(u.getId());
                    reviewerUserNames.add(u.getUserName());
                }
                for (Question q : context.questions().getAll()) {
                    questionIds.add(q.getId());
                }

                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    //checks if selected item is a reviewer or a question and appends appropriate text
                    if (reviewerUserNames.contains(item.getValue()) || reviewerIds.contains(item.getKey()) && !questionIds.contains(item.getKey())) {
                        setText(item.getValue() + "(Reviewer)");
                    } else if (questionIds.contains(item.getKey())) {
                        Question q = context.questions().getById(item.getKey());
                        if (!context.questions().hasPinnedAnswer(q.getId())) {
                            setText(item.getValue() + " (Unresolved)");
                        } else {
                            setText(item.getValue());
                        }

                    }
                }
            }
        });

        //Go to answer list on double-click
        searchListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pair<Integer, String> selectedItem = searchListView.getSelectionModel().getSelectedItem();
                List<Integer> reviewerIds = new ArrayList<Integer>();
                List<Integer> questionIds = new ArrayList<Integer>();
                for (User u : context.users().getAllReviewers()) {
                    reviewerIds.add(u.getId());
                }
                for (Question q : context.questions().getAll()) {
                    questionIds.add(q.getId());
                }
                if (selectedItem != null) {
                    if (reviewerIds.contains(selectedItem.getKey())) { //If item is a reviewer, open their profile
                        UserProfileWindow userProfileWindow = new UserProfileWindow();
                        UserProfileWindow.createUserProfileStage(context, context.getSession().getActiveUser().getId(), selectedItem.getKey());
                    } else if (questionIds.contains(selectedItem.getKey())) { //if item is a question, open answer stage
                        currentlySelectedQuestionId = selectedItem.getKey();
                        createAnswerStage(currentlySelectedQuestionId);
                        showAnswerWindow(currentlySelectedQuestionId);
                    }
                }
            }
        });


        //Delete button to delete a question
        Button deleteQuestionButton = UIFactory.createButton("Delete", e -> e.onAction
                (a -> deleteQuestion()));

        //Toggle unresolved questions only button
        Button unresolvedQuestionsButton = UIFactory.createButton("Show Unresolved Only");
        unresolvedQuestionsButton.setOnAction(a -> {
            showingResolvedQuestions = !showingResolvedQuestions;
            loadQuestions();
            if (showingResolvedQuestions) {
                unresolvedQuestionsButton.setText("Show Unresolved Only");
            } else {
                unresolvedQuestionsButton.setText("Show Resolved And Unresolved");
            }
        });

        //Toggle user questions only button
        Button myQuestionsButton = UIFactory.createButton("Show Mine Only");
        myQuestionsButton.setOnAction(a -> {
            showingUserQuestionsOnly = !showingUserQuestionsOnly;
            loadQuestions();
            if (showingUserQuestionsOnly) {
                myQuestionsButton.setText("Show Others");
            } else {
                myQuestionsButton.setText("Show Mine Only");
            }
        });
        Button reviwerProfileButton = UIFactory.createButton("Reviewer Profiles", e -> e.routeToPage(MyPages.REVIEWER_PROFILE, context));

        //Creating log out button
        Button logoutButton = UIFactory.createLogoutButton(context);

        Button dotMenuButton = new Button("⋮");
        dotMenuButton.setStyle("-fx-font-size: 15px; -fx-cursor: hand;");

// Create the menu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem announcementsItem = new MenuItem("Announcements");
        announcementsItem.setOnAction(e -> context.router().navigate(MyPages.ANNOUNCEMENTS));

        MenuItem staffChatItem = new MenuItem("Chat With Staff");
        staffChatItem.setOnAction(e -> {
            UserStaffChatWindow chatWindow = new UserStaffChatWindow();
            chatWindow.createStaffChatStage(context);
        });


        // Button to navigate to StaffNavigationPage
        Button staffNavigationButton = UIFactory.createButton("Navigate To Other Pages", b ->
                b.routeToPage(MyPages.STAFF_NAVIGATION, context)
        );


        //Button Bar above ListView for horizontal orientation
        HBox questionListBar = new HBox(10, addQuestionButton, editQuestionButton, deleteQuestionButton,
                unresolvedQuestionsButton, myQuestionsButton, searchInput, dotMenuButton);

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
                    if (questionsRepo.getUnansweredQuestions().contains(item.getValue())) {
                        setText(item.getValue().concat(" Unresolved")); // Display only the answer content
                    } else
                        setText(item.getValue()); // Display only the answer content
                }
            }
        });

        //menu bar for options
        contextMenu.getItems().addAll(announcementsItem, staffChatItem);
        dotMenuButton.setOnMouseClicked(e -> contextMenu.show(dotMenuButton, e.getScreenX(), e.getScreenY()));

        // HBox for option buttons
        HBox optionBar = new HBox(10, questionDisplayButton);

        // Add the trusted reviewer button only if the user is a student
        if (userCurrentRole == Roles.STUDENT) {
            contextMenu.getItems().add(trustedReviewersItem);
        } else if (userCurrentRole == Roles.STAFF) { // Add staff navigation button only for staff
            optionBar.getChildren().add(staffNavigationButton);
            questionListBar.getChildren().remove(dotMenuButton);
        } else {
            questionListBar.getChildren().remove(dotMenuButton);
        }


        layout.getChildren().addAll(userLabel, questionListBar, searchListView, questionListView, optionBar);

        // If more than one role, add a role selection dropdown and a Go button.
        if (allRoles.length > 1) {
            MenuButton roleMenu = UIFactory.createNavMenu(context, "Select Role");
            optionBar.getChildren().addAll(roleMenu);
        }
        optionBar.getChildren().add(logoutButton);

        return layout;
    }

    /**
     * Initializes the stage and layout used for creating new questions.
     *
     * @param userID The ID of the current user creating the question.
     */
    private void createQuestionStage(int userID) {
        questionStage = new Stage();
        questionStage.initModality(Modality.NONE);

        //UI for question window
        Label questionContent = UIFactory.createLabel("Question", f -> f.style("-fx-font-weight: bold;"));
        Label questionTitle = UIFactory.createLabel("Title", f -> f.style("-fx-font-weight: bold;"));
        Button createButton = UIFactory.createButton("Add Question", e -> e.onAction(a -> addQuestion(userID)));
        Button closeButton = UIFactory.createButton("Close", e -> e.onAction(a -> questionStage.close()));

        // Opening the answer window after a double click is detected on the list item


        VBox questionLayout = new VBox(10, questionTitle, questionTitleInput, resultView, questionContent, questionInput, createButton, closeButton);
        questionStage.setScene(new Scene(questionLayout, 300, 400));
    }

    /**
     * Loads and displays questions in the ListView based on current filters.
     */
    private void loadQuestions() {
        questionListView.getItems().clear();
        List<Question> questionList;
        // Use Questions class
        if (showingResolvedQuestions) {
            questionList = context.questions().getAll();
        } else {
            questionList = context.questions().getQuestionsWithoutPinnedAnswer();
        }

        if (showingUserQuestionsOnly) {
            // remove questions not belonging to the user
            questionList.removeIf(q -> context.getSession().getActiveUser().getId() != q.getMessage().getUserId());
        }

        for (Question q : questionList) {
            int numAnswers = answersRepo.getRepliesToQuestion(q.getId()).size();
            String title = q.getTitle();
            String r = "Reply";
            if (numAnswers != 1) {
                r = "Replies";
            }
            title += " [" + numAnswers + "] " + r;
            if (context.questions().hasPinnedAnswer(q.getId())) {
                title += " ✔";
            }
            questionListView.getItems().add(new Pair<>(q.getId(), title));
        }
    }


    /**
     * Adds a new question to the database and refreshes the question list.
     *
     * @param userID The ID of the user creating the question.
     */
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
        loadQuestions();
    }

    /**
     * Deletes the currently selected question from the database.
     */
    private void deleteQuestion() {
        Pair<Integer, String> selectedItem = questionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int questionId = selectedItem.getKey();
            Question question = context.questions().getById(questionId);
            User questionAuthor = context.users().getById(question.getMessage().getUserId());
            boolean checkIfStaff = RolesUtil.hasAnyRole(RolesUtil.intToRoles(context.getSession().getActiveUser().getRoles()), new Roles[]{Roles.STAFF, Roles.ADMIN, Roles.INSTRUCTOR});
            System.out.println(("Staff Check: " + checkIfStaff));

            if (questionAuthor.getId() != context.getSession().getActiveUser().getId() && !checkIfStaff) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You cannot delete this question.");
                alert.showAndWait();
                return;
            }

            context.questions().delete(selectedItem.getKey());// Use Questions class
            questionListView.getItems().remove(selectedItem);
        }
    }

    /**
     * Opens a window for editing the selected question's title and content.
     *
     * @param questionId The ID of the question to edit.
     */
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
            String newTitle = editQuestionTitleField.getText();
            context.questions().updateQuestionFields(questionId, newTitle, newContent);
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

    /**
     * Displays the window for creating a new question.
     */
    private void ShowQuestionWindow() {
        questionStage.setTitle("Create Question");
        questionStage.show();
    }


//------------------------------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------//
//Answer stage and methods

    /**
     * Initializes and sets up the UI components for the answerStage, which
     * includes displaying the question, showing a list of answers, and
     * providing controls to add, edit, delete, or mark answers as a solution.
     *
     * @param questionId The ID of the question for which answers are to be managed.
     */
    private void createAnswerStage(int questionId) {
        answerStage = new Stage();
        answerStage.initModality(Modality.NONE);

        Question queContent;
        if (context.questions().getById(questionId) != null) {
            queContent = context.questions().getById(questionId);
        } else {
            queContent = null;
        }

        //Question for the answers/ Labels
        assert queContent != null;
        Label questionContent = UIFactory.createLabel("Question: " + queContent.getMessage().getContent(),
                f -> f.style("-fx-font-weight: bold; -fx-font-size: 13pt;"));
        Label answerLabelList = UIFactory.createLabel("Answers:", f -> f.style("-fx-font-weight: bold;"));

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

        //Mark and Unmark Solution Button
        Button markAnswerButton = UIFactory.createButton("Mark Answer As Solution");
        if (context.questions().hasPinnedAnswer(questionId)) {
            markAnswerButton.setText("Unmark Answer As Solution");
        }

        markAnswerButton.setOnAction(a -> {
            Pair<Integer, String> selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            if (currentlySelectedQuestionResolved) {
                //unmark the solution
                for (Pair<Integer, String> answer : answerListView.getItems()) {
                    if (context.answers().getById(answer.getKey()).getIsPinned()) {
                        context.answers().togglePin(answer.getKey());
                        break;
                    }
                }
                //update answerListView, questionListView, and button text
                loadAnswers(questionId);
                loadQuestions();
                markAnswerButton.setText("Mark Answer As Solution");
            } else if (selectedAnswer != null) {
                context.answers().togglePin(selectedAnswer.getKey());
                //update answerListView, questionListView, and button text
                loadAnswers(questionId);
                loadQuestions();
                markAnswerButton.setText("Unmark Answer As Solution");
            }
        });

        //Adding answer UI
        Button addPMButton = UIFactory.createButton("Private Message", e -> e.onAction(
                a -> {
                    PrivateMessagePage.setTargetQuestion(queContent);
                    context.router().navigate(MyPages.PRIVATE_MESSAGE);
                }));
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
        answerListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pair<Integer, String> selectedItem = answerListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    final int currentlySelectedAnswerId = selectedItem.getKey();
                    ReplyList.setAnswer(context.answers().getById(currentlySelectedAnswerId));
                    context.router().navigate(MyPages.REPLY_LIST);
                }
            }
        });

        HBox addUI = new HBox(10, addAnswerButton, answerInput);
        HBox editUI = new HBox(10, answerLabelList, editAnswerButton, deleteAnswerButton, addPMButton);
        if (context.getSession().getActiveUser().getId() == queContent.getMessage().getUserId()) {
            editUI.getChildren().add(markAnswerButton);
        }

        //Layout to show every UI
        VBox answerLayout = new VBox(questionContent, addUI, editUI, answerListView, closeButton);
        answerStage.setScene(new Scene(answerLayout, 600, 500));
    }


    /**
     * Loads all answers associated with the given question ID from the database
     * and populates the answerListView. If an answer is marked as a solution,
     * it is displayed with a checkmark and sets the resolution flag.
     *
     * @param questionID The ID of the question of whose answers should be loaded.
     */
    private void loadAnswers(int questionID) {
        answerListView.getItems().clear();
        List<Answer> answerList = context.answers().getRepliesToQuestion(questionID);
        currentlySelectedQuestionResolved = false;
        for (Answer a : answerList) {
            if (a.getIsPinned()) {
                currentlySelectedQuestionResolved = true;
                answerListView.getItems().addFirst(new Pair<>(a.getId(), a.getMessage().getContent() + " ✔"));
            } else {
                answerListView.getItems().addLast(new Pair<>(a.getId(), a.getMessage().getContent()));
            }
        }
    }

    /**
     * Submits a new answer for the currently selected question. The input
     * content is validated and added to the system. The answer list is updated
     * after successful submission.
     */
    private void addAnswer() {
        if (currentlySelectedQuestionId == -1) return;
        String content = answerInput.getText().trim();
        if (!content.isEmpty()) {
            Message message = new Message(context.getSession().getActiveUser().getId(), content);
            Answer newAnswer = new Answer(message, currentlySelectedQuestionId, null, false);

            Answer createdAnswer = null;
            try {
                createdAnswer = context.answers().create(newAnswer);
                loadQuestions();
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
            answerListView.getItems().add(new Pair<>(createdAnswer.getId(), createdAnswer.getMessage().getContent()));
            answerInput.clear();

        }
    }

    /**
     * Deletes the selected answer from the database and removes it from
     * the ListView. If the deleted answer was marked as a solution, the
     * resolved status is reset and the questions are reloaded.
     */
    private void deleteAnswer() {
        Pair<Integer, String> selectedItem = answerListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int answerId = selectedItem.getKey();
            Answer answer = context.answers().getById(answerId);
            boolean checkIfStaff = RolesUtil.hasAnyRole(RolesUtil.intToRoles(context.getSession().getActiveUser().getRoles()), new Roles[]{Roles.STAFF, Roles.ADMIN, Roles.INSTRUCTOR});
            boolean isPinned = answer.getIsPinned();
            User answerAuthor = context.users().getById(answer.getMessage().getUserId());
            if (answerAuthor.getId() != context.getSession().getActiveUser().getId() && !checkIfStaff) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You cannot delete this answer.");
                alert.showAndWait();
                return;
            } else {
                context.answers().delete(answerId); // Use the Answers instance to delete
            }
            //if answer is pinned, loadQuestions to update questionViewList
            if (isPinned) {
                currentlySelectedQuestionResolved = false;
                loadQuestions();
            }
            answerListView.getItems().remove(selectedItem); // Remove the item from the ListView
        }
        loadQuestions();
    }

    /**
     * Opens a modal window allowing the user to edit the content of an existing answer.
     * Once edited, the answer is updated in the database and the list is refreshed.
     *
     * @param answerId The ID of the answer to be edited.
     */
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

    /**
     * Sets the answerStage title to the question's title, loads all answers related
     * to the question, and displays the stage.
     *
     * @param questionID The ID of the question of whose answers should be displayed.
     */
    private void showAnswerWindow(int questionID) {
        answerStage.setTitle(context.questions().getById(questionID).getTitle());
        loadAnswers(questionID);
        answerStage.show();
    }

}