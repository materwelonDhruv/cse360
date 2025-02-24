package src.database.model.entities;

import src.database.model.BaseEntity;

import java.sql.Timestamp;

public class PrivateMessage extends BaseEntity {
    private int userId;             // new field referencing Users(userID)
    private String content;
    private Integer questionId;     // can be null
    private Timestamp createdAt;

    public PrivateMessage(int userId, String content, Integer questionId, Timestamp createdAt) {
        this.userId = userId;
        this.content = content;
        this.questionId = questionId;
        this.createdAt = createdAt;
    }

    public PrivateMessage() {

    }

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
