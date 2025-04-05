package application.pages;

import application.framework.*;
import database.model.entities.Answer;
import database.model.entities.Message;
import database.model.entities.Review;
import database.model.entities.User;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays page with all replies of an answer. Supports nested replies and multiple users.
 */
@Route(MyPages.REPLY_LIST)
@View(title = "Reply List")
public class ReplyList extends BasePage {
    private static Answer root;
    private ObservableList<Answer> replies;

    // Keeps track of whether only trusted reviews are being shown rather than all reviews
    private boolean trustedReviewsOnly = false;

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
        //TODO: more robust input validation
        //TODO: add username displays
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
        Button editReply = replyEditButtonSetup(replyList, replyInput);
        Button deleteReply = replyDeleteButtonSetup(replyList);
        Button addReplyToSelected = replyToSelectedButtonSetup(replyList, replyInput);
        Button showTrustedReviewsOnly = showTrustedReviewsOnlySetup(replyList);
        topBar.getChildren().addAll(replyInput, addReply, addReplyToSelected, editReply, deleteReply);
        if (context.getSession().getCurrentRole() == Roles.STUDENT) {topBar.getChildren().add(showTrustedReviewsOnly);}
        layout.setTop(topBar);
        return layout;
    }


    /**
     * @return ListView of Answer objects, with a CellFactory that automatically displays their body text.
     * Internal method for the creation of the replyList ListView
     */
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

    /**
     * @param replyTable The replyTable set up previously with replyViewSetup.
     * @param replyInput The textField containing the user input.
     * @return Button containing the ability to reply to a selected item in the ListView.
     */
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
     * @param replyTable The replyTable set up previously with replyViewSetup.
     * @param replyInput The textField containing the user input.
     * @return Button containing the ability to edit a selected item in the ListView.
     */
    private Button replyEditButtonSetup(ListView<Answer> replyTable, TextField replyInput) {
        Button editReplyButton = UIFactory.createButton("Edit Reply",
                e -> e.onAction(
                        a -> {
                            String text = replyInput.getText();
                            if (context.getSession().getCurrentRole() == Roles.REVIEWER) {
                                text = "φ " + text + " φ";
                            }
                            Answer targetAnswer = replyTable.getSelectionModel().getSelectedItem();
                            targetAnswer.getMessage().setContent(text);
                            context.answers().update(targetAnswer);
                            updateList();
                            replyTable.setItems(replies);
                        }
                )
        );
        return editReplyButton;
    }

    /**
     * @param replyTable The replyTable set up previously with replyViewSetup.
     * @return Button containing the ability to delete a selected
     */
    private Button replyDeleteButtonSetup(ListView<Answer> replyTable) {
        Button deleteReplyButton = UIFactory.createButton("Delete Reply",
                e -> e.onAction(
                        a -> {
                            Answer selectedAnswer = replyTable.getSelectionModel().getSelectedItem();
                            selectedAnswer.getMessage().setContent("[DELETED] ");
                            context.answers().update(selectedAnswer);
                            updateList();
                            replyTable.setItems(replies);
                        }
                )
        );
        return deleteReplyButton;
    }

    /**
     * @param replyTable The ListView object containing all replies
     * @return showTrustedReviewsOnlyButton
     * Private method to build the button to switch between showing trusted reviews only and all reviews
     */
    private Button showTrustedReviewsOnlySetup(ListView<Answer> replyTable) {
        Button showTrustedReviewsOnlyButton = UIFactory.createButton("Show Trusted Reviews Only");
        showTrustedReviewsOnlyButton.setOnAction(
                a -> {
                    trustedReviewsOnly = !trustedReviewsOnly;
                    if (trustedReviewsOnly) {
                        showTrustedReviewsOnlyButton.setText("Show All Reviews");
                    } else {
                        showTrustedReviewsOnlyButton.setText("Show Trusted Reviews Only");
                    }
                    updateList();
                    replyTable.setItems(replies);
                }
        );
        return showTrustedReviewsOnlyButton;
    }

    /**
     * Internal method for updating the replies ObservableList
     */
    private void updateList() {
        ObservableList<Answer> tempReplyList = FXCollections.observableArrayList();
        replies = findAnswers(root, tempReplyList);
        rearrangeAnswers(replies);
    }

    /**
     * @param answer      root answer to search from
     * @param tempReplies the list that is overwritten and replaced with the result
     * @return A copy of the ObservableList filled, unsorted, with all answers descended from the root.
     * Recursively traverses the list, finding all replies to a root answer. It will replace the passed ObservableList
     * as well as return a copy for code formatting.
     */
    private ObservableList<Answer> findAnswers(Answer answer, ObservableList<Answer> tempReplies) {
        List<Answer> localReplies = context.answers().getRepliesToAnswer(answer.getId());
        tempReplies.add(answer);
        if (localReplies == null) {
            return tempReplies;
        }
        if (trustedReviewsOnly) {
            try {
                List<User> untrustedReviewers = context.users().getReviewersNotRatedByUser(context.getSession().getActiveUser().getId());
                List<Integer> untrustedReviewerIds = new ArrayList<>();
                for (User untrustedReviewer : untrustedReviewers) {untrustedReviewerIds.add(untrustedReviewer.getId());}
                localReplies.removeIf(a -> untrustedReviewerIds.contains(a.getMessage().getUserId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        for (Answer reply : localReplies) {
            findAnswers(reply, tempReplies);
        }
        return tempReplies;
    }

    /**
     * @param replies The unsorted ObservableList of Answers
     *                Rearranges the Answers to properly format the replies to each Answer
     */
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

    /**
     * Resets the spacing to all Answers in the replies ObservableList
     */
    private void resetSpacing() {
        for (Answer reply : replies) {
            String fixSpacing = reply.getMessage().getContent().strip();
            reply.getMessage().setContent(fixSpacing);
        }
    }
}
