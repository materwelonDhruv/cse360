package database.repository.repos;

import database.model.entities.Message;
import database.repository.Repository;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for managing {@link Message} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Messages" table, including creating,
 * retrieving, updating, and deleting messages. It extends the {@link Repository} class, which provides
 * base functionality for database operations.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class Messages extends Repository<Message> {

    /**
     * Constructor for {@code Messages} repository.
     * <p>
     * Initializes the repository with the provided database connection.
     * </p>
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public Messages(Connection connection) throws SQLException {
        super(connection);
    }

    /**
     * Creates a new message in the "Messages" table.
     * <p>
     * Validates the {@link Message} entity before inserting it into the database.
     * </p>
     *
     * @param msg The {@link Message} object to be created.
     * @return The created {@link Message} object, with its ID set.
     * @throws IllegalArgumentException If the message content is invalid.
     */
    @Override
    public Message create(Message msg) throws IllegalArgumentException {
        EntityValidator.validateMessage(msg);
        String sql = "INSERT INTO Messages (userID, content, createdAt) VALUES (?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, msg.getUserId());
            pstmt.setString(2, msg.getContent());
            pstmt.setTimestamp(3, msg.getCreatedAt());
        });
        if (generatedId > 0) {
            msg.setId(generatedId);
        }
        return msg;
    }

    /**
     * Retrieves a message by its ID.
     *
     * @param id The ID of the message to be retrieved.
     * @return The {@link Message} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public Message getById(int id) {
        String sql = "SELECT * FROM Messages WHERE messageID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all messages from the "Messages" table.
     *
     * @return A list of all {@link Message} objects in the table.
     */
    @Override
    public List<Message> getAll() {
        String sql = "SELECT * FROM Messages";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Builds a {@link Message} object from a {@link ResultSet}.
     * <p>
     * This method maps the result set from a SQL query to a {@link Message} object.
     * </p>
     *
     * @param rs The {@link ResultSet} containing the message data.
     * @return The {@link Message} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public Message build(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("messageID"));
        m.setUserId(rs.getInt("userID"));
        m.setContent(rs.getString("content"));
        m.setCreatedAt(rs.getTimestamp("createdAt"));
        return m;
    }

    /**
     * Updates the content of an existing message in the "Messages" table.
     * <p>
     * This method only updates the content field of the message. It does not modify other fields.
     * </p>
     *
     * @param msg The {@link Message} object containing the updated content.
     * @return The updated {@link Message} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the new message content is invalid.
     */
    @Override
    public Message update(Message msg) throws IllegalArgumentException {
        EntityValidator.validateMessageContent(msg.getContent());
        String sql = "UPDATE Messages SET content = ? WHERE messageID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, msg.getContent());
            pstmt.setInt(2, msg.getId());
        });
        return rows > 0 ? msg : null;
    }

    /**
     * Deletes a message from the "Messages" table.
     * <p>
     * This method deletes a message by its ID. If there are any child rows (e.g., replies), they will be cleaned up
     * based on cascading delete rules defined in the database schema.
     * </p>
     *
     * @param id The ID of the message to be deleted.
     */
    @Override
    public void delete(int id) {
        // Deleting the row from Messages. If child rows cascade, no manual cleanup needed here.
        String sql = "DELETE FROM Messages WHERE messageID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }
}