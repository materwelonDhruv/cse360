package application.pages;

import application.framework.*;
import database.model.entities.Answer;
import database.model.entities.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

@Route(MyPages.REPLY_LIST)
@View(title = "Reply List")
public class ReplyList extends BasePage {
    private static Answer parent;

    /**
     * @param p Sets the root answer to passed parameter
     */
    public static void setAnswer(Answer p) {
        parent = p;
    }

    /**
     * @return layout
     * Builds the layout for ReplyList
     */
    @Override
    public Pane createView() {
        //Setup layout
        BorderPane layout = new BorderPane();
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);
        HBox bottomBar = new HBox(10);
        HBox topBar = new HBox(10);
        //Setup bottom toolbar
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.REVIEW_HOME, context));
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));
        bottomBar.getChildren().addAll(backButton, logoutButton);
        layout.setBottom(bottomBar);
        //Setup ListView of replies
        ListView<Answer> replyList = new ListView<>();
        ObservableList<Answer> replies = FXCollections.observableArrayList(context.answers().getRepliesToAnswer(parent.getId()));
        replyList.setItems(replies);
        layout.setCenter(replyList);
        //Setup top toolbar
        TextField replyInput = UIFactory.createTextField("Reply:", f -> f.minWidth(200).maxWidth(600).minChars(10).maxChars(2000));
        Button addReply = replyAddButtonSetup(replyList, replyInput);
        Button editReply = replyEditButtonSetup();
        Button deleteReply = replyDeleteButtonSetup(replyList.getSelectionModel().getSelectedItem());
        topBar.getChildren().addAll(replyInput, addReply, editReply, deleteReply);
        layout.setTop(topBar);
        return layout;
    }

    /**
     * @param replyTable The ListView object containing all replies
     * @param replyInput The reply text field
     * @return addReplyButton
     * Private method to build the "add reply" button
     */
    private Button replyAddButtonSetup(ListView<Answer> replyTable, TextField replyInput) {
        Button addReplyButton = UIFactory.createButton("Add Reply",
                e -> e.onAction(
                        a -> {
                            String replyContent = replyInput.getCharacters().toString();
                            Message tempMessage = new Message(parent.getMessage().getUserId(), replyContent);
                            Answer newAnswer = new Answer(tempMessage, null, parent.getId(), false);
                            context.answers().create(newAnswer);
                        }
                )
        );
        return addReplyButton;
    }

    /**
     * @return editReplyButton
     */
    private Button replyEditButtonSetup() {
        Button editReplyButton = UIFactory.createButton("Edit Reply",
                e -> e.onAction(
                        a -> {
                            //TODO: implement edit
                        }
                )
        );
        return editReplyButton;
    }

    private Button replyDeleteButtonSetup(Answer selectedAnswer) {
        Button deleteReplyButton = UIFactory.createButton("Delete Reply",
                e -> e.onAction(
                        a -> {
                            context.answers().delete(selectedAnswer.getId());
                        }
                )
        );
        return deleteReplyButton;
    }
}
