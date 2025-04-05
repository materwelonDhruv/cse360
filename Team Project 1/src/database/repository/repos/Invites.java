package database.repository.repos;

import database.model.entities.Invite;
import database.repository.Repository;
import utils.Helpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Repository class for managing {@link Invite} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Invites" table, including creating,
 * retrieving, updating, and deleting invites. It also provides methods to find invites by user or invite code.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class Invites extends Repository<Invite> {

    /**
     * Constructor for {@code Invites} repository.
     * <p>
     * Initializes the repository with the provided database connection.
     * </p>
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public Invites(Connection connection) throws SQLException {
        super(connection);
    }

    /**
     * Creates a new invite in the "Invites" table.
     * <p>
     * This method generates a new invite code, sets the user ID, roles, and creation timestamp before inserting
     * the invite into the database.
     * </p>
     *
     * @param invite The {@link Invite} object to be created.
     * @return The created {@link Invite} object with its ID set.
     */
    @Override
    public Invite create(Invite invite) {
        String sql = "INSERT INTO Invites (code, userID, roles, createdAt) VALUES (?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setString(1, invite.getCode());
            if (invite.getUserId() == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, invite.getUserId());
            }
            pstmt.setInt(3, invite.getRoles());
            pstmt.setLong(4, invite.getCreatedAt());
        });
        if (generatedId > 0) {
            invite.setId(generatedId);
        }
        return invite;
    }

    /**
     * Retrieves an invite by its ID.
     *
     * @param id The ID of the invite to be retrieved.
     * @return The {@link Invite} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public Invite getById(int id) {
        String sql = "SELECT * FROM Invites WHERE inviteID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all invites from the "Invites" table.
     *
     * @return A list of all {@link Invite} objects in the table.
     */
    @Override
    public List<Invite> getAll() {
        String sql = "SELECT * FROM Invites";
        return queryForList(sql,
                pstmt -> {
                },
                this::build
        );
    }

    /**
     * Builds an {@link Invite} object from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet} containing the invite data.
     * @return The {@link Invite} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public Invite build(ResultSet rs) throws SQLException {
        Invite i = new Invite();
        i.setId(rs.getInt("inviteID"));
        i.setCode(rs.getString("code"));
        int userId = rs.getInt("userID");
        i.setUserId(rs.wasNull() ? null : userId);
        i.setRoles(rs.getInt("roles"));
        i.setCreatedAt(rs.getLong("createdAt"));
        return i;
    }

    /**
     * Updates an existing invite in the "Invites" table.
     *
     * @param invite The {@link Invite} object containing the updated information.
     * @return The updated {@link Invite} object if the update was successful, or {@code null} if no rows were affected.
     */
    @Override
    public Invite update(Invite invite) {
        String sql = "UPDATE Invites SET code = ?, userID = ?, roles = ?, createdAt = ? WHERE inviteID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, invite.getCode());
            if (invite.getUserId() == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, invite.getUserId());
            }
            pstmt.setInt(3, invite.getRoles());
            pstmt.setLong(4, invite.getCreatedAt());
            pstmt.setInt(5, invite.getId());
        });
        return rows > 0 ? invite : null;
    }

    /**
     * Deletes an invite from the "Invites" table by its ID.
     *
     * @param id The ID of the invite to be deleted.
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Invites WHERE inviteID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Finds an invitation used by a specific user ID.
     * <p>
     * This method checks if there is an invite assigned to a user based on their ID.
     * </p>
     *
     * @param userId The ID of the user whose invite is being searched for.
     * @return The invite ID, or {@code 0} if no invite is found.
     */
    public int findInviteUsedByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM Invites WHERE userID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, userId),
                rs -> rs.getInt(1)
        );
    }

    /**
     * Finds an invite by its code.
     * <p>
     * This method checks for an invite that matches the provided code and is still valid (less than 24 hours old).
     * </p>
     *
     * @param code The invite code to search for.
     * @return The {@link Invite} object if found, or {@code null} if no valid invite is found.
     */
    public Invite findInvite(String code) {
        String sql = "SELECT * FROM Invites WHERE code = ? AND ? - createdAt < 86400";

        Invite invite = queryForObject(sql,
                pstmt -> {
                    pstmt.setString(1, code);
                    pstmt.setLong(2, Helpers.getCurrentTimeInSeconds());
                },
                this::build
        );
        if (invite != null) {
            delete(invite.getId());
        }
        return invite;
    }
}