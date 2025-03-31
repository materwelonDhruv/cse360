package database.repository.repos;

import database.model.entities.Review;
import database.model.entities.User;
import database.repository.Repository;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Reviews extends Repository<Review> {
    private final Users usersRepo;

    public Reviews(Connection connection) throws SQLException {
        super(connection);
        this.usersRepo = new Users(connection);
    }

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

    @Override
    public Review getById(int id) {
        throw new UnsupportedOperationException("Use getByCompositeKey(reviewerId, userId) for Reviews.");
    }

    public Review getByCompositeKey(int reviewerId, int userId) {
        String sql = "SELECT reviewerID, userID, rating FROM Reviews WHERE reviewerID=? AND userID=?";
        return queryForObject(sql, pstmt -> {
            pstmt.setInt(1, reviewerId);
            pstmt.setInt(2, userId);
        }, this::build);
    }

    @Override
    public List<Review> getAll() {
        String sql = "SELECT reviewerID, userID, rating FROM Reviews";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

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

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Use delete(reviewerId, userId) for composite PK in Reviews.");
    }

    public void delete(int reviewerId, int userId) {
        String sql = "DELETE FROM Reviews WHERE reviewerID=? AND userID=?";
        executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, reviewerId);
            pstmt.setInt(2, userId);
        });
    }

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

    public List<Review> getReviewersByUserId(int userId) {
        String sql = "SELECT reviewerID, userID, rating FROM Reviews WHERE userID=? ORDER BY rating DESC";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    public Review setRating(User reviewer, User user, int newRating) {
        String sql = "UPDATE Reviews SET rating=? WHERE reviewerID=? AND userID=?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, newRating);
            pstmt.setInt(2, reviewer.getId());
            pstmt.setInt(3, user.getId());
        });
        return rows > 0 ? getByCompositeKey(reviewer.getId(), user.getId()) : null;
    }
}