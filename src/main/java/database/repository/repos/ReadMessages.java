package database.repository.repos;

import database.model.BaseEntity;
import database.repository.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing read messages.
 * <p>
 * This class provides methods to interact with the "ReadMessages" table. It supports marking messages
 * as read or unread, finding read messages for a user, and performing basic CRUD operations.
 * The repository uses a pivot model with composite keys for the "userID" and "messageID".
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class ReadMessages extends Repository<ReadMessages.ReadMessagePivot> {

    /**
     * Constructor for {@code ReadMessages} repository.
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public ReadMessages(Connection connection) throws SQLException {
        super(connection);
    }

    /**
     * Creates a new read message entry in the "ReadMessages" table.
     *
     * @param pivot The {@link ReadMessagePivot} object representing the new read message.
     * @return The created {@link ReadMessagePivot} object with its ID set.
     */
    @Override
    public ReadMessagePivot create(ReadMessagePivot pivot) {
        String sql = "INSERT INTO ReadMessages (userID, messageID) VALUES (?, ?)";
        executeInsert(sql, pstmt -> {
            pstmt.setInt(1, pivot.getUserId());
            pstmt.setInt(2, pivot.getMessageId());
        });
        return pivot;
    }

    /**
     * Retrieves a read message by its ID.
     * <p>
     * This method is not supported for the {@code ReadMessages} repository as it uses a composite key.
     * </p>
     *
     * @param id The ID of the read message to be retrieved.
     * @throws UnsupportedOperationException Always thrown for this method.
     */
    @Override
    public ReadMessagePivot getById(int id) {
        throw new UnsupportedOperationException("Use composite key methods instead.");
    }

    /**
     * Retrieves all read messages for a specific user.
     *
     * @param userId The ID of the user whose read messages are to be retrieved.
     * @return A list of {@link ReadMessagePivot} objects representing the read messages for the user.
     */
    public List<ReadMessagePivot> getAllByUser(int userId) {
        String sql = "SELECT * FROM ReadMessages WHERE userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Retrieves all read messages from the "ReadMessages" table.
     *
     * @return A list of all {@link ReadMessagePivot} objects in the table.
     */
    @Override
    public List<ReadMessagePivot> getAll() {
        String sql = "SELECT * FROM ReadMessages";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Builds a {@link ReadMessagePivot} object from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet} containing the read message data.
     * @return A {@link ReadMessagePivot} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public ReadMessagePivot build(ResultSet rs) throws SQLException {
        int userId = rs.getInt("userID");
        int messageId = rs.getInt("messageID");
        return new ReadMessagePivot(userId, messageId);
    }

    /**
     * Updates a read message in the "ReadMessages" table.
     * <p>
     * Updating a read message is not supported for the pivot entity.
     * </p>
     *
     * @param pivot The {@link ReadMessagePivot} object containing the updated data.
     * @throws UnsupportedOperationException Always thrown for this method.
     */
    @Override
    public ReadMessagePivot update(ReadMessagePivot pivot) {
        throw new UnsupportedOperationException("Update not supported for read-message pivot.");
    }

    /**
     * Deletes a read message by its ID.
     * <p>
     * Deleting by ID is not supported for the pivot entity.
     * </p>
     *
     * @param id The ID of the read message to be deleted.
     * @throws UnsupportedOperationException Always thrown for this method.
     */
    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Use deleteByCompositeKey instead.");
    }

    /**
     * Deletes a read message using the composite key (userID and messageID).
     *
     * @param userId    The ID of the user.
     * @param messageId The ID of the message.
     */
    public void deleteByCompositeKey(int userId, int messageId) {
        String sql = "DELETE FROM ReadMessages WHERE userID = ? AND messageID = ?";
        executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, messageId);
        });
    }

    /**
     * Marks a message as read for a specific user.
     *
     * @param userId    The ID of the user.
     * @param messageId The ID of the message.
     */
    public void markAsRead(int userId, int messageId) {
        create(new ReadMessagePivot(userId, messageId));
    }

    /**
     * Marks multiple messages as read for a specific user.
     *
     * @param userId     The ID of the user.
     * @param messageIds A list of message IDs to be marked as read.
     */
    public void markAsRead(int userId, List<Integer> messageIds) {
        for (int messageId : messageIds) {
            create(new ReadMessagePivot(userId, messageId));
        }
    }

    /**
     * Marks a message as unread for a specific user.
     *
     * @param userId    The ID of the user.
     * @param messageId The ID of the message.
     */
    public void markAsUnread(int userId, int messageId) {
        deleteByCompositeKey(userId, messageId);
    }

    /**
     * Marks multiple messages as unread for a specific user.
     *
     * @param userId     The ID of the user.
     * @param messageIds A list of message IDs to be marked as unread.
     */
    public void markAsUnread(int userId, List<Integer> messageIds) {
        for (int messageId : messageIds) {
            deleteByCompositeKey(userId, messageId);
        }
    }

    /**
     * Finds messages that have been read by a specific user from a list of message IDs.
     *
     * @param userId     The ID of the user.
     * @param messageIds A list of message IDs to check for read status.
     * @return A list of message IDs that have been read by the user.
     */
    public List<Integer> findReadMessages(int userId, List<Integer> messageIds) {
        List<ReadMessagePivot> userRead = getAllByUser(userId);
        List<Integer> result = new ArrayList<>();
        for (ReadMessagePivot pivot : userRead) {
            if (messageIds.contains(pivot.getMessageId())) {
                result.add(pivot.getMessageId());
            }
        }
        return result;
    }

    /**
     * Represents a pivot between user and message, indicating whether the message has been read.
     */
    public static class ReadMessagePivot extends BaseEntity {
        private final int userId;
        private final int messageId;

        /**
         * Constructs a new {@code ReadMessagePivot}.
         *
         * @param userId    The ID of the user.
         * @param messageId The ID of the message.
         */
        public ReadMessagePivot(int userId, int messageId) {
            this.userId = userId;
            this.messageId = messageId;
        }

        /**
         * Gets the ID of the user.
         *
         * @return The user ID.
         */
        public int getUserId() {
            return userId;
        }

        /**
         * Gets the ID of the message.
         *
         * @return The message ID.
         */
        public int getMessageId() {
            return messageId;
        }
    }
}