package database.model.entities;

import database.model.BaseEntity;

/**
 * Represents a question in the system, including its associated message and title.
 * <p>
 * This class stores the message related to the question (which includes user ID, content, and creation date)
 * and the title of the question.
 * </p>
 *
 * @author Dhruv
 * @see Message
 */
public class Question extends BaseEntity {
    private Message message;   // Underlying message (userId, content, createdAt)
    private String title;      // Field unique to Question

    /**
     * Default constructor for {@code Question}.
     * Initializes a new instance of the class without setting any properties.
     */
    public Question() {
    }

    /**
     * Creates a new Question with the specified message and title.
     *
     * @param message The message associated with the question.
     * @param title   The title of the question.
     */
    public Question(Message message, String title) {
        this.message = message;
        this.title = title;
    }

    /**
     * Gets the message associated with the question.
     *
     * @return The message related to the question.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the question.
     *
     * @param message The new message for the question.
     */
    public Question setMessage(Message message) {
        this.message = message;
        return this;
    }

    /**
     * Gets the title of the question.
     *
     * @return The title of the question.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the question.
     *
     * @param title The new title for the question.
     */
    public Question setTitle(String title) {
        this.title = title;
        return this;
    }
}