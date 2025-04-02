package database.model.entities;

import database.model.BaseEntity;

public class Review extends BaseEntity {
    private User reviewer;
    private User user;
    private int rating; // 0-5

    public Review() {
    }

    public Review(User reviewer, User user, int rating) {
        this.reviewer = reviewer;
        this.user = user;
        this.rating = rating;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}