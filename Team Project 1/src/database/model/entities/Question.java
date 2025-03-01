package database.model.entities;

import database.model.BaseEntity;

public class Question extends BaseEntity {
    private Message message;   // Underlying message (userId, content, createdAt)
    private String title;      // Field unique to Question

    public Question() {
    }

    /**
     * Creates a new Question with no existing questionID.
     */
    public Question(Message message, String title) {
        this.message = message;
        this.title = title;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}