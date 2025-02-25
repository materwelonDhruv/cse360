package database.repository.repos;

import database.model.BaseEntity;
import database.repository.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReadMessages extends Repository<ReadMessages.ReadMessagePivot> {

    public ReadMessages(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public ReadMessagePivot create(ReadMessagePivot pivot) {
        String sql = "INSERT INTO ReadMessages (userID, messageID) VALUES (?, ?)";
        executeInsert(sql, pstmt -> {
            pstmt.setInt(1, pivot.getUserId());
            pstmt.setInt(2, pivot.getMessageId());
        });
        return pivot;
    }

    @Override
    public ReadMessagePivot getById(int id) {
        throw new UnsupportedOperationException("Use composite key methods instead.");
    }

    public List<ReadMessagePivot> getAllByUser(int userId) {
        String sql = "SELECT * FROM ReadMessages WHERE userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    @Override
    public List<ReadMessagePivot> getAll() {
        String sql = "SELECT * FROM ReadMessages";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public ReadMessagePivot build(ResultSet rs) throws SQLException {
        int userId = rs.getInt("userID");
        int messageId = rs.getInt("messageID");
        return new ReadMessagePivot(userId, messageId);
    }

    @Override
    public ReadMessagePivot update(ReadMessagePivot pivot) {
        throw new UnsupportedOperationException("Update not supported for read-message pivot.");
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Use deleteByCompositeKey instead.");
    }

    public void deleteByCompositeKey(int userId, int messageId) {
        String sql = "DELETE FROM ReadMessages WHERE userID = ? AND messageID = ?";
        executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, messageId);
        });
    }

    public void markAsRead(int userId, int messageId) {
        create(new ReadMessagePivot(userId, messageId));
    }

    public void markAsRead(int userId, List<Integer> messageIds) {
        for (int messageId : messageIds) {
            create(new ReadMessagePivot(userId, messageId));
        }
    }

    public void markAsUnread(int userId, int messageId) {
        deleteByCompositeKey(userId, messageId);
    }

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

    public static class ReadMessagePivot extends BaseEntity {
        private final int userId;
        private final int messageId;

        public ReadMessagePivot(int userId, int messageId) {
            this.userId = userId;
            this.messageId = messageId;
        }

        public int getUserId() {
            return userId;
        }

        public int getMessageId() {
            return messageId;
        }
    }
}