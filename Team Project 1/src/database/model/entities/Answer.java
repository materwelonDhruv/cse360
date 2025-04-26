package database.model.entities;

import database.model.BaseEntity;

/**
 * Represents an answer to a question in the system.
 * <p>
 * This class stores the message related to the answer, the associated question ID, and the parent answer ID
 * (if this answer is part of a thread). It also contains a boolean to indicate if the answer is pinned.
 * </p>
 *
 * @author Dhruv
 * @see Message
 */
public class Answer extends BaseEntity {
    private Message message;        // Underlying message
    private Integer questionId;     // If top-level answer (optional)
    private Integer parentAnswerId; // For nested replies (optional)
    private boolean isPinned;

    /**
     * Default constructor for {@code Answer}.
     * Initializes a new instance of the Answer class without setting any properties.
     */
    public Answer() {
    }

    /**
     * Constructs a new {@code Answer} with the specified message, question ID, parent answer ID, and pinned status.
     *
     * @param message        The message content of the answer.
     * @param questionId     The ID of the question this answer is related to.
     * @param parentAnswerId The ID of the parent answer if this is a nested reply.
     * @param isPinned       Indicates whether the answer is pinned.
     */
    public Answer(Message message, Integer questionId, Integer parentAnswerId, boolean isPinned) {
        this.message = message;
        this.questionId = questionId;
        this.parentAnswerId = parentAnswerId;
        this.isPinned = isPinned;
    }

    /**
     * Gets the message associated with the answer.
     *
     * @return The message content of the answer.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the answer.
     *
     * @param message The new message to be associated with the answer.
     */
    public Answer setMessage(Message message) {
        this.message = message;
        return this;
    }

    /**
     * Gets the ID of the question this answer is related to.
     *
     * @return The question ID associated with this answer.
     */
    public Integer getQuestionId() {
        return questionId;
    }

    /**
     * Sets the ID of the question this answer is related to.
     *
     * @param questionId The new question ID to be associated with the answer.
     */
    public Answer setQuestionId(Integer questionId) {
        this.questionId = questionId;
        return this;
    }

    /**
     * Gets the ID of the parent answer, if this answer is a reply to another.
     *
     * @return The parent answer ID, or {@code null} if this is a top-level answer.
     */
    public Integer getParentAnswerId() {
        return parentAnswerId;
    }

    /**
     * Sets the ID of the parent answer, if this answer is a reply to another.
     *
     * @param parentAnswerId The new parent answer ID to be set.
     */
    public Answer setParentAnswerId(Integer parentAnswerId) {
        this.parentAnswerId = parentAnswerId;
        return this;
    }

    /**
     * Gets whether the answer is pinned.
     *
     * @return {@code true} if the answer is pinned, {@code false} otherwise.
     */
    public boolean getIsPinned() {
        return isPinned;
    }

    /**
     * Sets the pinned status of the answer.
     *
     * @param pinned {@code true} if the answer should be pinned, {@code false} otherwise.
     */
    public Answer setPinned(boolean pinned) {
        this.isPinned = pinned;
        return this;
    }
}