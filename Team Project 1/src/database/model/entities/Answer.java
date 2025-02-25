package database.model.entities;

import database.model.BaseEntity;

public class Answer extends BaseEntity {
    private Message message;        // Underlying message
    private Integer questionId;     // If top-level answer (optional)
    private Integer parentAnswerId; // For nested replies (optional)
    private boolean isPinned;

    public Answer() {
    }

    /**
     * Creates a new Answer with no existing answerID.
     */
    public Answer(Message message, Integer questionId, Integer parentAnswerId, boolean isPinned) {
        this.message = message;
        this.questionId = questionId;
        this.parentAnswerId = parentAnswerId;
        this.isPinned = isPinned;
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

    public Integer getParentAnswerId() {
        return parentAnswerId;
    }

    public void setParentAnswerId(Integer parentAnswerId) {
        this.parentAnswerId = parentAnswerId;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }
}