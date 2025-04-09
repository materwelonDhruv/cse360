package database.repository.repos;

import database.model.entities.Message;
import database.model.entities.PrivateMessage;
import database.repository.Repository;
import utils.SearchUtil;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for managing {@link PrivateMessage} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "PrivateMessages" table,
 * including creating, retrieving, updating, and deleting private messages. It also provides methods
 * for retrieving replies to private messages or questions, and searching private messages by content.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class PrivateMessages extends Repository<PrivateMessage> {
    private final Messages messagesRepo;
    private final String baseJoinQuery =
            "SELECT pm.privateMessageID, pm.questionID, pm.parentPrivateMessageID, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM PrivateMessages pm " +
                    "JOIN Messages m ON pm.messageID = m.messageID ";

    /**
     * Constructor for {@code PrivateMessages} repository.
     * Initializes the repository with the provided database connection.
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public PrivateMessages(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

    /**
     * Creates a new private message in the "PrivateMessages" table.
     * <p>
     * Validates the {@link PrivateMessage} entity before inserting it into the database.
     * </p>
     *
     * @param pm The {@link PrivateMessage} object to be created.
     * @return The created {@link PrivateMessage} object, with its ID set.
     * @throws IllegalArgumentException If the private message is invalid.
     */
    @Override
    public PrivateMessage create(PrivateMessage pm) throws IllegalArgumentException {
        EntityValidator.validatePrivateMessage(pm);
        Message msg = pm.getMessage();
        if (msg == null) {
            throw new IllegalArgumentException("PrivateMessage must have a Message");
        }
        messagesRepo.create(msg);

        String sql = "INSERT INTO PrivateMessages (messageID, questionID, parentPrivateMessageID) VALUES (?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, msg.getId());
            if (pm.getQuestionId() != null) {
                pstmt.setInt(2, pm.getQuestionId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (pm.getParentPrivateMessageId() != null) {
                pstmt.setInt(3, pm.getParentPrivateMessageId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
        });

        if (generatedId > 0) {
            pm.setId(generatedId);
        }
        return pm;
    }

    /**
     * Retrieves a private message by its ID.
     *
     * @param id The ID of the private message to be retrieved.
     * @return The {@link PrivateMessage} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public PrivateMessage getById(int id) {
        String sql = baseJoinQuery + "WHERE pm.privateMessageID = ?";

        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all private messages from the "PrivateMessages" table.
     *
     * @return A list of all {@link PrivateMessage} objects in the table.
     */
    @Override
    public List<PrivateMessage> getAll() {
        String sql = baseJoinQuery;

        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Builds a {@link PrivateMessage} object from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet} containing the private message data.
     * @return The {@link PrivateMessage} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public PrivateMessage build(ResultSet rs) throws SQLException {
        PrivateMessage pm = new PrivateMessage();
        pm.setId(rs.getInt("privateMessageID"));

        Message msg = new Message();
        msg.setId(rs.getInt("msg_id"));
        msg.setUserId(rs.getInt("msg_userID"));
        msg.setContent(rs.getString("msg_content"));
        msg.setCreatedAt(rs.getTimestamp("msg_createdAt"));

        pm.setMessage(msg);

        int qId = rs.getInt("questionID");
        pm.setQuestionId(!rs.wasNull() ? qId : null);

        int parentPrivateMessageId = rs.getInt("parentPrivateMessageID");
        pm.setParentPrivateMessageId(!rs.wasNull() ? parentPrivateMessageId : null);

        return pm;
    }

    /**
     * Updates an existing private message in the "PrivateMessages" table.
     * <p>
     * Validates the {@link PrivateMessage} entity before performing the update. If the associated message
     * is updated, it is also updated in the "Messages" table.
     * </p>
     *
     * @param pm The {@link PrivateMessage} object containing the updated information.
     * @return The updated {@link PrivateMessage} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the private message is invalid.
     */
    @Override
    public PrivateMessage update(PrivateMessage pm) throws IllegalArgumentException {
        EntityValidator.validatePrivateMessage(pm);
        if (pm.getMessage() == null) {
            throw new IllegalArgumentException("PrivateMessage must have a Message");
        }

        messagesRepo.update(pm.getMessage());

        String sql = "UPDATE PrivateMessages SET questionID = ?, parentPrivateMessageID = ? WHERE privateMessageID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            if (pm.getQuestionId() != null) {
                pstmt.setInt(1, pm.getQuestionId());
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }

            if (pm.getParentPrivateMessageId() != null) {
                pstmt.setInt(2, pm.getParentPrivateMessageId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }

            pstmt.setInt(3, pm.getId());
        });

        return rows > 0 ? pm : null;
    }

    /**
     * Deletes a private message from the "PrivateMessages" table by its ID.
     * <p>
     * Only the private message is deleted; cascading delete for the associated message will occur if set in the DB.
     * </p>
     *
     * @param id The ID of the private message to be deleted.
     */
    @Override
    public void delete(int id) {
        // Only delete from PrivateMessages; DB cascade can handle Messages if set
        String sql = "DELETE FROM PrivateMessages WHERE privateMessageID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Returns all private messages created by a particular user.
     *
     * @param userId The ID of the user whose private messages are to be retrieved.
     * @return A list of {@link PrivateMessage} objects created by the user.
     */
    public List<PrivateMessage> getPrivateMessagesByUser(int userId) {
        String sql = baseJoinQuery + "WHERE m.userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Performs a fuzzy search by content of the underlying message.
     *
     * @param keyword The search keyword to be matched in the message content.
     * @return A list of {@link PrivateMessage} objects matching the search keyword.
     * @throws Exception If an error occurs during the search operation.
     */
    public List<PrivateMessage> searchPrivateMessages(String keyword) throws Exception {
        List<PrivateMessage> all = getAll();
        return SearchUtil.fullTextSearch(all, keyword,
                pm -> pm.getMessage().getContent()
        );
    }

    /**
     * Returns all private messages that are replies to a particular private message.
     *
     * @param privateMessageId The ID of the private message to find replies to.
     * @return A list of {@link PrivateMessage} objects that are replies to the specified private message.
     */
    public List<PrivateMessage> getRepliesToPrivateMessage(int privateMessageId) {
        String sql = baseJoinQuery + "WHERE pm.parentPrivateMessageID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, privateMessageId), this::build);
    }

    /**
     * Returns all private messages that are replies to a particular question.
     *
     * @param questionId The ID of the question to find replies to.
     * @return A list of {@link PrivateMessage} objects that are replies to the specified question.
     */
    public List<PrivateMessage> getRepliesToQuestion(int questionId) {
        String sql = baseJoinQuery + "WHERE pm.questionID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, questionId), this::build);
    }
}