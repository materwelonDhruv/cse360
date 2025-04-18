package database.model.entities;

import database.model.BaseEntity;

/**
 * <p>
 * Represents an announcement in the system, including its associated message (content, userId, date)
 * and a title field. Each announcement is linked to exactly one {@link Message}.
 * </p>
 *
 * @author Dhruv
 */
public class Announcement extends BaseEntity {
    private Message message;
    private String title;

    /**
     * Default constructor for {@code Announcement}.
     */
    public Announcement() {
    }

    /**
     * Constructs an Announcement with a given message and title.
     *
     * @param message The {@link Message} associated with this announcement.
     * @param title   The announcement title.
     */
    public Announcement(Message message, String title) {
        this.message = message;
        this.title = title;
    }

    /**
     * Returns the underlying {@link Message} entity for this announcement.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the underlying {@link Message} entity for this announcement.
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Returns the title of the announcement.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the announcement.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}