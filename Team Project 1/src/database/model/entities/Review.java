package database.model.entities;

import database.model.BaseEntity;

/**
 * Represents a review given by a user to a reviewer.
 * <p>
 * This class stores the reviewer, the user being reviewed, and the position in their list given by the user.
 * </p>
 *
 * @author Dhruv
 * @see User
 */
public class Review extends BaseEntity {
    private User reviewer;
    private User user;
    private int rating;

    /**
     * Default constructor for {@code Review}.
     * Initializes a new instance of the class without setting any properties.
     */
    public Review() {
    }

    /**
     * Constructs a new {@code Review} with the specified reviewer, user, and rating.
     *
     * @param reviewer The user who is reviewing the other user.
     * @param user     The user being reviewed.
     * @param rating   The position given by the reviewer.
     */
    public Review(User reviewer, User user, int rating) {
        this.reviewer = reviewer;
        this.user = user;
        this.rating = rating;
    }

    /**
     * Gets the reviewer who gave the rating.
     *
     * @return The user who got the review.
     */
    public User getReviewer() {
        return reviewer;
    }

    /**
     * Sets the reviewer who gave the rating.
     *
     * @param reviewer The user who is receiving the review.
     */
    public Review setReviewer(User reviewer) {
        this.reviewer = reviewer;
        return this;
    }

    /**
     * Gets the user being reviewed.
     *
     * @return The user who gave the review.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user being reviewed.
     *
     * @param user The user giving the review.
     */
    public Review setUser(User user) {
        this.user = user;
        return this;
    }

    /**
     * Gets the rating given to the user.
     *
     * @return The position for the user.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating given to the user.
     *
     * @param rating The position to be set for the user.
     */
    public Review setRating(int rating) {
        this.rating = rating;
        return this;
    }
}