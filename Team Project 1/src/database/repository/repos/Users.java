package database.repository.repos;

import database.model.entities.Review;
import database.model.entities.User;
import database.repository.Repository;
import utils.PasswordUtil;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for managing {@link User} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Users" table, including creating,
 * retrieving, updating, and deleting users. It extends the {@link Repository} class, which provides
 * base functionality for database operations.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class Users extends Repository<User> {

    /**
     * Constructor for {@code Users} repository.
     * <p>
     * Initializes the repository with the provided database connection.
     * </p>
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public Users(Connection connection) throws SQLException {
        super(connection);
    }

    /**
     * Creates a new user in the "Users" table.
     * <p>
     * This method hashes the user's password before inserting the user into the database.
     * </p>
     *
     * @param user The {@link User} object to be created.
     * @return The created {@link User} object, with its ID set.
     */
    @Override
    public User create(User user) {
        // Hash password before inserting
        String plain = user.getPassword();
        String hashed = PasswordUtil.hashPassword(plain);
        user.setPassword(hashed);

        String sql = "INSERT INTO Users "
                + "(userName, firstName, lastName, password, email, roles) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getEmail());
            pstmt.setInt(6, user.getRoles()); // store the roles bit field
        });

        if (generatedId > 0) {
            user.setId(generatedId);
        }
        return user;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to be retrieved.
     * @return The {@link User} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM Users WHERE userID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all users from the "Users" table.
     *
     * @return A list of all {@link User} objects in the table.
     */
    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM Users";
        return queryForList(sql,
                pstmt -> {
                },
                this::build
        );
    }

    /**
     * Builds a {@link User} object from a {@link ResultSet}.
     * <p>
     * This method maps the result set from a SQL query to a {@link User} object.
     * </p>
     *
     * @param rs The {@link ResultSet} containing the user data.
     * @return The {@link User} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public User build(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("userID"));
        u.setUserName(rs.getString("userName"));
        u.setFirstName(rs.getString("firstName"));
        u.setLastName(rs.getString("lastName"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setRoles(rs.getInt("roles")); // read roles from the new column
        return u;
    }

    /**
     * Updates an existing user's information in the "Users" table.
     * <p>
     * This method re-hashes the password if it is updated.
     * </p>
     *
     * @param user The {@link User} object containing the updated information.
     * @return The updated {@link User} object if the update was successful, or {@code null} if no rows were affected.
     */
    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET userName = ?, "
                + "firstName = ?, lastName = ?, "
                + "email = ?, roles = ? WHERE userID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setInt(5, user.getRoles());
            pstmt.setInt(6, user.getId());
        });
        return rows > 0 ? user : null;
    }

    /**
     * Deletes a user from the "Users" table by their ID.
     *
     * @param id The ID of the user to be deleted.
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Users WHERE userID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Validates a user's login by comparing the hashed password stored in the database.
     *
     * @param userName      The username of the user attempting to log in.
     * @param plainPassword The plain-text password provided by the user.
     * @return {@code true} if the login is valid, {@code false} otherwise.
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
     *
     * @param userName The username to check for existence.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    public boolean doesUserExist(String userName) {
        String sql = "SELECT COUNT(*) FROM Users WHERE userName = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setString(1, userName),
                rs -> rs.getInt(1) > 0
        );
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to be retrieved.
     * @return The {@link User} object corresponding to the provided username, or {@code null} if not found.
     */
    public User getByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE userName = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setString(1, username),
                this::build
        );
    }

    /**
     * Returns a list of all users with the REVIEWER role.
     *
     * @return A list of users who have the REVIEWER role.
     */
    public List<User> getAllReviewers() {
        List<User> users = getAll();

        return users.stream()
                .filter(user -> {
                    Roles[] userRoles = RolesUtil.intToRoles(user.getRoles());
                    return RolesUtil.hasRole(userRoles, Roles.REVIEWER);
                })
                .toList();
    }

    /**
     * Returns a list of all reviewers that a user hasn't yet rated.
     *
     * @param userId The ID of the user who is rating/trusting reviewers.
     * @return A list of reviewers not yet rated by the specified user.
     * @throws SQLException If an error occurs during the retrieval of reviewers.
     */
    public List<User> getReviewersNotRatedByUser(int userId) throws SQLException {
        List<User> allReviewers = getAllReviewers();
        List<Review> userReviews = new Reviews(this.connection).getReviewersByUserId(userId);

        List<Integer> ratedReviewerIds = userReviews.stream()
                .map(review -> review.getReviewer().getId())
                .toList();

        return allReviewers.stream()
                .filter(reviewer -> !ratedReviewerIds.contains(reviewer.getId())
                        && reviewer.getId() != userId)
                .toList();
    }

    /**
     * Update user password
     *
     * @param user The user whose password is to be updated
     */
    public void updatePassword(User user) {
        // If allowing password changes, re-hash
        String hashed = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashed);

        String sql = "UPDATE Users SET password = ? WHERE userID = ?";
        executeUpdate(sql, pstmt -> {
            pstmt.setString(1, user.getPassword());
            pstmt.setInt(2, user.getId());
        });
    }
}
