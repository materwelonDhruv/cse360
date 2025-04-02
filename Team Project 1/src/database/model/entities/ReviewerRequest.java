package database.model.entities;

import database.model.BaseEntity;

import java.sql.Timestamp;

public class ReviewerRequest extends BaseEntity {
    private User requester;
    private User instructor;
    private Boolean status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ReviewerRequest() {
    }

    public ReviewerRequest(User requester, User instructor, Boolean status) {
        this.requester = requester;
        this.instructor = instructor;
        this.status = status;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}