package database.model.entities;

import database.model.BaseEntity;

/**
 * Represents a staff message in the system, including its associated userId, staffId, and message.
 * <p>
 * This class stores the message related to the staff message (which includes userId, content, and creation date)
 * and the two involved users: the staff member and the user.
 * </p>
 *
 * @author Dhruv
 * @see Message
 */
public class StaffMessage extends BaseEntity {
    private Message message;
    private User user;
    private User staff;

    /**
     * Default constructor for {@code StaffMessage}.
     * Initializes a new instance of the class without setting any properties.
     */
    public StaffMessage() {
    }

    /**
     * Creates a new StaffMessage with the specified message, user, and staff.
     *
     * @param message The message associated with the staff message.
     * @param user    The user involved in the staff message.
     * @param staff   The staff member involved in the staff message.
     */
    public StaffMessage(Message message, User user, User staff) {
        this.message = message;
        this.user = user;
        this.staff = staff;
    }

    /**
     * Gets the message associated with the staff message.
     *
     * @return The message related to the staff message.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the staff message.
     *
     * @param message The new message for the staff message.
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Gets the user involved in the staff message.
     *
     * @return The user associated with the staff message.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user involved in the staff message.
     *
     * @param user The new user for the staff message.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the staff member involved in the staff message.
     *
     * @return The staff member associated with the staff message.
     */
    public User getStaff() {
        return staff;
    }

    /**
     * Sets the staff member involved in the staff message.
     *
     * @param staff The new staff member for the staff message.
     */
    public void setStaff(User staff) {
        this.staff = staff;
    }
}