package database.model.entities;

import database.model.BaseEntity;

/**
 * Represents a private message in the system.
 * <p>
 * This class stores the message content, the associated question ID, and the parent private message ID
 * (if it's part of a nested thread).
 * </p>
 *
 * @author Dhruv
 * @see Message
 */
public class PrivateMessage extends BaseEntity {
    private Message message;   // Underlying message
    private Integer questionId;
    private Integer parentPrivateMessageId; // For nested replies (optional)

    /**
     * Default constructor for {@code PrivateMessage}.
     * Initializes a new instance of the class without setting any properties.
     */
    public PrivateMessage() {
    }

    /**
     * Constructs a new {@code PrivateMessage} with the specified message, question ID, and parent message ID.
     *
     * @param message                The message content associated with the private message.
     * @param questionId             The ID of the question this message is associated with.
     * @param parentPrivateMessageId The ID of the parent private message if this is a reply.
     */
    public PrivateMessage(Message message, Integer questionId, Integer parentPrivateMessageId) {
        this.message = message;
        this.questionId = questionId;
        this.parentPrivateMessageId = parentPrivateMessageId;
    }

    /**
     * Gets the underlying message for the private message.
     *
     * @return The underlying message associated with the private message.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the underlying message for the private message.
     *
     * @param message The new message to be set.
     */
    public PrivateMessage setMessage(Message message) {
        this.message = message;
        return this;
    }

    /**
     * Gets the question ID associated with the private message.
     *
     * @return The ID of the question associated with this private message.
     */
    public Integer getQuestionId() {
        return questionId;
    }

    /**
     * Sets the question ID associated with the private message.
     *
     * @param questionId The new question ID to be set.
     */
    public PrivateMessage setQuestionId(Integer questionId) {
        this.questionId = questionId;
        return this;
    }

    /**
     * Checks whether a question ID exists for this private message.
     *
     * @return {@code true} if a question ID exists, {@code false} otherwise.
     */
    public boolean doesQuestionIdExist() {
        return questionId != null;
    }

    /**
     * Gets the ID of the parent private message, if this message is a reply.
     *
     * @return The ID of the parent private message, or {@code null} if there is no parent.
     */
    public Integer getParentPrivateMessageId() {
        return parentPrivateMessageId;
    }

    /**
     * Sets the ID of the parent private message, if this message is a reply.
     *
     * @param parentPrivateMessageId The new parent private message ID to be set.
     */
    public PrivateMessage setParentPrivateMessageId(Integer parentPrivateMessageId) {
        this.parentPrivateMessageId = parentPrivateMessageId;
        return this;
    }
}