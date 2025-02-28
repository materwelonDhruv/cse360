package application.pages;

import application.framework.*;
import database.model.entities.PrivateMessage;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

@Route(MyPages.PRIVATE_CONVERSATION)
@View(title = "Private Conversation Page")
public class PrivateMessageConvoPage extends BasePage {
    private static PrivateMessage privateMessage;

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
        User user = context.getSession().getActiveUser();
        int pmSenderID = privateMessage.getMessage().getId();
        User pmSender = context.users().getById(pmSenderID);
        TextField privateMessageInput = UIFactory.createTextField("Enter reply to message", f ->
                f.minWidth(200).maxWidth(600).minChars(10).maxChars(MAX_LENGTH));
        BorderPane view = new BorderPane();
        view.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        //Middle text display
        TextArea textArea = new TextArea();
        String totalConversation = "";
        totalConversation += "(" + pmSender.getUserName() + "): " + privateMessage.getMessage().getContent();
        textArea.setText(totalConversation);
        view.setCenter(textArea);

        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_QUESTION_DISPLAY, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        toolbar.getChildren().addAll(backButton, logoutButton);
        view.setBottom(toolbar);


        return view;
    }
}
