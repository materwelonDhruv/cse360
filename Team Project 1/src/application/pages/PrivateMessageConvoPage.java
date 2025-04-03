package application.pages;

import application.framework.*;
import database.model.entities.Message;
import database.model.entities.PrivateMessage;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@Route(MyPages.PRIVATE_CONVERSATION)
@View(title = "Private Conversation Page")
public class PrivateMessageConvoPage extends BasePage {
    private static PrivateMessage privateMessage;
    private static User user;

    public PrivateMessageConvoPage() {
        super();
    }

    public static void setTargetPM(PrivateMessage pm) {
        privateMessage = pm;
    }

    @Override
    public Pane createView() {
        int MAX_LENGTH = 300;
        //Declare fields
        user = context.getSession().getActiveUser();
        TextField privateMessageInput = UIFactory.createTextField("Enter reply to message", f ->
                f.minWidth(200).maxWidth(600).minChars(10).maxChars(MAX_LENGTH));
        BorderPane view = new BorderPane();
        view.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        //Middle text display
        VBox replyBox = new VBox(10);
        HBox messageBox = new HBox(10);
        TextArea textArea = new TextArea();
        String totalConversation = getConversation(privateMessage);
        textArea.setText(totalConversation);
        view.setCenter(textArea);

        //Setup Button for sending message
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            sendReply(privateMessageInput, getMostRecentReply());
            textArea.setText(getConversation(privateMessage));
        });

        //Arrange Elements
        messageBox.getChildren().addAll(privateMessageInput, sendButton);
        replyBox.getChildren().addAll(textArea, messageBox);
        view.setCenter(replyBox);

        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_QUESTION_DISPLAY, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        toolbar.getChildren().addAll(backButton, logoutButton);
        view.setBottom(toolbar);


        return view;
    }

    private String getConversation(PrivateMessage pm) {
        StringBuilder conversation = new StringBuilder();
        User pmSender;
        while (pm != null) {
            int pmSenderID = pm.getMessage().getUserId();
            pmSender = context.users().getById(pmSenderID);
            conversation.append("(").append(pmSender.getUserName()).append("): ").append(pm.getMessage().getContent());
            conversation.append("\n");
            if (!context.privateMessages().getRepliesToPrivateMessage(pm.getId()).isEmpty()) {
                System.out.println("Private message has replies: " + context.privateMessages().getRepliesToPrivateMessage(pm.getId()));
                pm = context.privateMessages().getRepliesToPrivateMessage(pm.getId()).getFirst();
            } else {
                break;
            }
        }
        //System.out.println("conversation: " + conversation);
        return conversation.toString();
    }

    private void sendReply(TextField replyText, PrivateMessage parent) {
        Message tempMessage = new Message(user.getId(), replyText.getText());
        System.out.println("Parent: " + parent.getMessage().getContent());
        PrivateMessage pm = new PrivateMessage(tempMessage, null, parent.getId());
        context.privateMessages().create(pm);
    }

    private PrivateMessage getMostRecentReply() {
        PrivateMessage pm = privateMessage;
        while (pm != null) {
            if (!context.privateMessages().getRepliesToPrivateMessage(pm.getId()).isEmpty()) {
                pm = context.privateMessages().getRepliesToPrivateMessage(pm.getId()).getFirst();
            } else {
                break;
            }
        }
        return pm;
    }
}
