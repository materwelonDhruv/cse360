package src.database.repository.repos;

import src.database.model.entities.Question;
import src.database.repository.Repository;
import src.validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Questions extends Repository<Question> {

    public Questions(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public Question create(Question question) {
        EntityValidator.validateQuestion(question); // NEW: validate input

        String sql = "INSERT INTO Questions (userID, title, content) VALUES (?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, question.getUserId());
            pstmt.setString(2, question.getTitle());
            pstmt.setString(3, question.getContent());
        });

        if (generatedId > 0) {
            question.setId(generatedId);
        }
        return question;
    }

    @Override
    public Question getById(int id) {
        String sql = "SELECT * FROM Questions WHERE questionID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Question> getAll() {
        String sql = "SELECT * FROM Questions";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public Question build(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("questionID"));
        question.setUserId(rs.getInt("userID"));
        question.setTitle(rs.getString("title"));
        question.setContent(rs.getString("content"));
        question.setCreatedAt(rs.getTimestamp("createdAt"));
        return question;
    }

    @Override
    public Question update(Question question) {
        EntityValidator.validateQuestion(question);

        String sql = "UPDATE Questions SET userID = ?, title = ?, content = ? WHERE questionID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, question.getUserId());
            pstmt.setString(2, question.getTitle());
            pstmt.setString(3, question.getContent());
            pstmt.setInt(4, question.getId());
        });

        return rows > 0 ? question : null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Questions WHERE questionID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    // ------------------------------------------------------------------------
    // Additional methods:

    /**
     * Returns questions whose title or content contains the given keyword (case-insensitive).
     */
    public List<Question> searchQuestions(String keyword) {
        String sql = "SELECT * FROM Questions " +
                "WHERE LOWER(title) LIKE ? OR LOWER(content) LIKE ?";
        String param = "%" + keyword.toLowerCase() + "%";

        return queryForList(sql, pstmt -> {
            pstmt.setString(1, param);
            pstmt.setString(2, param);
        }, this::build);
    }

    /**
     * Returns all questions asked by a specific userID.
     */
    public List<Question> getQuestionsByUser(int userId) {
        String sql = "SELECT * FROM Questions WHERE userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Returns all questions that currently have no answers.
     */
    public List<Question> getUnansweredQuestions() {
        String sql = "SELECT q.* FROM Questions q " +
                "LEFT JOIN Answers a ON q.questionID = a.questionID " +
                "WHERE a.answerID IS NULL";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }
}
