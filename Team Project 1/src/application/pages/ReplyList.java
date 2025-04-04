package application.pages;

import application.framework.*;
import database.model.entities.Answer;
import database.model.entities.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import utils.permissions.Roles;

import java.util.List;

@Route(MyPages.REPLY_LIST)
@View(title = "Reply List")
public class ReplyList extends BasePage {
    private static Answer root;
    private ObservableList<Answer> replies;

    /**
     * @param p Sets the root answer to passed parameter
     */
    public static void setAnswer(Answer p) {
        root = p;
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
        ListView<Answer> replyList = replyListViewSetup();
        layout.setCenter(replyList);
        //Setup top toolbar
        TextField replyInput = UIFactory.createTextField("Reply:", f -> f.minWidth(200).maxWidth(600).minChars(10).maxChars(2000));
        Button addReply = replyAddButtonSetup(replyList, replyInput);
        Button editReply = replyEditButtonSetup();
        Button deleteReply = replyDeleteButtonSetup(replyList.getSelectionModel().getSelectedItem());
        Button addReplyToSelected = replyToSelectedButtonSetup(replyList, replyInput);
        topBar.getChildren().addAll(replyInput, addReply, addReplyToSelected, editReply, deleteReply);
        layout.setTop(topBar);
        return layout;
    }


    private ListView<Answer> replyListViewSetup() {
        ListView<Answer> replyList = new ListView<>();
        updateList();
        replyList.setItems(replies);
        replyList.setCellFactory(param -> new ListCell<Answer>() {
            @Override
            protected void updateItem(Answer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMessage().getContent());
                }
            }
        });
        return replyList;
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
                            if (context.getSession().getCurrentRole() == Roles.REVIEWER) {
                                replyContent = "φ " + replyContent + " φ";
                            }
                            Message tempMessage = new Message(context.getSession().getActiveUser().getId(), replyContent);
                            Answer newAnswer = new Answer(tempMessage, null, root.getId(), false);
                            context.answers().create(newAnswer);
                            updateList();
                            replyTable.setItems(replies);
                        }
                )
        );
        return addReplyButton;
    }

    private Button replyToSelectedButtonSetup(ListView<Answer> replyTable, TextField replyInput) {
        Button addReplyButton = UIFactory.createButton("Add Reply to Selected",
                e -> e.onAction(
                        a -> {
                            String replyContent = replyInput.getCharacters().toString();
                            if (context.getSession().getCurrentRole() == Roles.REVIEWER) {
                                replyContent = "φ " + replyContent + " φ";
                            }
                            Answer parentAnswer = replyTable.getSelectionModel().getSelectedItem();
                            Message tempMessage = new Message(context.getSession().getActiveUser().getId(), replyContent);
                            Answer newAnswer = new Answer(tempMessage, null, parentAnswer.getId(), false);
                            context.answers().create(newAnswer);
                            updateList();
                            replyTable.setItems(replies);
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

    private void updateList() {
        ObservableList<Answer> tempReplyList = FXCollections.observableArrayList();
        replies = findAllAnswers(root, tempReplyList);
        rearrangeAnswers(replies);
    }

    private ObservableList<Answer> findAllAnswers(Answer answer, ObservableList<Answer> tempReplies) {
        List<Answer> localReplies = context.answers().getRepliesToAnswer(answer.getId());
        tempReplies.add(answer);
        if (localReplies == null) {
            return tempReplies;
        }
        for (Answer reply : localReplies) {
            findAllAnswers(reply, tempReplies);
        }
        return tempReplies;
    }

    private void rearrangeAnswers(ObservableList<Answer> replies) {
        resetSpacing();
        for (Answer reply : replies) {
            int depth = 0;
            Answer tempAnswer = reply;
            while (tempAnswer != root && tempAnswer.getParentAnswerId() != null) {
                depth++;
                tempAnswer = context.answers().getById(tempAnswer.getParentAnswerId());
            }
            StringBuilder reformat = new StringBuilder(reply.getMessage().getContent());
            for (int i = 0; i < depth; i++) {
                reformat.insert(0, "    ");
            }
            reply.getMessage().setContent(reformat.toString());
        }
    }

    private void resetSpacing() {
        for (Answer reply : replies) {
            String fixSpacing = reply.getMessage().getContent().strip();
            reply.getMessage().setContent(fixSpacing);
        }
    }
}
