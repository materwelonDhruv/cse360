package database.model.entities;

import database.model.BaseEntity;

import java.sql.Timestamp;

public class Message extends BaseEntity {
    private int userId;
    private String content;
    private Timestamp createdAt;

    public Message() {
    }

    /**
     * Creates a new Message with no existing ID.
     */
    public Message(int userId, String content) {
        this.userId = userId;
        this.content = content;
        this.createdAt = new Timestamp(System.currentTimeMillis());
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}