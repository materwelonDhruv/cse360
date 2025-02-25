package database.model.entities;

import database.model.BaseEntity;

public class PrivateMessage extends BaseEntity {
    private Message message;   // Underlying message
    private Integer questionId;

    public PrivateMessage() {
    }

    /**
     * Creates a new PrivateMessage (no existing ID).
     */
    public PrivateMessage(Message message, Integer questionId) {
        this.message = message;
        this.questionId = questionId;
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
}