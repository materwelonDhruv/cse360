package database.model.entities;

import database.model.BaseEntity;

import java.sql.Timestamp;

/**
 * Represents a request to become a reviewer in the system.
 * <p>
 * This class stores the requester (user), the instructor (user), the status of the request, and timestamps
 * for when the request was created and last updated.
 * </p>
 *
 * @author Dhruv
 * @see User
 */
public class ReviewerRequest extends BaseEntity {
    private User requester;
    private User instructor;
    private Boolean status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor for {@code ReviewerRequest}.
     * Initializes a new instance of the class without setting any properties.
     */
    public ReviewerRequest() {
    }

    /**
     * Constructs a new {@code ReviewerRequest} with the specified requester, instructor, and status.
     *
     * @param requester  The user who is requesting to become a reviewer.
     * @param instructor The user who will approve or reject the request (instructor).
     * @param status     The current status of the request approved/rejected as a boolean or null if pending.
     */
    public ReviewerRequest(User requester, User instructor, Boolean status) {
        this.requester = requester;
        this.instructor = instructor;
        this.status = status;
    }

    /**
     * Gets the user who made the request.
     *
     * @return The user who is requesting to become a reviewer.
     */
    public User getRequester() {
        return requester;
    }

    /**
     * Sets the user who made the request.
     *
     * @param requester The user making the request.
     */
    public void setRequester(User requester) {
        this.requester = requester;
    }

    /**
     * Gets the instructor who will approve or reject the request.
     *
     * @return The user who is the instructor for this request.
     */
    public User getInstructor() {
        return instructor;
    }

    /**
     * Sets the instructor who will approve or reject the request.
     *
     * @param instructor The instructor who handles the request.
     */
    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    /**
     * Gets the current status of the request.
     *
     * @return The status of the request (approved or pending).
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * Sets the status of the request.
     *
     * @param status The new status of the request (approved or pending).
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the request was created.
     *
     * @return The timestamp when the request was created.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the request was created.
     *
     * @param createdAt The timestamp when the request was created.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the request was last updated.
     *
     * @return The timestamp when the request was last updated.
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the request was last updated.
     *
     * @param updatedAt The timestamp when the request was last updated.
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}