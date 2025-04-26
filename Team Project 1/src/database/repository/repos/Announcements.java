package database.repository.repos;

import database.model.entities.Announcement;
import database.model.entities.Message;
import database.repository.Repository;
import validators.EntityValidator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for managing {@link Announcement} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "Announcements" table,
 * including creating, retrieving, updating, and deleting announcements. It also joins with the
 * underlying {@link Message} table to store and load message content.
 * </p>
 *
 * @author Dhruv
 */
public class Announcements extends Repository<Announcement> {
    private final Messages messagesRepo;

    private final String baseJoinQuery =
            "SELECT a.announcementID, a.title, " +
                    "       m.messageID, m.userID, m.content, m.createdAt " +
                    "FROM Announcements a " +
                    "JOIN Messages m ON a.messageID = m.messageID ";

    /**
     * Constructs the Announcements repository with the specified database connection.
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during initialization.
     */
    public Announcements(Connection connection) throws SQLException {
        super(connection);
        this.messagesRepo = new Messages(connection);
    }

    /**
     * Creates a new announcement in the "Announcements" table, including inserting the underlying message.
     *
     * @param announcement The {@link Announcement} entity to create.
     * @return The created {@link Announcement} with its primary key set.
     * @throws IllegalArgumentException If the announcement fails validation.
     */
    @Override
    public Announcement create(Announcement announcement) throws IllegalArgumentException {
        EntityValidator.validateMessage(announcement.getMessage());

        // Create the underlying message
        Message createdMsg = messagesRepo.create(announcement.getMessage());
        announcement.getMessage().setId(createdMsg.getId());

        // Insert into Announcements table
        String sql = "INSERT INTO Announcements (messageID, title) VALUES (?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setInt(1, announcement.getMessage().getId());
            pstmt.setString(2, announcement.getTitle());
        });
        if (generatedId > 0) {
            announcement.setId(generatedId);
        }
        return announcement;
    }

    /**
     * Retrieves an announcement by its primary key (announcementID).
     *
     * @param id The announcementID.
     * @return The {@link Announcement} entity, or null if not found.
     */
    @Override
    public Announcement getById(int id) {
        String sql = baseJoinQuery + "WHERE a.announcementID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all announcements from the database.
     *
     * @return A list of all {@link Announcement} entities.
     */
    @Override
    public List<Announcement> getAll() {
        return queryForList(baseJoinQuery, pstmt -> {
        }, this::build);
    }

    /**
     * Builds an {@link Announcement} entity from a {@link ResultSet}.
     *
     * @param rs The result set containing the announcement data.
     * @return The built {@link Announcement}.
     * @throws SQLException If an error occurs while reading from the result set.
     */
    @Override
    public Announcement build(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setId(rs.getInt("announcementID"));
        announcement.setTitle(rs.getString("title"));

        Message msg = new Message();
        msg.setId(rs.getInt("messageID"));
        msg.setUserId(rs.getInt("userID"));
        msg.setContent(rs.getString("content"));
        msg.setCreatedAt(rs.getTimestamp("createdAt"));

        announcement.setMessage(msg);
        return announcement;
    }

    /**
     * Updates the title and message content of an existing announcement.
     *
     * @param announcement The updated {@link Announcement}.
     * @return The updated announcement, or null if no rows were affected.
     * @throws IllegalArgumentException If the announcement fails validation.
     */
    @Override
    public Announcement update(Announcement announcement) throws IllegalArgumentException {
        EntityValidator.validateMessage(announcement.getMessage());

        // Update the message content
        messagesRepo.update(announcement.getMessage());

        // Update the title in Announcements
        String sql = "UPDATE Announcements SET title = ? WHERE announcementID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setString(1, announcement.getTitle());
            pstmt.setInt(2, announcement.getId());
        });

        return rows > 0 ? announcement : null;
    }

    /**
     * Deletes an announcement by its ID. The underlying message is removed via ON DELETE CASCADE.
     *
     * @param id The announcementID to delete.
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Announcements WHERE announcementID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }
}