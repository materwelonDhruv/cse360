package application.pages;

import application.framework.*;
import database.model.entities.Message;
import database.model.entities.PrivateMessage;
import database.model.entities.Question;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Represents the page where users can send a private message in response to a specific question.
 * This page provides a text field to input the message and a button to send it.
 * After sending, the message is stored and the user is redirected back to their home page.
 */
@Route(MyPages.PRIVATE_MESSAGE)
@View(title = "Private Message Page")
public class PrivateMessagePage extends BasePage {
    private static Question question;

    /**
     * Default constructor for the PrivateMessagePage.
     */
    public PrivateMessagePage() {
        super();
    }

    /**
     * Sets the question that the private message will be in response to.
     *
     * @param q The target question.
     */
    public static void setTargetQuestion(Question q) {
        question = q;
    }

    /**
     * Creates and returns the UI view for the private message page.
     * It includes a text field for entering a message, a send button,
     * and a bottom toolbar with navigation options.
     *
     * @return The fully constructed JavaFX Pane for this view.
     */
    public Pane createView() {
        int MAX_LENGTH = 300;
        //Declare fields
        User user = context.getSession().getActiveUser();
        TextField privateMessageInput = UIFactory.createTextField("Enter private message", f ->
                f.minWidth(200).maxWidth(600).minChars(10).maxChars(MAX_LENGTH));
        BorderPane view = new BorderPane();
        view.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Bottom toolbar with Back and Logout buttons using UIFactory
        HBox toolbar = new HBox(10);
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_HOME, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        toolbar.getChildren().addAll(backButton, logoutButton);
        view.setBottom(toolbar);

        //Middle section with Text field
        VBox centerItems = new VBox(10);
        Button privateMessageButton = UIFactory.createButton("Send Private Message", e -> e.routeToPage(MyPages.USER_HOME, context));
        //create private message
        privateMessageButton.onActionProperty().set(e -> {
            context.router().navigate(MyPages.PRIVATE_MESSAGE);
            Message message = new Message(user.getId(), privateMessageInput.getText().trim());
            PrivateMessage privateMessage = new PrivateMessage(message, question.getId(), null);
            privateMessage.setMessage(message);
            context.privateMessages().create(privateMessage);
        });
        centerItems.getChildren().addAll(privateMessageInput, privateMessageButton);
        view.setCenter(centerItems);
        return view;
    }
}
