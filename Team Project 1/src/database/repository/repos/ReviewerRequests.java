package database.repository.repos;

import database.model.entities.ReviewerRequest;
import database.model.entities.User;
import database.repository.Repository;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ReviewerRequests extends Repository<ReviewerRequest> {
    private final Users usersRepo;

    public ReviewerRequests(Connection connection) throws SQLException {
        super(connection);
        this.usersRepo = new Users(connection);
    }

    @Override
    public ReviewerRequest create(ReviewerRequest entity) throws IllegalArgumentException {
        EntityValidator.validateReviewerRequest(entity);
        String sql = "INSERT INTO ReviewerRequests (userID, instructorID, status) VALUES (?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, entity.getRequester().getId());
            if (entity.getInstructor() != null) {
                pstmt.setInt(2, entity.getInstructor().getId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (entity.getStatus() != null) {
                pstmt.setBoolean(3, entity.getStatus());
            } else {
                pstmt.setNull(3, java.sql.Types.BOOLEAN);
            }
        });
        entity.setId(generatedId);
        return entity;
    }

    @Override
    public ReviewerRequest getById(int id) {
        String sql = "SELECT * FROM ReviewerRequests WHERE requestID=?";
        return queryForObject(sql, pstmt -> pstmt.setInt(1, id), this::build);
    }

    @Override
    public List<ReviewerRequest> getAll() {
        String sql = "SELECT * FROM ReviewerRequests";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public ReviewerRequest update(ReviewerRequest entity) throws IllegalArgumentException {
        EntityValidator.validateReviewerRequest(entity);
        String sql = "UPDATE ReviewerRequests SET userID=?, instructorID=?, status=? WHERE requestID=?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, entity.getRequester().getId());
            if (entity.getInstructor() != null) {
                pstmt.setInt(2, entity.getInstructor().getId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (entity.getStatus() != null) {
                pstmt.setBoolean(3, entity.getStatus());
            } else {
                pstmt.setNull(3, java.sql.Types.BOOLEAN);
            }
            pstmt.setInt(4, entity.getId());
        });
        return rows > 0 ? entity : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ReviewerRequests WHERE requestID=?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    @Override
    public ReviewerRequest build(ResultSet rs) throws SQLException {
        ReviewerRequest rr = new ReviewerRequest();
        rr.setId(rs.getInt("requestID"));
        User requester = usersRepo.getById(rs.getInt("userID"));
        rr.setRequester(requester);
        int instructorId = rs.getInt("instructorID");
        if (!rs.wasNull()) {
            rr.setInstructor(usersRepo.getById(instructorId));
        }
        Boolean st = null;
        boolean statusVal = rs.getBoolean("status");
        if (!rs.wasNull()) {
            st = statusVal;
        }
        rr.setStatus(st);
        Timestamp cAt = rs.getTimestamp("createdAt");
        rr.setCreatedAt(cAt);
        Timestamp uAt = rs.getTimestamp("updatedAt");
        rr.setUpdatedAt(uAt);
        return rr;
    }

    public List<ReviewerRequest> getRequestsByUser(int userId) {
        String sql = "SELECT * FROM ReviewerRequests WHERE userID=?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    public List<ReviewerRequest> getRequestsByInstructor(int instructorId) {
        String sql = "SELECT * FROM ReviewerRequests WHERE instructorID=?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, instructorId), this::build);
    }

    public ReviewerRequest acceptRequest(int requestId) {
        ReviewerRequest rr = getById(requestId);
        if (rr == null) return null;
        if (rr.getInstructor() == null) return null;
        if (!RolesUtil.hasRole(rr.getInstructor().getRoles(), Roles.INSTRUCTOR)) return null;
        rr.setStatus(true);
        return update(rr);
    }

    public ReviewerRequest rejectRequest(int requestId) {
        ReviewerRequest rr = getById(requestId);
        if (rr == null) return null;
        if (rr.getInstructor() == null) return null;
        if (!RolesUtil.hasRole(rr.getInstructor().getRoles(), Roles.INSTRUCTOR)) return null;
        rr.setStatus(false);
        return update(rr);
    }
}