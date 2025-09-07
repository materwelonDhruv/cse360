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

/**
 * Repository class for managing {@link Answer} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Answers" table, including creating,
 * retrieving, updating, and deleting answers. It also includes functionality for retrieving replies to answers
 * or questions and searching for answers by content.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 * @see Answer
 */
public class Answers extends Repository<Answer> {
    private final Messages messagesRepo;
    private final String baseJoinQuery =
            "SELECT a.answerID, a.questionID, a.parentAnswerID, a.isPinned, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM Answers a " +
                    "JOIN Messages m ON a.messageID = m.messageID ";

    /**
     * Constructor for {@code Answers} repository.
     * Initializes the repository with the provided database connection.
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public Answers(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

    /**
     * Creates a new answer in the "Answers" table.
     * <p>
     * Validates the {@link Answer} entity before inserting it into the database. This method also creates a new message
     * for the answer if it does not already exist.
     * </p>
     *
     * @param answer The {@link Answer} object to be created.
     * @return The created {@link Answer} object, with its ID set.
     * @throws IllegalArgumentException If the answer is invalid.
     */
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

    /**
     * Retrieves an answer by its ID.
     *
     * @param id The ID of the answer to be retrieved.
     * @return The {@link Answer} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public Answer getById(int id) {
        String sql = baseJoinQuery + "WHERE a.answerID = ?";

        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all answers from the "Answers" table.
     *
     * @return A list of all {@link Answer} objects in the table.
     */
    @Override
    public List<Answer> getAll() {
        String sql = baseJoinQuery;

        return queryForList(sql, pstmt -> {
        }, this::build);
    }

    /**
     * Builds an {@link Answer} object from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet} containing the answer data.
     * @return The {@link Answer} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
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

    /**
     * Updates an existing answer in the "Answers" table.
     * <p>
     * This method only updates the question ID, parent answer ID, and pinned state of the answer. The associated message
     * is also updated if necessary.
     * </p>
     *
     * @param answer The {@link Answer} object containing the updated information.
     * @return The updated {@link Answer} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the answer is invalid.
     */
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

    /**
     * Deletes an answer from the "Answers" table by its ID.
     * <p>
     * This method deletes the answer record from the table. The associated message will not be deleted explicitly,
     * but it can be managed by cascading delete rules if set in the database schema.
     * </p>
     *
     * @param id The ID of the answer to be deleted.
     */
    @Override
    public void delete(int id) {
        // Only delete from Answers; DB cascade can handle Messages if set
        String sql = "DELETE FROM Answers WHERE answerID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Returns answers posted by a particular user (filter by message's userID).
     *
     * @param userId The ID of the user whose answers are to be retrieved.
     * @return A list of {@link Answer} objects posted by the user.
     */
    public List<Answer> getAnswersByUser(int userId) {
        String sql = baseJoinQuery + "WHERE m.userID = ?";

        return queryForList(sql, pstmt -> pstmt.setInt(1, userId), this::build);
    }

    /**
     * Searches answers by content of the underlying message.
     *
     * @param keyword The keyword to search in the answer content.
     * @return A list of {@link Answer} objects that match the search keyword.
     * @throws Exception If an error occurs during the search operation.
     */
    public List<Answer> searchAnswers(String keyword) throws Exception {
        List<Answer> all = getAll();
        return SearchUtil.fullTextSearch(all, keyword,
                a -> a.getMessage().getContent()
        );
    }

    /**
     * Toggles the pinned state of an answer.
     * <p>
     * This method changes the pinned state of the answer, setting it to true if it was previously false, and vice versa.
     * </p>
     *
     * @param answerId The ID of the answer to be toggled.
     * @return The updated {@link Answer} object.
     */
    public Answer togglePin(int answerId) {
        Answer existing = getById(answerId);
        if (existing == null) return null;
        existing.setPinned(!existing.getIsPinned());
        return update(existing);
    }

    /**
     * Updates only the content of an existing answer.
     *
     * @param answerId   The ID of the answer whose content is to be updated.
     * @param newContent The new content to set for the answer.
     * @return The updated {@link Answer} object.
     */
    public Answer updateAnswerContent(int answerId, String newContent) {
        Answer existing = getById(answerId);
        if (existing == null) return null;
        existing.getMessage().setContent(newContent);
        return update(existing);
    }

    /**
     * Returns all answers that are replies to a particular answer.
     *
     * @param answerId The ID of the answer to get replies for.
     * @return A list of {@link Answer} objects that are replies to the specified answer.
     */
    public List<Answer> getRepliesToAnswer(int answerId) {
        String sql = baseJoinQuery + "WHERE a.parentAnswerID = ?";

        return queryForList(sql, pstmt -> pstmt.setInt(1, answerId), this::build);
    }

    /**
     * Returns all answers that are replies to a particular question.
     *
     * @param questionId The ID of the question to get replies for.
     * @return A list of {@link Answer} objects that are replies to the specified question.
     */
    public List<Answer> getRepliesToQuestion(int questionId) {
        String sql = baseJoinQuery + "WHERE a.questionID = ?";

        return queryForList(sql, pstmt -> pstmt.setInt(1, questionId), this::build);
    }
}