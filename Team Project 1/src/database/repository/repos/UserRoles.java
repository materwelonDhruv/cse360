package src.database.repository.repos;

import src.database.model.entities.UserRole;
import src.database.repository.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRoles extends Repository<UserRole> {

    public UserRoles(java.sql.Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public UserRole create(UserRole ur) {
        String sql = "INSERT INTO UserRoles (userID, roleID) VALUES (?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, ur.getUserId());
            pstmt.setInt(2, ur.getRoleId());
        });
        if (generatedId > 0) {
            ur.setId(generatedId);
        }
        return ur;
    }

    @Override
    public UserRole getById(int id) {
        String sql = "SELECT * FROM UserRoles WHERE id = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<UserRole> getAll() {
        String sql = "SELECT * FROM UserRoles";
        return queryForList(sql,
                pstmt -> {},
                this::build
        );
    }

    @Override
    public UserRole build(ResultSet rs) throws SQLException {
        UserRole ur = new UserRole();
        ur.setId(rs.getInt("id"));
        ur.setUserId(rs.getInt("userID"));
        ur.setRoleId(rs.getInt("roleID"));
        return ur;
    }

    @Override
    public UserRole update(UserRole ur) {
        String sql = "UPDATE UserRoles SET userID = ?, roleID = ? WHERE id = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, ur.getUserId());
            pstmt.setInt(2, ur.getRoleId());
            pstmt.setInt(3, ur.getId());
        });
        return rows > 0 ? ur : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM UserRoles WHERE id = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }
}