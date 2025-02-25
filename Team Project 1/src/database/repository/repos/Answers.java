package src.database.repository.repos;

import src.database.model.entities.Answer;
import src.database.repository.Repository;
import src.validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for Answers table.
 */
public class Answers extends Repository<Answer> {

    public Answers(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public Answer create(Answer answer) {
        EntityValidator.validateAnswer(answer); // NEW: validate input

        String sql = "INSERT INTO Answers (userID, content, questionID, parentAnswerID) VALUES (?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, answer.getUserId());
            pstmt.setString(2, answer.getContent());

            // questionID can be null
            if (answer.getQuestionId() != null) {
                pstmt.setInt(3, answer.getQuestionId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            // parentAnswerID can be null
            if (answer.getParentAnswerId() != null) {
                pstmt.setInt(4, answer.getParentAnswerId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
        });

        if (generatedId > 0) {
            answer.setId(generatedId);
        }
        return answer;
    }

    @Override
    public Answer getById(int id) {
        String sql = "SELECT * FROM Answers WHERE answerID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Answer> getAll() {
        String sql = "SELECT * FROM Answers";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public Answer build(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("answerID"));
        answer.setUserId(rs.getInt("userID"));
        answer.setContent(rs.getString("content"));

        int qId = rs.getInt("questionID");
        if (!rs.wasNull()) {
            answer.setQuestionId(qId);
        } else {
            answer.setQuestionId(null);
        }

        int pId = rs.getInt("parentAnswerID");
        if (!rs.wasNull()) {
            answer.setParentAnswerId(pId);
        } else {
            answer.setParentAnswerId(null);
        }

        answer.setCreatedAt(rs.getTimestamp("createdAt"));
        answer.setIsPinned(rs.getBoolean("isPinned"));
        return answer;
    }

    @Override
    public Answer update(Answer answer) {
        EntityValidator.validateAnswer(answer);

        String sql = "UPDATE Answers SET userID = ?, content = ?, questionID = ?, parentAnswerID = ? " +
                "WHERE answerID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, answer.getUserId());
            pstmt.setString(2, answer.getContent());

            if (answer.getQuestionId() != null) {
                pstmt.setInt(3, answer.getQuestionId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            if (answer.getParentAnswerId() != null) {
                pstmt.setInt(4, answer.getParentAnswerId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }

            pstmt.setInt(5, answer.getId());
        });

        return rows > 0 ? answer : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Answers WHERE answerID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    // ------------------------------------------------------------------------
    // Additional methods:

    /**
     * Returns all top-level answers to a particular question
     * (i.e. questionID = ? and parentAnswerID IS NULL).
     */
    public List<Answer> getAnswersByQuestionId(int questionId) {
        String sql = "SELECT * FROM Answers WHERE questionID = ? AND parentAnswerID IS NULL";
        return queryForList(sql, pstmt -> pstmt.setInt(1, questionId), this::build);
    }

    /**
     * Returns replies to a particular answer (i.e. parentAnswerID = ?).
     */
    public List<Answer> getRepliesByAnswerId(int parentAnswerId) {
        String sql = "SELECT * FROM Answers WHERE parentAnswerID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, parentAnswerId), this::build);
    }

    /**
     * Search answers by keyword in the content, ignoring case.
     */
    public List<Answer> searchAnswers(String keyword) {
        String sql = "SELECT * FROM Answers WHERE LOWER(content) LIKE ?";
        String param = "%" + keyword.toLowerCase() + "%";

        return queryForList(sql, pstmt -> pstmt.setString(1, param), this::build);
    }

    /**
     * Get answers posted by a particular user.
     */
    public List<Answer> getAnswersByUser(int userId) {
        String sql = "SELECT * FROM Answers WHERE userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }
}
