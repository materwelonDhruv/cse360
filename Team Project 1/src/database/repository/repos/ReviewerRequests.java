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

/**
 * Repository class for managing {@link ReviewerRequest} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "ReviewerRequests" table,
 * including creating, retrieving, updating, and deleting reviewer requests. It extends the
 * {@link Repository} class, which provides base functionality for database operations.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 * @see ReviewerRequest
 */
public class ReviewerRequests extends Repository<ReviewerRequest> {
    private final Users usersRepo;

    /**
     * Constructor for {@code ReviewerRequests} repository.
     * <p>
     * Initializes the repository with the provided database connection.
     * </p>
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public ReviewerRequests(Connection connection) throws SQLException {
        super(connection);
        this.usersRepo = new Users(connection);
    }

    /**
     * Creates a new reviewer request in the "ReviewerRequests" table.
     * <p>
     * This method validates the {@link ReviewerRequest} entity before inserting it into the database.
     * </p>
     *
     * @param entity The {@link ReviewerRequest} object to be created.
     * @return The created {@link ReviewerRequest} object, with its ID set.
     * @throws IllegalArgumentException If the reviewer request is invalid.
     */
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

    /**
     * Retrieves a reviewer request by its ID.
     *
     * @param id The ID of the reviewer request to be retrieved.
     * @return The {@link ReviewerRequest} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public ReviewerRequest getById(int id) {
        String sql = "SELECT * FROM ReviewerRequests WHERE requestID=?";
        return queryForObject(sql, pstmt -> pstmt.setInt(1, id), this::build);
    }

    /**
     * Retrieves all reviewer requests from the "ReviewerRequests" table.
     *
     * @return A list of all {@link ReviewerRequest} objects in the table.
     */
    @Override
    public List<ReviewerRequest> getAll() {
        String sql = "SELECT * FROM ReviewerRequests";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Updates an existing reviewer request in the "ReviewerRequests" table.
     * <p>
     * This method validates the {@link ReviewerRequest} entity before performing the update.
     * </p>
     *
     * @param entity The {@link ReviewerRequest} object containing the updated information.
     * @return The updated {@link ReviewerRequest} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the reviewer request is invalid.
     */
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

    /**
     * Deletes a reviewer request from the "ReviewerRequests" table by its ID.
     *
     * @param id The ID of the reviewer request to be deleted.
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ReviewerRequests WHERE requestID=?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Builds a {@link ReviewerRequest} object from a {@link ResultSet}.
     * <p>
     * This method maps the result set from a SQL query to a {@link ReviewerRequest} object.
     * </p>
     *
     * @param rs The {@link ResultSet} containing the reviewer request data.
     * @return The {@link ReviewerRequest} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
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

    /**
     * Retrieves all requests made by a specific user.
     *
     * @param userId The ID of the user whose requests are to be retrieved.
     * @return A list of {@link ReviewerRequest} objects made by the specified user.
     */
    public List<ReviewerRequest> getRequestsByUser(int userId) {
        String sql = "SELECT * FROM ReviewerRequests WHERE userID=?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Retrieves all requests made to a specific instructor.
     *
     * @param instructorId The ID of the instructor whose requests are to be retrieved.
     * @return A list of {@link ReviewerRequest} objects made to the specified instructor.
     */
    public List<ReviewerRequest> getRequestsByInstructor(int instructorId) {
        String sql = "SELECT * FROM ReviewerRequests WHERE instructorID=?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, instructorId), this::build);
    }

    /**
     * Accepts a reviewer request by updating its status to {@code true}.
     * <p>
     * The instructor must be assigned to the request and must have the {@link Roles#INSTRUCTOR} role.
     * </p>
     *
     * @param requestId The ID of the request to be accepted.
     * @return The updated {@link ReviewerRequest} object if the request was accepted, or {@code null} if it couldn't be accepted.
     */
    public ReviewerRequest acceptRequest(int requestId) {
        ReviewerRequest rr = getById(requestId);
        if (rr == null) return null;
        if (rr.getInstructor() == null) return null;
        if (!RolesUtil.hasRole(rr.getInstructor().getRoles(), Roles.INSTRUCTOR)) return null;
        rr.setStatus(true);
        return update(rr);
    }

    /**
     * Rejects a reviewer request by updating its status to {@code false}.
     * <p>
     * The instructor must be assigned to the request and must have the {@link Roles#INSTRUCTOR} role.
     * </p>
     *
     * @param requestId The ID of the request to be rejected.
     * @return The updated {@link ReviewerRequest} object if the request was rejected, or {@code null} if it couldn't be rejected.
     */
    public ReviewerRequest rejectRequest(int requestId) {
        ReviewerRequest rr = getById(requestId);
        if (rr == null) return null;
        if (rr.getInstructor() == null) return null;
        if (!RolesUtil.hasRole(rr.getInstructor().getRoles(), Roles.INSTRUCTOR)) return null;
        rr.setStatus(false);
        return update(rr);
    }
}