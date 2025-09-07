package database.repository.repos;

import database.model.entities.Message;
import database.model.entities.StaffMessage;
import database.model.entities.User;
import database.repository.Repository;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing {@link StaffMessage} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "StaffMessages" table, including creation,
 * retrieval, updating, and deletion of staff messages. It also implements custom operations for retrieving unique chat
 * partners, loading an entire chat between a staff member and a user, and sending new messages.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 */
public class StaffMessages extends Repository<StaffMessage> {

    private final Messages messagesRepo;
    private final Users usersRepo;
    private final String baseJoinQuery =
            "SELECT sm.staffMessageID, " +
                    "       sm.userID AS sm_userID, sm.staffID AS sm_staffID, " +
                    "       m.messageID AS msg_id, m.userID AS msg_userID, m.content AS msg_content, m.createdAt AS msg_createdAt " +
                    "FROM StaffMessages sm " +
                    "JOIN Messages m ON sm.messageID = m.messageID ";

    /**
     * Constructor for the {@code StaffMessages} repository.
     * <p>
     * Initializes the repository with the provided database connection.
     * </p>
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during initialization.
     */
    public StaffMessages(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
        this.usersRepo = new Users(connection);
    }

    /**
     * Creates a new staff message in the "StaffMessages" table.
     * <p>
     * This method validates the provided {@link StaffMessage} entity, creates its underlying {@link Message} entry,
     * and inserts the corresponding record into the "StaffMessages" table.
     * </p>
     *
     * @param staffMessage The {@link StaffMessage} entity to create.
     * @return The created {@link StaffMessage} object with its primary key set.
     * @throws IllegalArgumentException If the staff message fails validation.
     */
    @Override
    public StaffMessage create(StaffMessage staffMessage) throws IllegalArgumentException {
        EntityValidator.validateStaffMessage(staffMessage);

        // Create the underlying Message entity.
        Message createdMsg = messagesRepo.create(staffMessage.getMessage());
        staffMessage.getMessage().setId(createdMsg.getId());

        // Insert the record into StaffMessages table.
        String sql = "INSERT INTO StaffMessages (userID, staffID, messageID) VALUES (?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, staffMessage.getUser().getId());
            pstmt.setInt(2, staffMessage.getStaff().getId());
            pstmt.setInt(3, staffMessage.getMessage().getId());
        });
        if (generatedId > 0) {
            staffMessage.setId(generatedId);
        }
        return staffMessage;
    }

    /**
     * Retrieves a staff message by its primary key ID.
     * <p>
     * This method returns a {@link StaffMessage} entity by joining with the underlying message data.
     * </p>
     *
     * @param id The primary key (staffMessageID) of the staff message.
     * @return The {@link StaffMessage} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public StaffMessage getById(int id) {
        String sql = baseJoinQuery + " WHERE sm.staffMessageID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all staff messages from the "StaffMessages" table.
     * <p>
     * This method returns a list of all {@link StaffMessage} entities stored in the database.
     * </p>
     *
     * @return A {@code List} of {@link StaffMessage} objects.
     */
    @Override
    public List<StaffMessage> getAll() {
        return queryForList(baseJoinQuery, pstmt -> {
        }, this::build);
    }

    /**
     * Builds a {@link StaffMessage} object from the current row of the provided {@link ResultSet}.
     * <p>
     * Maps the columns from the SQL query into a fully populated {@link StaffMessage} entity.
     * </p>
     *
     * @param rs The {@link ResultSet} containing staff message data.
     * @return The built {@link StaffMessage} object.
     * @throws SQLException If an error occurs while mapping columns.
     */
    @Override
    public StaffMessage build(ResultSet rs) throws SQLException {
        StaffMessage sm = new StaffMessage();
        sm.setId(rs.getInt("staffMessageID"));

        // Build the underlying Message.
        Message m = new Message();
        m.setId(rs.getInt("msg_id"));
        m.setUserId(rs.getInt("msg_userID"));
        m.setContent(rs.getString("msg_content"));
        m.setCreatedAt(rs.getTimestamp("msg_createdAt"));
        sm.setMessage(m);

        // Retrieve the user entity.
        int userId = rs.getInt("sm_userID");
        User user = usersRepo.getById(userId);
        sm.setUser(user);

        // Retrieve the staff entity.
        int staffId = rs.getInt("sm_staffID");
        User staff = usersRepo.getById(staffId);
        sm.setStaff(staff);

        return sm;
    }

    /**
     * Updates an existing staff message entry.
     * <p>
     * This method validates and updates the underlying message content as well as the user/staff references in the database.
     * </p>
     *
     * @param staffMessage The updated {@link StaffMessage} entity.
     * @return The updated {@link StaffMessage} object if the update was successful, or {@code null} if no rows were affected.
     * @throws IllegalArgumentException If the provided staff message fails validation.
     */
    @Override
    public StaffMessage update(StaffMessage staffMessage) throws IllegalArgumentException {
        EntityValidator.validateStaffMessage(staffMessage);

        // Update underlying message content.
        messagesRepo.update(staffMessage.getMessage());

        // Update user and staff references.
        String sql = "UPDATE StaffMessages SET userID = ?, staffID = ? WHERE staffMessageID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, staffMessage.getUser().getId());
            pstmt.setInt(2, staffMessage.getStaff().getId());
            pstmt.setInt(3, staffMessage.getId());
        });
        return rows > 0 ? staffMessage : null;
    }

    /**
     * Deletes a staff message from the "StaffMessages" table.
     * <p>
     * The associated underlying {@link Message} will be removed automatically via cascading delete rules if configured.
     * </p>
     *
     * @param id The primary key (staffMessageID) of the staff message to delete.
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM StaffMessages WHERE staffMessageID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Retrieves all unique users that a given staff member is chatting with.
     * <p>
     * This method fetches distinct {@link User} entities representing users involved in a chat with the specified staff member.
     * </p>
     *
     * @param staffId The unique identifier of the staff member.
     * @return A {@code List} of unique {@link User} objects.
     */
    public List<User> getUniqueChats(int staffId) {
        String sql = "SELECT DISTINCT userID FROM StaffMessages WHERE staffID = ?";
        List<Integer> userIds = queryForList(sql, pstmt -> pstmt.setInt(1, staffId), rs -> rs.getInt("userID"));
        List<User> users = new ArrayList<>();
        for (Integer uId : userIds) {
            User u = usersRepo.getById(uId);
            if (u != null) {
                users.add(u);
            }
        }
        return users;
    }

    /**
     * Loads the complete chat history between a specific user and a staff member.
     * <p>
     * The messages are ordered chronologically (oldest first) based on the underlying message creation time.
     * </p>
     *
     * @param userId  The identifier of the user.
     * @param staffId The identifier of the staff member.
     * @return A {@code List} of {@link StaffMessage} objects representing the chat history.
     */
    public List<StaffMessage> loadChat(int userId, int staffId) {
        String sql = baseJoinQuery +
                " WHERE sm.userID = ? AND sm.staffID = ? " +
                " ORDER BY m.createdAt ASC";
        return queryForList(sql, pstmt -> {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, staffId);
        }, this::build);
    }

    /**
     * Sends a new message between a user and a staff member.
     * <p>
     * This method creates a new {@link StaffMessage} based on the provided {@link Message} entity,
     * sender identifier, and staff identifier, then persists it in the database.
     * The provided {@code message} will have its sender (userId) set to the provided {@code userId}.
     * </p>
     *
     * @param message The {@link Message} entity containing the message content.
     * @param userId  The identifier of the sender (user or staff) for the message.
     * @param staffId The identifier of the staff member associated with the chat.
     * @return The newly created {@link StaffMessage} object.
     */
    public StaffMessage sendMessage(Message message, int userId, int staffId) {
        StaffMessage sm = new StaffMessage();
        sm.setMessage(message);

        // Retrieve user and staff entities.
        User userObj = usersRepo.getById(userId);
        User staffObj = usersRepo.getById(staffId);
        sm.setUser(userObj);
        sm.setStaff(staffObj);

        return create(sm);
    }
}