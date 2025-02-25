package src.database.model.entities;

import src.database.model.BaseEntity;

import java.sql.Timestamp;

/**
 * Represents a row in the Answers table.
 * An Answer can either link to a Question OR another Answer (parentAnswerId).
 */
public class Answer extends BaseEntity {
    private int userId;             // new field referencing Users(userID)
    private String content;
    private Integer questionId;     // can be null
    private Integer parentAnswerId; // can be null
    private Timestamp createdAt;
    private boolean isPinned;

    public Answer() {
    }

    /**
     * @param userId         - user posting this answer
     * @param content        - the answer text
     * @param questionId     - if this is a top-level answer
     * @param parentAnswerId - if this is a reply to another answer
     */
    public Answer(int userId, String content, Integer questionId, Integer parentAnswerId) {
        this.userId = userId;
        this.content = content;
        this.questionId = questionId;
        this.parentAnswerId = parentAnswerId;
    }

    // Getters & setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }
}
