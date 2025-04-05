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

/**
 * Repository class for managing {@link Question} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Questions" table, including creating,
 * retrieving, updating, and deleting questions. It extends the {@link Repository} class, which provides
 * base functionality for database operations.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class Questions extends Repository<Question> {
    private final Messages messagesRepo;
    private final String baseJoinQuery =
            "SELECT q.questionID, q.title, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM Questions q " +
                    "JOIN Messages m ON q.messageID = m.messageID ";

    /**
     * Constructor for {@code Questions} repository.
     * Initializes the repository with the provided database connection.
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public Questions(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

    /**
     * Creates a new question in the "Questions" table.
     * <p>
     * Validates the {@link Question} entity before inserting it into the database.
     * </p>
     *
     * @param question The {@link Question} object to be created.
     * @return The created {@link Question} object, with its ID set.
     * @throws IllegalArgumentException If the question is invalid.
     */
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

    /**
     * Retrieves a question by its ID.
     *
     * @param id The ID of the question to be retrieved.
     * @return The {@link Question} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public Question getById(int id) {
        String sql = baseJoinQuery + "WHERE q.questionID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all questions from the "Questions" table.
     *
     * @return A list of all {@link Question} objects in the table.
     */
    @Override
    public List<Question> getAll() {
        String sql = baseJoinQuery;
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Builds a {@link Question} object from a {@link ResultSet}.
     * <p>
     * This method maps the result set from a SQL query to a {@link Question} object.
     * </p>
     *
     * @param rs The {@link ResultSet} containing the question data.
     * @return The {@link Question} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
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

    /**
     * Updates an existing question in the "Questions" table.
     * <p>
     * This method only updates the title of the question and updates the message content in the associated {@link Message}.
     * </p>
     *
     * @param question The {@link Question} object containing the updated information.
     * @return The updated {@link Question} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the question is invalid.
     */
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

    /**
     * Deletes a question from the "Questions" table by its ID.
     * <p>
     * The message associated with the question is not deleted explicitly, but it will be removed
     * automatically if a cascading delete rule is set up in the database schema.
     * </p>
     *
     * @param id The ID of the question to be deleted.
     */
    @Override
    public void delete(int id) {
        // Only delete from Questions; message row is removed by cascade if set up
        String sql = "DELETE FROM Questions WHERE questionID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Returns a list of questions posted by a particular user.
     *
     * @param userId The ID of the user whose questions are to be retrieved.
     * @return A list of {@link Question} objects posted by the user.
     */
    public List<Question> getQuestionsByUser(int userId) {
        String sql = baseJoinQuery + "WHERE m.userID = ?";
        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Searches questions by fuzzy-matching both the title and the content.
     * <p>
     * This method retrieves all questions and performs an in-memory fuzzy search using the specified keyword.
     * </p>
     *
     * @param keyword The search keyword to match in the question title and content.
     * @return A list of {@link Question} objects that match the search keyword.
     * @throws Exception If an error occurs during the search operation.
     */
    public List<Question> searchQuestions(String keyword) throws Exception {
        // Basic approach: retrieve all, then do fuzzy filter in-memory
        List<Question> all = getAll();
        return SearchUtil.fullTextSearch(all, keyword,
                q -> q.getTitle() + " " + q.getMessage().getContent()
        );
    }

    /**
     * Updates the title and content of an existing question.
     * <p>
     * This method allows updating the question's title and content. If a field is not provided,
     * it will not be updated.
     * </p>
     *
     * @param questionId The ID of the question to be updated.
     * @param newTitle   The new title for the question (can be {@code null} if not updating).
     * @param newContent The new content for the question (can be {@code null} if not updating).
     * @return The updated {@link Question} object if the update was successful, or {@code null} if the question was not found.
     */
    public Question updateQuestionFields(int questionId, String newTitle, String newContent) {
        Question existing = getById(questionId);
        if (existing == null) return null;
        if (newTitle != null) {
            existing.setTitle(newTitle);
        }
        if (newContent != null) {
            existing.getMessage().setContent(newContent);
        }
        return update(existing);
    }

    /**
     * Returns a list of unanswered questions.
     *
     * @return A list of {@link Question} objects that do not have associated answers.
     */
    public List<Question> getUnansweredQuestions() {
        String sql = baseJoinQuery +
                "LEFT JOIN Answers a ON q.questionID = a.questionID " +
                "WHERE a.answerID IS NULL";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Returns a list of questions that do not have a pinned answer.
     * <p>
     * This method retrieves questions that either do not have any answers or have answers but none are pinned.
     * </p>
     *
     * @return A list of {@link Question} objects that do not have a pinned answer.
     */
    public List<Question> getQuestionsWithoutPinnedAnswer() {
        String sql = baseJoinQuery +
                "LEFT JOIN Answers a ON q.questionID = a.questionID " +
                "GROUP BY q.questionID " +
                "HAVING COUNT(CASE WHEN a.isPinned = TRUE THEN 1 END) = 0";
        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Checks if a question has a pinned answer.
     *
     * @param questionId The ID of the question to check.
     * @return {@code true} if the question has a pinned answer, {@code false} otherwise.
     */
    public boolean hasPinnedAnswer(int questionId) {
        String sql = "SELECT COUNT(*) FROM Answers WHERE questionID = ? AND isPinned = TRUE";
        return queryForBoolean(sql, pstmt -> pstmt.setInt(1, questionId));
    }
}