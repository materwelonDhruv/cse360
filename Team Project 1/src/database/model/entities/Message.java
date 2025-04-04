package database.model.entities;

import database.model.BaseEntity;

import java.sql.Timestamp;

/**
 * Represents a message in the system.
 * <p>
 * This class stores the message content, the user ID of the message author, and the timestamp of when
 * the message was created.
 * </p>
 *
 * @author Dhruv
 * @see BaseEntity
 */
public class Message extends BaseEntity {
    private int userId;
    private String content;
    private Timestamp createdAt;

    /**
     * Default constructor for {@code Message}.
     * Initializes a new instance of the class without setting any properties.
     */
    public Message() {
    }

    /**
     * Creates a new message with the specified user ID and content.
     *
     * @param userId  The ID of the user who sent the message.
     * @param content The content of the message.
     */
    public Message(int userId, String content) {
        this.userId = userId;
        this.content = content;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Gets the ID of the user who sent the message.
     *
     * @return The user ID of the message sender.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who sent the message.
     *
     * @param userId The new user ID of the message sender.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the content of the message.
     *
     * @return The content of the message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     *
     * @param content The new content to be set for the message.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the timestamp when the message was created.
     *
     * @return The timestamp of when the message was created.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the message was created.
     *
     * @param createdAt The new timestamp to be set for the message creation time.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}