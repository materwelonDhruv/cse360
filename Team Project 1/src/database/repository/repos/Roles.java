package src.database.repository.repos;

import src.database.model.entities.Role;
import src.database.repository.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Roles extends Repository<Role> {

    public Roles(java.sql.Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public Role create(Role role) {
        String sql = "INSERT INTO Roles (roleName) VALUES (?)";
        int generatedId = executeInsert(sql, pstmt -> pstmt.setString(1, role.getRoleName()));
        if (generatedId > 0) {
            role.setId(generatedId);
        }
        return role;
    }

    @Override
    public Role getById(int id) {
        String sql = "SELECT * FROM Roles WHERE roleID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Role> getAll() {
        String sql = "SELECT * FROM Roles";
        return queryForList(sql,
                pstmt -> {},
                this::build
        );
    }

    @Override
    public Role build(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("roleID"));
        role.setRoleName(rs.getString("roleName"));
        return role;
    }

                      @Override
    public Role update(Role role) {
        String sql = "UPDATE Roles SET roleName = ? WHERE roleID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, role.getRoleName());
            pstmt.setInt(2, role.getId());
        });
        return rows > 0 ? role : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Roles WHERE roleID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }
}