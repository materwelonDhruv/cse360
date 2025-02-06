package src.database.repository.repos;

import src.database.model.entities.Invite;
import src.database.repository.Repository;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class Invites extends Repository<Invite> {

    public Invites(Connection connection) throws SQLException {
        super(connection);
    }

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

    @Override
    public Invite getById(int id) {
        String sql = "SELECT * FROM Invites WHERE inviteID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Invite> getAll() {
        String sql = "SELECT * FROM Invites";
        return queryForList(sql,
                pstmt -> {},
                this::build
        );
    }

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

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Invites WHERE inviteID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    public int findInviteUsedByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM Invites WHERE userID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, userId),
                rs -> rs.getInt(1)
        );
    }
}
