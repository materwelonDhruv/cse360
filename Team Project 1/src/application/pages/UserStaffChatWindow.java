package application.pages;

import application.AppContext;
import database.model.entities.Message;
import database.model.entities.StaffMessage;
import database.model.entities.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

/**
 * The UserStaffChatWindow class represents a pop-up window for normal users to chat with staff.
 * <p>
 * The window displays a left panel with a search box and a list of staff members, and a right panel
 * with a scrollable chat view and a message input field. Messages sent by the user are aligned on the right,
 * and staff messages are aligned on the left.
 * </p>
 *
 * @author Dhruv
 */
public class UserStaffChatWindow {

    private ListView<User> staffListView;
    private TextField searchField;
    private VBox chatDisplayBox;
    private ScrollPane chatDisplayScrollPane;
    private TextArea inputArea;
    private AppContext context;
    private int currentUserId;
    private User selectedStaff;

    /**
     * Initializes and displays the chat window for normal users to communicate with staff.
     *
     * @param context Application context.
     */
    public void createStaffChatStage(AppContext context) {
        this.context = context;
        this.currentUserId = context.getSession().getActiveUser().getId();
        Stage stage = new Stage();
        stage.setTitle("Chat with Staff");

        // Main container with a split layout
        HBox mainContainer = new HBox(10);
        mainContainer.setPadding(new Insets(10));

        // Left panel: staff search and list
        VBox leftPane = new VBox(10);
        leftPane.setPrefWidth(250);

        Label searchLabel = new Label("Search Staff");
        searchField = new TextField();
        searchField.setPromptText("Enter staff name...");
        searchField.setOnAction(e -> performSearch());

        staffListView = new ListView<>();
        staffListView.setPlaceholder(new Label("No staff found"));
        staffListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getUserName());
            }
        });
        staffListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedStaff = newVal;
                loadChat(selectedStaff.getId());
            }
        });

        leftPane.getChildren().addAll(searchLabel, searchField, staffListView);
        loadAllStaff();

        // Right panel: chat display area and message input
        VBox rightPane = new VBox(10);
        rightPane.setPrefWidth(600);

        chatDisplayScrollPane = new ScrollPane();
        chatDisplayScrollPane.setFitToWidth(true);
        chatDisplayBox = new VBox(8);
        chatDisplayBox.setFillWidth(true);
        chatDisplayBox.setAlignment(Pos.TOP_LEFT);
        chatDisplayScrollPane.setContent(chatDisplayBox);
        VBox.setVgrow(chatDisplayScrollPane, Priority.ALWAYS);

        inputArea = new TextArea();
        inputArea.setPromptText("Type your message...");
        inputArea.setWrapText(true);
        inputArea.setPrefRowCount(3);
        inputArea.setPrefHeight(60);

        Button sendButton = new Button("↪");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(8, inputArea, sendButton);
        rightPane.getChildren().addAll(chatDisplayScrollPane, inputBox);

        mainContainer.getChildren().addAll(leftPane, rightPane);

        Scene scene = new Scene(mainContainer, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Performs a search for staff based on the text in the searchField and populates the staff list.
     */
    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        staffListView.getItems().clear();
        if (query.isEmpty()) {
            loadAllStaff();
            return;
        }
        List<User> allUsers = context.users().getAll();
        for (User u : allUsers) {
            if (RolesUtil.hasRole(u.getRoles(), Roles.STAFF) && u.getUserName().toLowerCase().contains(query)) {
                staffListView.getItems().add(u);
            }
        }
    }

    /**
     * Loads all staff members into the staff list view.
     */
    private void loadAllStaff() {
        staffListView.getItems().clear();
        List<User> allUsers = context.users().getAll();
        for (User u : allUsers) {
            if (RolesUtil.hasRole(u.getRoles(), Roles.STAFF)) {
                staffListView.getItems().add(u);
            }
        }
    }

    /**
     * Loads the chat history with the specified staff member.
     *
     * @param staffId Identifier of the staff member.
     */
    private void loadChat(int staffId) {
        chatDisplayBox.getChildren().clear();
        List<StaffMessage> chatMessages = context.staffMessages().loadChat(currentUserId, staffId);
        for (StaffMessage sm : chatMessages) {
            addMessageToChat(sm);
        }
        chatDisplayBox.layout();
        chatDisplayScrollPane.setVvalue(1.0);
    }

    /**
     * Sends a new message from the current user to the selected staff member.
     */
    private void sendMessage() {
        if (selectedStaff == null) {
            return;
        }
        String content = inputArea.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        Message msg = new Message(currentUserId, content);
        context.staffMessages().sendMessage(msg, currentUserId, selectedStaff.getId());
        inputArea.clear();
        loadChat(selectedStaff.getId());
    }

    /**
     * Adds a single message to the chat display.
     *
     * @param sm StaffMessage object to add.
     */
    private void addMessageToChat(StaffMessage sm) {
        HBox messageContainer = new HBox(5);
        boolean userIsSender = (sm.getMessage().getUserId() == currentUserId);
        Label nameLabel = new Label(userIsSender ? " : Me " : sm.getStaff().getUserName() + ": ");
        TextFlow textFlow = new TextFlow(new Text(sm.getMessage().getContent()));
        if (userIsSender) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.getChildren().addAll(textFlow, nameLabel);
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageContainer.getChildren().addAll(nameLabel, textFlow);
        }
        chatDisplayBox.getChildren().add(messageContainer);
    }
}