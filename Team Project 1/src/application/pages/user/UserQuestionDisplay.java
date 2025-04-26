package application.pages.user;

import application.framework.*;
import application.pages.PrivateMessageConvoPage;
import database.model.entities.PrivateMessage;
import database.model.entities.Question;
import database.model.entities.ReviewerRequest;
import database.model.entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.Helpers;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> UserQuestionDisplay page shows the current user's questions
 * (and reserved space for answers). Double-clicking a question is intended
 * to load its detail page.</p>
 *
 * @author Mike
 */

@Route(MyPages.USER_QUESTION_DISPLAY)
@View(title = "User Questions")
public class UserQuestionDisplay extends BasePage {

    private User user;

    public UserQuestionDisplay() {
        super();
    }

    /**
     * @return container
     * Creates the view to display a user's questions, private messages, and a reviewer request tool
     */
    @Override
    public Pane createView() {
        // Main container with consistent styling from DesignGuide
        VBox container = new VBox(15);
        container.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Create a split pane: left for Questions, right for Private Messages (future use)
        SplitPane splitPane = new SplitPane();
        user = context.getSession().getActiveUser();

        // Create table for user's questions
        TableView<Question> questionTable = questionTableSetup();
        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox questionTableVBox = new VBox(10);
        Label questionLabel = new Label("Your Questions");
        questionLabel.setStyle("-fx-font-weight: bold;");
        questionTableVBox.getChildren().addAll(questionLabel, questionTable);

        //Private message table declaration
        TableView<PrivateMessage> privateMessageTable = privateMessageTableSetup();
        privateMessageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox privateMessageVBox = new VBox(10);
        Label privateMessageLabel = new Label("Received PrivateMessages");
        privateMessageLabel.setStyle("-fx-font-weight: bold;");
        privateMessageVBox.getChildren().addAll(privateMessageLabel, privateMessageTable);

        //Set up SentPMTable
        TableView<PrivateMessage> sentPrivateMessageTable = sentPrivateMessageTableSetup();
        sentPrivateMessageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox sentPrivateMessageVBox = new VBox(10);
        Label sentPrivateMessageLabel = new Label("Sent PrivateMessages");
        sentPrivateMessageLabel.setStyle("-fx-font-weight: bold;");
        sentPrivateMessageVBox.getChildren().addAll(sentPrivateMessageLabel, sentPrivateMessageTable);
        // Add tables to the split pane
        SplitPane sp = new SplitPane();
        sp.getItems().addAll(privateMessageVBox, sentPrivateMessageVBox);
        splitPane.getItems().addAll(questionTableVBox, sp);

        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Roles role = context.getSession().getCurrentRole();
        Button homepageBttn = UIFactory.createHomepageButton(context);
        Button logoutButton = UIFactory.createLogoutButton(context);
        // Add reviewer request button
        Button requestReviewerButton = UIFactory.createButton("Request Reviewer Status", e -> e.onAction(a -> {
            User currentUser = context.getSession().getActiveUser();

            // Check if the user already has a pending request
            List<ReviewerRequest> userRequests = context.reviewerRequests().getRequestsByUser(currentUser.getId());
            boolean hasPendingRequest = userRequests.stream()
                    .anyMatch(request -> request.getStatus() == null || !request.getStatus());

            if (hasPendingRequest) {
                UIFactory.showAlert(Alert.AlertType.WARNING, "Request Pending",
                        "You already have a pending reviewer status request.");
                return;
            }

            // Check if user already has reviewer role
            if (RolesUtil.hasRole(currentUser.getRoles(), Roles.REVIEWER)) {
                UIFactory.showAlert(Alert.AlertType.INFORMATION, "Already a Reviewer",
                        "You already have reviewer status.");
                return;
            }

            // Find available instructors
            List<User> all_users = context.users().getAll();
            List<User> instructors = new ArrayList<>(); // Fix: Initialize as ArrayList
            for (User usr : all_users) {
                Roles[] all_usr_roles = RolesUtil.intToRoles(usr.getRoles());
                if (RolesUtil.hasRole(all_usr_roles, Roles.INSTRUCTOR)) {
                    instructors.add(usr);
                }
            }

            if (instructors.isEmpty()) {
                UIFactory.showAlert(Alert.AlertType.ERROR, "No Instructors",
                        "No instructors available to process your request.");
                return;
            }

            // Create a dialog to select an instructor
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Select Instructor");
            dialog.setHeaderText("Please select an instructor to review your request:");

            // Set button types
            ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

            // Create a list view with all instructors
            ListView<User> listView = new ListView<>();
            listView.getItems().addAll(instructors);

            // Display instructor username in the list
            listView.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getUserName());
                    }
                }
            });

            // Set the list view as the dialog content
            dialog.getDialogPane().setContent(listView);

            // Disable the select button until a selection is made
            Button selectButton = (Button) dialog.getDialogPane().lookupButton(selectButtonType);
            selectButton.setDisable(true);

            // Enable select button when an instructor is selected
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                selectButton.setDisable(newValue == null);
            });

            // Convert the result when select button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == selectButtonType) {
                    return listView.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            // Show dialog and process result
            dialog.showAndWait().ifPresent(instructor -> {
                // Create a new reviewer request with the selected instructor
                ReviewerRequest request = new ReviewerRequest();
                request.setRequester(currentUser);
                request.setInstructor(instructor);
                request.setStatus(null); // null means pending

                // Save the request
                ReviewerRequest savedRequest = context.reviewerRequests().create(request);

                if (savedRequest != null && savedRequest.getId() > 0) {
                    UIFactory.showAlert(Alert.AlertType.INFORMATION, "Request Sent",
                            "Your reviewer status request has been sent to " + instructor.getUserName() + ".");
                } else {
                    UIFactory.showAlert(Alert.AlertType.ERROR, "Request Failed",
                            "Failed to send reviewer request. Please try again later.");
                }
            });
        }));
        User currentUser = context.getSession().getActiveUser();
        if (RolesUtil.hasRole(currentUser.getRoles(), Roles.REVIEWER)) {
            toolbar.getChildren().addAll(homepageBttn, logoutButton);
        } else {
            toolbar.getChildren().addAll(homepageBttn, requestReviewerButton, logoutButton);
        }


        container.getChildren().addAll(splitPane, toolbar);
        return container;
    }

    private TableView<PrivateMessage> sentPrivateMessageTableSetup() {
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
        return sentPrivateMessageTable;
    }

    private TableView<PrivateMessage> privateMessageTableSetup() {
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
        return privateMessageTable;
    }

    private TableView<Question> questionTableSetup() {
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
            return new SimpleStringProperty(Helpers.formatTimestamp(q.getMessage().getCreatedAt()));
        });

        questionTable.getColumns().addAll(idCol, titleCol, timeCol);
        return questionTable;
    }
}