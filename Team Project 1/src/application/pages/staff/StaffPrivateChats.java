package application.pages.staff;

import application.framework.*;
import database.model.entities.Message;
import database.model.entities.StaffMessage;
import database.model.entities.User;
import database.repository.repos.StaffMessages;
import database.repository.repos.Users;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

/**
 * The StaffPrivateChats page provides a split interface for staff members to manage
 * private chats with users. On the left, a search box and a list of unique chat partners;
 * on the right, a scrollable chat view and a message input field.
 * <p>
 * This page is accessed from the StaffHomePage and allows staff to:
 * <ul>
 *     <li>Search for a user to start or continue a conversation</li>
 *     <li>View existing chats in chronological order</li>
 *     <li>Send new messages to the selected user</li>
 * </ul>
 * <p>
 * Messages sent by the staff member appear aligned on the right,
 * and messages from the user appear on the left.
 * </p>
 *
 * @author Dhruv
 * @see StaffMessages
 * @see Users
 */
@Route(MyPages.STAFF_PRIVATE_CHATS)
@View(title = "Staff Private Chats")
public class StaffPrivateChats extends BasePage {

    private ListView<User> userListView;
    private TextField searchField;
    private TextArea messageInputArea;
    private VBox chatDisplayBox;
    private ScrollPane chatDisplayScrollPane;

    private final StaffMessages staffMessagesRepo;
    private final Users usersRepo;

    private int staffId;         // The logged-in staff's userId
    private User selectedUser;   // The user currently selected in the chat list

    /**
     * Constructs the StaffPrivateChats page and initializes the repositories.
     */
    public StaffPrivateChats() {
        super();
        staffMessagesRepo = context.staffMessages();
        usersRepo = context.users();
    }

    /**
     * Creates and returns the UI layout for the staff private chats page.
     *
     * @return A Pane containing the layout for searching users, displaying chat history,
     * and sending new messages.
     */
    @Override
    public Pane createView() {
        // Retrieve the active staff user
        User staffUser = context.getSession().getActiveUser();
        if (staffUser == null) {
            return new VBox(new Label("No active staff user found."));
        }
        staffId = staffUser.getId();

        // Main container split into left (user list) and right (chat display)
        HBox mainContainer = new HBox(10);
        mainContainer.setStyle(DesignGuide.MAIN_PADDING);

        // Left side: search box + user list
        VBox leftPane = new VBox(10);
        leftPane.setPrefWidth(250);

        Label searchLabel = UIFactory.createLabel("Search User");
        searchField = UIFactory.createTextField("Search by username", t ->
                t.onAction(e -> performSearch())); // triggers a search on ENTER

        userListView = new ListView<>();
        userListView.setPlaceholder(new Label("No users found"));
        configureUserListView(); // Custom method to set cell factory + selection listener

        // Load unique chats on initial display
        loadUniqueChats();

        // "Back" button to return to the StaffHomePage
        Button backButton = UIFactory.createBackButton(context);

        leftPane.getChildren().addAll(searchLabel, searchField, userListView, backButton);

        // Right side: chat display area
        VBox rightPane = new VBox(10);
        rightPane.setPrefWidth(600);

        // Chat scroll pane
        chatDisplayScrollPane = new ScrollPane();
        chatDisplayScrollPane.setFitToWidth(true);
        chatDisplayBox = new VBox(8);
        chatDisplayBox.setFillWidth(true);
        chatDisplayBox.setAlignment(Pos.TOP_LEFT);
        chatDisplayScrollPane.setContent(chatDisplayBox);

        // Make the chat area expand vertically
        VBox.setVgrow(chatDisplayScrollPane, Priority.ALWAYS);

        // Input area at the bottom
        messageInputArea = new TextArea();
        messageInputArea.setPromptText("Type your message...");
        messageInputArea.setWrapText(true);
        messageInputArea.setPrefRowCount(3);
        messageInputArea.setPrefHeight(60);

        // "Send" button
        Button sendButton = UIFactory.createButton("↪", b ->
                b.onAction(e -> sendMessageToUser()));

        HBox inputBox = new HBox(8, messageInputArea, sendButton);

        rightPane.getChildren().addAll(chatDisplayScrollPane, inputBox);

        // Combine left and right panes
        mainContainer.getChildren().addAll(leftPane, rightPane);

        return mainContainer;
    }

    /**
     * Configures the user list to display only the username, and loads
     * chat automatically when a user is selected.
     */
    private void configureUserListView() {
        // Show the username instead of the default toString()
        userListView.setCellFactory(lv -> new ListCell<>() {
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

        // Automatically load chat on single-click selection
        userListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedUser = newVal;
                        loadChat(newVal.getId());
                    }
                }
        );
    }

    /**
     * Loads the unique users that the staff member has chatted with and displays them in the user list.
     */
    private void loadUniqueChats() {
        userListView.getItems().clear();
        List<User> chatPartners = staffMessagesRepo.getUniqueChats(staffId);
        userListView.getItems().addAll(chatPartners);
    }

    /**
     * Performs a search for users based on the text in the searchField,
     * then populates the user list with matching results.
     */
    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        userListView.getItems().clear();

        if (query.isEmpty()) {
            loadUniqueChats();
            return;
        }
        // Get all users, filter by partial match in username
        List<User> allUsers = usersRepo.getAll();
        for (User u : allUsers) {
            if (u.getUserName().toLowerCase().contains(query) && u.getId() != staffId) {
                userListView.getItems().add(u);
            }
        }
    }

    /**
     * Loads the chat history with the specified user, displaying messages in chronological order.
     *
     * @param userId The ID of the user to load the chat with.
     */
    private void loadChat(int userId) {
        chatDisplayBox.getChildren().clear();
        List<StaffMessage> chatMessages = staffMessagesRepo.loadChat(userId, staffId);

        for (StaffMessage sm : chatMessages) {
            addMessageToChat(sm);
        }

        // Auto-scroll to the bottom of the chat
        chatDisplayBox.layout();
        chatDisplayScrollPane.setVvalue(1.0);
    }

    /**
     * Sends a new message (typed in the messageInputArea) to the currently selected user.
     */
    private void sendMessageToUser() {
        if (selectedUser == null) {
            return;
        }
        String content = messageInputArea.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        // Build the underlying message
        Message msg = new Message(staffId, content);

        // Actually send it
        StaffMessage sm = staffMessagesRepo.sendMessage(msg, selectedUser.getId(), staffId);

        // Clear input, display the new message, and scroll down
        messageInputArea.clear();
        addMessageToChat(sm);
        chatDisplayBox.layout();
        chatDisplayScrollPane.setVvalue(1.0);
    }

    /**
     * Adds a single message to the chat display, aligning staff vs user messages differently.
     *
     * @param sm The StaffMessage to display.
     */
    private void addMessageToChat(StaffMessage sm) {
        HBox messageContainer = new HBox(5);

        // If the staff is the sender, align right. Otherwise, align left.
        boolean staffIsSender = (sm.getMessage().getUserId() == staffId);

        Label nameLabel = new Label(staffIsSender
                ? " : Me"
                : sm.getUser().getUserName() + ": ");

        TextFlow textFlow = new TextFlow(new Text(sm.getMessage().getContent()));

        if (staffIsSender) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.getChildren().addAll(textFlow, nameLabel);
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageContainer.getChildren().addAll(nameLabel, textFlow);
        }

        chatDisplayBox.getChildren().add(messageContainer);
    }
}