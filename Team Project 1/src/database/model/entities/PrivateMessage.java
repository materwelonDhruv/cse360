package database.model.entities;

import database.model.BaseEntity;

public class PrivateMessage extends BaseEntity {
    private Message message;   // Underlying message
    private Integer questionId;
    private Integer parentPrivateMessageId; // For nested replies (optional)

    public PrivateMessage() {
    }

    /**
     * Creates a new PrivateMessage (no existing ID).
     */
    public PrivateMessage(Message message, Integer questionId, Integer parentPrivateMessageId) {
        this.message = message;
        this.questionId = questionId;
        this.parentPrivateMessageId = parentPrivateMessageId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getParentPrivateMessageId() {
        return parentPrivateMessageId;
    }

    public void setParentPrivateMessageId(Integer parentPrivateMessageId) {
        this.parentPrivateMessageId = parentPrivateMessageId;
    }
}