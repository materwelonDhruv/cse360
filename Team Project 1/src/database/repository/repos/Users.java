package src.database.repository.repos;

import src.database.model.entities.User;
import src.database.repository.Repository;
import src.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Users extends Repository<User> {

    public Users(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public User create(User user) {
        // Hash password before inserting
        String plain = user.getPassword();
        String hashed = PasswordUtil.hashPassword(plain);
        user.setPassword(hashed);

        String sql = "INSERT INTO Users (userName, password, email, roles) VALUES (?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, user.getRoles());// store the roles bit field
        });

        if (generatedId > 0) {
            user.setId(generatedId);
        }
        return user;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM Users WHERE userID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM Users";
        return queryForList(sql,
                pstmt -> {},
                this::build
        );
    }

    @Override
    public User build(ResultSet rs) throws SQLException {
        User u = new User(

        rs.getString("userName"),
        rs.getString("password"),
        rs.getString("email"),
        rs.getInt("roles") // read roles from the new column
        );
        u.setId(rs.getInt("userID"));
        return u;
    }

    @Override
    public User update(User user) {
        // If allowing password changes, re-hash
        String hashed = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashed);

        String sql = "UPDATE Users SET userName = ?, password = ?, email = ?, roles = ?, inviteUsed = ? WHERE userID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, user.getRoles());
            pstmt.setInt(6, user.getId());
        });
        return rows > 0 ? user : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Users WHERE userID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Validates a user's login by comparing the hashed password.
     */
    public boolean validateLogin(String userName, String plainPassword) {
        String sql = "SELECT password FROM Users WHERE userName = ?";
        String storedHash = queryForObject(sql,
                pstmt -> pstmt.setString(1, userName),
                rs -> rs.getString("password")
        );
        return storedHash != null && PasswordUtil.verifyPassword(storedHash, plainPassword);
    }

    /**
     * Checks if a user already exists in the database based on their userName.
     */
    public boolean doesUserExist(String userName) {
        String sql = "SELECT COUNT(*) FROM Users WHERE userName = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setString(1, userName),
                rs -> rs.getInt(1) > 0
        );
    }

    public User getByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE userName = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setString(1, username),
                this::build
        );
    }

}