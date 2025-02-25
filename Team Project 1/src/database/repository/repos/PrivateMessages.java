package src.database.repository.repos;

import src.database.model.entities.PrivateMessage;
import src.database.repository.Repository;
import src.validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PrivateMessages extends Repository<PrivateMessage> {
    public PrivateMessages(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public PrivateMessage create(PrivateMessage privateMessage) {
        EntityValidator.validatePrivateMessage(privateMessage); // NEW: validate input

        String sql = "INSERT INTO PrivateMessages (userID, content, questionID) VALUES (?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, privateMessage.getUserId());
            pstmt.setString(2, privateMessage.getContent());

            // questionID can be null
            if (privateMessage.getQuestionId() != null) {
                pstmt.setInt(3, privateMessage.getQuestionId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
        });

        if (generatedId > 0) {
            privateMessage.setId(generatedId);
        }
        return privateMessage;
    }

    @Override
    public PrivateMessage getById(int id) {
        String sql = "SELECT * FROM PrivateMessages WHERE privateMessageID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<PrivateMessage> getAll() {
        String sql = "SELECT * FROM PrivateMessages";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public PrivateMessage build(ResultSet rs) throws SQLException {
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setId(rs.getInt("privateMessageID"));
        privateMessage.setUserId(rs.getInt("userID"));
        privateMessage.setContent(rs.getString("content"));

        int qId = rs.getInt("questionID");
        if (!rs.wasNull()) {
            privateMessage.setQuestionId(qId);
        } else {
            privateMessage.setQuestionId(null);
        }

        privateMessage.setCreatedAt(rs.getTimestamp("createdAt"));
        return privateMessage;
    }

    @Override
    public PrivateMessage update(PrivateMessage privateMessage) {
        EntityValidator.validatePrivateMessage(privateMessage);

        String sql = "UPDATE PrivateMessages SET userID = ?, content = ?, questionID = ? " +
                "WHERE privateMessageID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, privateMessage.getUserId());
            pstmt.setString(2, privateMessage.getContent());

            if (privateMessage.getQuestionId() != null) {
                pstmt.setInt(3, privateMessage.getQuestionId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            pstmt.setInt(5, privateMessage.getId());
        });

        return rows > 0 ? privateMessage : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM PrivateMessages WHERE privateMessageID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    // ------------------------------------------------------------------------
    // Additional methods:

    /**
     * Returns all top-level privateMessages to a particular question
     */
    public List<PrivateMessage> getPrivateMessageByQuestionId(int questionId) {
        String sql = "SELECT * FROM PrivateMessages WHERE questionID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, questionId), this::build);
    }

    /**
     * Search privateMessages by keyword in the content, ignoring case.
     */
    public List<PrivateMessage> searchPrivateMessages(String keyword) {
        String sql = "SELECT * FROM PrivateMessages WHERE LOWER(content) LIKE ?";
        String param = "%" + keyword.toLowerCase() + "%";

        return queryForList(sql, pstmt -> pstmt.setString(1, param), this::build);
    }

    /**
     * Get privateMessages posted by a particular user.
     */
    public List<PrivateMessage> getPrivateMessageByUser(int userId) {
        String sql = "SELECT * FROM PrivateMessages WHERE userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }
}