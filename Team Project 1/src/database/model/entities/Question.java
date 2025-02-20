package src.database.model.entities;

import src.database.model.BaseEntity;

import java.sql.Timestamp;

/**
 * Represents a row in the Questions table.
 */
public class Question extends BaseEntity {
    private int userId;       // new field referencing Users(userID)
    private String title;
    private String content;
    private Timestamp createdAt;

    public Question() {
    }

    public Question(int userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    // Getters & setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
