package database.repository.repos;

import database.model.entities.Message;
import database.model.entities.Question;
import database.repository.Repository;
import utils.SearchUtil;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Questions extends Repository<Question> {

    private final Messages messagesRepo;
    private final String baseJoinQuery =
            "SELECT q.questionID, q.title, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM Questions q " +
                    "JOIN Messages m ON q.messageID = m.messageID ";

    public Questions(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

    @Override
    public Question create(Question question) throws IllegalArgumentException {
        EntityValidator.validateQuestion(question);
        Message msg = question.getMessage();
        if (msg == null) {
            throw new IllegalArgumentException("Question must have a Message");
        }
        messagesRepo.create(msg); // sets msg.id

        String sql = "INSERT INTO Questions (messageID, title) VALUES (?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, msg.getId());
            pstmt.setString(2, question.getTitle());
        });
        if (generatedId > 0) {
            question.setId(generatedId);
        }
        return question;
    }

    @Override
    public Question getById(int id) {
        String sql = baseJoinQuery + "WHERE q.questionID = ?";

        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<Question> getAll() {
        String sql = baseJoinQuery;

        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    @Override
    public Question build(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("questionID"));
        question.setTitle(rs.getString("title"));

        Message msg = new Message();
        msg.setId(rs.getInt("msg_id"));
        msg.setUserId(rs.getInt("msg_userID"));
        msg.setContent(rs.getString("msg_content"));
        msg.setCreatedAt(rs.getTimestamp("msg_createdAt"));

        question.setMessage(msg);
        return question;
    }

    @Override
    public Question update(Question question) throws IllegalArgumentException {
        EntityValidator.validateQuestion(question);
        if (question.getMessage() == null) {
            throw new IllegalArgumentException("Question must have a Message");
        }
        // Only content is updated in Messages
        messagesRepo.update(question.getMessage());

        String sql = "UPDATE Questions SET title = ? WHERE questionID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, question.getTitle());
            pstmt.setInt(2, question.getId());
        });
        return rows > 0 ? question : null;
    }

    @Override
    public void delete(int id) {
        // Only delete from Questions; message row is removed by cascade if set up
        String sql = "DELETE FROM Questions WHERE questionID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Returns questions posted by a particular user.
     */
    public List<Question> getQuestionsByUser(int userId) {
        String sql = baseJoinQuery + "WHERE m.userID = ?";

        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Searches questions by fuzzy-matching both title and content.
     */
    public List<Question> searchQuestions(String keyword) throws Exception {
        // Basic approach: retrieve all, then do fuzzy filter in-memory
        List<Question> all = getAll();
        return SearchUtil.fullTextSearch(all, keyword,
                q -> q.getTitle() + " " + q.getMessage().getContent()
        );
    }

    /**
     * Updates only the title of an existing question.
     */
    public Question updateQuestionTitle(int questionId, String newTitle) {
        Question existing = getById(questionId);
        if (existing == null) return null;
        existing.setTitle(newTitle);
        return update(existing);
    }

    /**
     * Updates only the content of the question's underlying message.
     */
    public Question updateQuestionContent(int questionId, String newContent) {
        Question existing = getById(questionId);
        if (existing == null) return null;
        existing.getMessage().setContent(newContent);
        return update(existing);
    }

    /**
     * Returns questions that have not been answered yet.
     *
     * @return List of unanswered questions
     */
    public List<Question> getUnansweredQuestions() {
        String sql = baseJoinQuery +
                "LEFT JOIN Answers a ON q.questionID = a.questionID " +
                "WHERE a.answerID IS NULL";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Returns a list of questions that don't have a pinned answer.
     *
     * @return List of questions without a pinned answer
     */
    public List<Question> getQuestionsWithoutPinnedAnswer() {
        String sql = baseJoinQuery +
                "LEFT JOIN Answers a ON q.questionID = a.questionID " +
                "WHERE a.isPinned = FALSE OR a.isPinned IS NULL " +
                "GROUP BY q.questionID";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }


    /**
     * Returns a true false for whether a question has a pinned answer.
     *
     * @param questionId The ID of the question to check
     */
    public boolean hasPinnedAnswer(int questionId) {
        String sql = "SELECT COUNT(*) FROM Answers WHERE questionID = ? AND isPinned = TRUE";
        return queryForBoolean(sql, pstmt -> pstmt.setInt(1, questionId));
    }
}