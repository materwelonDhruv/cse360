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

public class PrivateMessages extends Repository<PrivateMessage> {
    private final Messages messagesRepo;
    private final String baseJoinQuery =
            "SELECT pm.privateMessageID, pm.questionID, pm.parentPrivateMessageID, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM PrivateMessages pm " +
                    "JOIN Messages m ON pm.messageID = m.messageID ";

    public PrivateMessages(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

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

    @Override
    public PrivateMessage getById(int id) {
        String sql = baseJoinQuery + "WHERE pm.privateMessageID = ?";

        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<PrivateMessage> getAll() {
        String sql = baseJoinQuery;

        return queryForList(sql, pstmt -> {
        }, this::build);
    }

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

    @Override
    public void delete(int id) {
        // Only delete from PrivateMessages; DB cascade can handle Messages if set
        String sql = "DELETE FROM PrivateMessages WHERE privateMessageID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Returns all PrivateMessages created by a particular user.
     */
    public List<PrivateMessage> getPrivateMessagesByUser(int userId) {
        String sql = baseJoinQuery + "WHERE m.userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Fuzzy search by content of the underlying message.
     */
    public List<PrivateMessage> searchPrivateMessages(String keyword) throws Exception {
        List<PrivateMessage> all = getAll();
        return SearchUtil.fullTextSearch(all, keyword,
                pm -> pm.getMessage().getContent()
        );
    }

    /**
     * Returns all PrivateMessages that are replies to a particular PrivateMessage.
     */
    public List<PrivateMessage> getRepliesToPrivateMessage(int privateMessageId) {
        String sql = baseJoinQuery + "WHERE pm.parentPrivateMessageID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, privateMessageId), this::build);
    }

    /**
     * Returns all PrivateMessages that are replies to a particular Question.
     */
    public List<PrivateMessage> getRepliesToQuestion(int questionId) {
        String sql = baseJoinQuery + "WHERE pm.questionID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, questionId), this::build);
    }
}