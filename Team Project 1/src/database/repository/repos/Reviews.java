package database.repository.repos;

import database.model.entities.Review;
import database.model.entities.User;
import database.repository.Repository;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for managing {@link Review} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Reviews" table, including creating,
 * retrieving, updating, and deleting reviews. It uses a composite key of reviewerID and userID for identifying reviews.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 * @see User
 * @see Review
 */
public class Reviews extends Repository<Review> {
    private final Users usersRepo;

    /**
     * Constructor for {@code Reviews} repository.
     * <p>
     * Initializes the repository with the provided database connection and sets up the users repository.
     * </p>
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public Reviews(Connection connection) throws SQLException {
        super(connection);
        this.usersRepo = new Users(connection);
    }

    /**
     * Creates a new review in the "Reviews" table.
     * <p>
     * Validates the {@link Review} entity before inserting it into the database.
     * </p>
     *
     * @param review The {@link Review} object to be created.
     * @return The created {@link Review} object, with its ID set.
     * @throws IllegalArgumentException If the review is invalid.
     */
    @Override
    public Review create(Review review) throws IllegalArgumentException {
        EntityValidator.validateReview(review);
        String sql = "INSERT INTO Reviews (reviewerID, userID, rating) VALUES (?, ?, ?)";
        executeInsert(sql, pstmt -> {
            pstmt.setInt(1, review.getReviewer().getId());
            pstmt.setInt(2, review.getUser().getId());
            pstmt.setInt(3, review.getRating());
        });
        return review;
    }

    /**
     * Retrieves a review by its composite key (reviewerId and userId).
     *
     * @param reviewerId The ID of the reviewer.
     * @param userId     The ID of the user being reviewed.
     * @return The {@link Review} object corresponding to the provided composite key, or {@code null} if not found.
     */
    public Review getByCompositeKey(int reviewerId, int userId) {
        String sql = "SELECT reviewerID, userID, rating FROM Reviews WHERE reviewerID=? AND userID=?";
        return queryForObject(sql, pstmt -> {
            pstmt.setInt(1, reviewerId);
            pstmt.setInt(2, userId);
        }, this::build);
    }

    /**
     * Retrieves all reviews from the "Reviews" table.
     *
     * @return A list of all {@link Review} objects in the table.
     */
    @Override
    public List<Review> getAll() {
        String sql = "SELECT reviewerID, userID, rating FROM Reviews";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Updates an existing review's rating in the "Reviews" table.
     * <p>
     * Validates the {@link Review} entity before performing the update.
     * </p>
     *
     * @param review The {@link Review} object containing the updated rating.
     * @return The updated {@link Review} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the review is invalid.
     */
    @Override
    public Review update(Review review) throws IllegalArgumentException {
        EntityValidator.validateReview(review);
        String sql = "UPDATE Reviews SET rating=? WHERE reviewerID=? AND userID=?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, review.getRating());
            pstmt.setInt(2, review.getReviewer().getId());
            pstmt.setInt(3, review.getUser().getId());
        });
        return rows > 0 ? review : null;
    }

    /**
     * Deletes a review from the "Reviews" table by its composite key (reviewerId and userId).
     *
     * @param reviewerId The ID of the reviewer.
     * @param userId     The ID of the user being reviewed.
     */
    public void delete(int reviewerId, int userId) {
        String sql = "DELETE FROM Reviews WHERE reviewerID=? AND userID=?";
        executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, reviewerId);
            pstmt.setInt(2, userId);
        });
    }

    /**
     * Builds a {@link Review} object from a {@link ResultSet}.
     * <p>
     * This method maps the result set from a SQL query to a {@link Review} object.
     * </p>
     *
     * @param rs The {@link ResultSet} containing the review data.
     * @return The {@link Review} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public Review build(ResultSet rs) throws SQLException {
        Review r = new Review();
        int reviewerId = rs.getInt("reviewerID");
        int userId = rs.getInt("userID");
        int rating = rs.getInt("rating");
        r.setRating(rating);
        r.setReviewer(usersRepo.getById(reviewerId));
        r.setUser(usersRepo.getById(userId));
        return r;
    }

    /**
     * Retrieves a list of reviewers who have reviewed a particular user.
     *
     * @param userId The ID of the user who has been reviewed.
     * @return A list of {@link Review} objects representing the reviews given by reviewers for the user.
     */
    public List<Review> getReviewersByUserId(int userId) {
        String sql = "SELECT reviewerID, userID, rating FROM Reviews WHERE userID=? ORDER BY rating DESC";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Sets or updates the rating for a reviewer by a user.
     * <p>
     * If an existing review is found, it is updated. Otherwise, a new review is created.
     * </p>
     *
     * @param reviewer  The user who is reviewing.
     * @param user      The user being reviewed.
     * @param newRating The new rating to be set for the review.
     * @return The {@link Review} object with the updated or newly set rating.
     */
    public Review setRating(User reviewer, User user, int newRating) {
        Review existing = getByCompositeKey(reviewer.getId(), user.getId());
        if (existing != null) {
            String sql = "UPDATE Reviews SET rating=? WHERE reviewerID=? AND userID=?";
            executeUpdate(sql, pstmt -> {
                pstmt.setInt(1, newRating);
                pstmt.setInt(2, reviewer.getId());
                pstmt.setInt(3, user.getId());
            });
            return getByCompositeKey(reviewer.getId(), user.getId());
        } else {
            Review newReview = new Review();
            newReview.setRating(newRating);
            newReview.setReviewer(reviewer);
            newReview.setUser(user);
            return create(newReview);
        }
    }
}