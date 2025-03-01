package database.repository.repos;

import database.model.entities.Message;
import database.repository.Repository;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Messages extends Repository<Message> {

    public Messages(Connection connection) throws SQLException {
        super(connection);
    }

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

    @Override
    public Message getById(int id) {
        String sql = "SELECT * FROM Messages WHERE messageID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Message> getAll() {
        String sql = "SELECT * FROM Messages";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

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
     * Only updates the content field.
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

    @Override
    public void delete(int id) {
        // Deleting the row from Messages. If child rows cascade, no manual cleanup needed here.
        String sql = "DELETE FROM Messages WHERE messageID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }
}