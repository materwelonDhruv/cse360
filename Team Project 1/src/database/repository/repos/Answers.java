package database.repository.repos;

import database.model.entities.Answer;
import database.model.entities.Message;
import database.repository.Repository;
import utils.SearchUtil;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Answers extends Repository<Answer> {
    private final Messages messagesRepo;
    private final String baseJoinQuery =
            "SELECT a.answerID, a.questionID, a.parentAnswerID, a.isPinned, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM Answers a " +
                    "JOIN Messages m ON a.messageID = m.messageID ";

    public Answers(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

    @Override
    public Answer create(Answer answer) throws IllegalArgumentException {
        EntityValidator.validateAnswer(answer);
        Message msg = answer.getMessage();
        if (msg == null) {
            throw new IllegalArgumentException("Answer must have a Message");
        }
        messagesRepo.create(msg);

        String sql = "INSERT INTO Answers (messageID, questionID, parentAnswerID, isPinned) VALUES (?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, msg.getId());
            if (answer.getQuestionId() != null) {
                pstmt.setInt(2, answer.getQuestionId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (answer.getParentAnswerId() != null) {
                pstmt.setInt(3, answer.getParentAnswerId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setBoolean(4, answer.getIsPinned());
        });
        if (generatedId > 0) {
            answer.setId(generatedId);
        }
        return answer;
    }

    @Override
    public Answer getById(int id) {
        String sql = baseJoinQuery + "WHERE a.answerID = ?";

        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Answer> getAll() {
        String sql = baseJoinQuery;

        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public Answer build(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("answerID"));

        Message msg = new Message();
        msg.setId(rs.getInt("msg_id"));
        msg.setUserId(rs.getInt("msg_userID"));
        msg.setContent(rs.getString("msg_content"));
        msg.setCreatedAt(rs.getTimestamp("msg_createdAt"));

        answer.setMessage(msg);

        int qId = rs.getInt("questionID");
        answer.setQuestionId(!rs.wasNull() ? qId : null);

        int pId = rs.getInt("parentAnswerID");
        answer.setParentAnswerId(!rs.wasNull() ? pId : null);

        answer.setPinned(rs.getBoolean("isPinned"));
        return answer;
    }

    @Override
    public Answer update(Answer answer) throws IllegalArgumentException {
        EntityValidator.validateAnswer(answer);
        if (answer.getMessage() == null) {
            throw new IllegalArgumentException("Answer must have a Message");
        }
        messagesRepo.update(answer.getMessage());

        String sql = "UPDATE Answers SET questionID = ?, parentAnswerID = ?, isPinned = ? WHERE answerID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            if (answer.getQuestionId() != null) {
                pstmt.setInt(1, answer.getQuestionId());
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }
            if (answer.getParentAnswerId() != null) {
                pstmt.setInt(2, answer.getParentAnswerId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setBoolean(3, answer.getIsPinned());
            pstmt.setInt(4, answer.getId());
        });
        return rows > 0 ? answer : null;
    }

    @Override
    public void delete(int id) {
        // Only delete from Answers; DB cascade can handle Messages if set
        String sql = "DELETE FROM Answers WHERE answerID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Returns answers posted by a particular user (filter by message's userID).
     */
    public List<Answer> getAnswersByUser(int userId) {
        String sql = baseJoinQuery + "WHERE m.userID = ?";

        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Search by content of the underlying message.
     */
    public List<Answer> searchAnswers(String keyword) throws Exception {
        List<Answer> all = getAll();
        return SearchUtil.fullTextSearch(all, keyword,
                a -> a.getMessage().getContent()
        );
    }

    /**
     * Toggle the pinned state of an answer.
     */
    public Answer togglePin(int answerId) {
        Answer existing = getById(answerId);
        if (existing == null) return null;
        existing.setPinned(!existing.getIsPinned());
        return update(existing);
    }

    /**
     * Update only the content of an existing answer.
     */
    public Answer updateAnswerContent(int answerId, String newContent) {
        Answer existing = getById(answerId);
        if (existing == null) return null;
        existing.getMessage().setContent(newContent);
        return update(existing);
    }
}