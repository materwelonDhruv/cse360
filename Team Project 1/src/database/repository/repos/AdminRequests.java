package database.repository.repos;

import database.model.entities.AdminRequest;
import database.repository.Repository;
import utils.requests.AdminActions;
import utils.requests.RequestState;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository for managing {@link AdminRequest} entities.
 * <p>
 * Provides CRUD operations on the "AdminRequests" table, using a single
 * auto-incremented requestId as the primary key.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 * @see AdminRequest
 */
public class AdminRequests extends Repository<AdminRequest> {
    private final Users usersRepo;

    /**
     * Initializes with a database connection and Users repository.
     */
    public AdminRequests(Connection connection) throws SQLException {
        super(connection);
        this.usersRepo = new Users(connection);
    }

    /**
     * Inserts a new AdminRequest and sets its generated requestId.
     */
    @Override
    public AdminRequest create(AdminRequest req) throws IllegalArgumentException {
        EntityValidator.validateAdminRequest(req);
        String sql = "INSERT INTO AdminRequests " +
                "(requesterID,targetID,type,state,reason,context) " +
                "VALUES(?,?,?,?,?,?)";
        int id = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, req.getRequester().getId());
            pstmt.setInt(2, req.getTarget().getId());
            pstmt.setInt(3, req.getType().ordinal());
            pstmt.setInt(4, req.getState().ordinal());
            pstmt.setString(5, req.getReason());
            if (req.getContext() != null) pstmt.setInt(6, req.getContext());
            else pstmt.setNull(6, java.sql.Types.INTEGER);
        });
        req.setId(id);
        return req;
    }

    /**
     * Retrieves an AdminRequest by primary key.
     */
    @Override
    public AdminRequest getById(int id) throws SQLException {
        String sql = "SELECT requestId,requesterID,targetID,type,state,reason,context " +
                "FROM AdminRequests WHERE requestId=?";
        return queryForObject(sql, pstmt -> pstmt.setInt(1, id), this::build);
    }

    /**
     * Returns all AdminRequest records.
     */
    @Override
    public List<AdminRequest> getAll() throws SQLException {
        String sql = "SELECT requestId,requesterID,targetID,type,state,reason,context FROM AdminRequests";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Updates an existing AdminRequest by requestId.
     */
    @Override
    public AdminRequest update(AdminRequest req) throws IllegalArgumentException {
        EntityValidator.validateAdminRequest(req);
        String sql = "UPDATE AdminRequests SET requesterID=?,targetID=?,type=?,state=?,reason=?,context=? " +
                "WHERE requestId=?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, req.getRequester().getId());
            pstmt.setInt(2, req.getTarget().getId());
            pstmt.setInt(3, req.getType().ordinal());
            pstmt.setInt(4, req.getState().ordinal());
            pstmt.setString(5, req.getReason());
            if (req.getContext() != null) pstmt.setInt(6, req.getContext());
            else pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.setInt(7, req.getId());
        });
        return rows > 0 ? req : null;
    }

    /**
     * Deletes an AdminRequest by its requestId.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM AdminRequests WHERE requestId=?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Maps a ResultSet row to an AdminRequest entity.
     */
    @Override
    public AdminRequest build(ResultSet rs) throws SQLException {
        AdminRequest req = new AdminRequest();
        req.setId(rs.getInt("requestId"));
        req.setRequester(usersRepo.getById(rs.getInt("requesterID")));
        req.setTarget(usersRepo.getById(rs.getInt("targetID")));
        req.setType(AdminActions.values()[rs.getInt("type")]);
        req.setState(RequestState.values()[rs.getInt("state")]);
        req.setReason(rs.getString("reason"));
        int ctx = rs.getInt("context");
        req.setContext(rs.wasNull() ? null : ctx);
        return req;
    }

    /**
     * Retrieves all {@link AdminRequest}s matching the given action and state.
     *
     * @param action the {@link AdminActions} to filter by
     * @param state  the {@link RequestState} to filter by
     * @return a list of AdminRequest objects whose type and state match the parameters
     */
    public List<AdminRequest> filterFetch(AdminActions action, RequestState state) {
        String sql =
                "SELECT requestId, requesterID, targetID, type, state, reason, context " +
                        "FROM AdminRequests " +
                        "WHERE type = ? AND state = ?";
        return queryForList(sql, pstmt -> {
            pstmt.setInt(1, action.ordinal());
            pstmt.setInt(2, state.ordinal());
        }, this::build);
    }

    /**
     * Retrieves all {@link AdminRequest}s matching the given action, state, and requester.
     *
     * @param action      the {@link AdminActions} to filter by
     * @param state       the {@link RequestState} to filter by
     * @param requesterId the ID of the user who initiated the requests
     * @return a list of AdminRequest objects matching all three criteria
     */
    public List<AdminRequest> filterFetch(AdminActions action, RequestState state, int requesterId) {
        String sql =
                "SELECT requestId, requesterID, targetID, type, state, reason, context " +
                        "FROM AdminRequests " +
                        "WHERE type = ? AND state = ? AND requesterID = ?";
        return queryForList(sql, pstmt -> {
            pstmt.setInt(1, action.ordinal());
            pstmt.setInt(2, state.ordinal());
            pstmt.setInt(3, requesterId);
        }, this::build);
    }

    /**
     * Updates only the {@link RequestState} of the specified AdminRequest.
     *
     * @param requestId the primary key of the request to update
     * @param newState  the new {@link RequestState} to apply
     * @return the updated {@link AdminRequest}, or null if no such request exists
     * @throws IllegalArgumentException if {@code newState} is null
     */
    public AdminRequest setState(int requestId, RequestState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("RequestState must be specified.");
        }
        String sql = "UPDATE AdminRequests SET state = ? WHERE requestId = ?";
        executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, newState.ordinal());
            pstmt.setInt(2, requestId);
        });
        String fetchSql =
                "SELECT requestId, requesterID, targetID, type, state, reason, context " +
                        "FROM AdminRequests WHERE requestId = ?";
        return queryForObject(fetchSql, pstmt -> pstmt.setInt(1, requestId), this::build);
    }
}