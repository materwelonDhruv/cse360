package database.model.entities;

import database.model.BaseEntity;
import utils.Helpers;

/**
 * Represents an invitation in the system, including a random invite code, associated user ID,
 * roles, and the creation timestamp.
 * <p>
 * This class generates a unique invite code and associates it with a user and roles. It also stores
 * the creation timestamp in seconds.
 * </p>
 *
 * @author Dhruv
 * @see Helpers
 */
public class Invite extends BaseEntity {
    private String code;
    private Integer userId; // The user who created the invite
    private int roles;      // Bit field for roles associated with this invite
    private long createdAt; // Unix timestamp representing when the invite was created

    /**
     * Default constructor for {@code Invite}.
     * Initializes a new instance of the Invite class with a random code, default roles,
     * and the current timestamp.
     */
    public Invite() {
        this.code = Helpers.generateRandomCode(6, false);
        this.roles = 0;
        this.createdAt = Helpers.getCurrentTimeInSeconds();
    }

    /**
     * Creates a new invite with a random code, specified user ID, default roles, and the current timestamp.
     *
     * @param userId The user who created the invite.
     */
    public Invite(Integer userId) {
        this.code = Helpers.generateRandomCode(6, false);
        this.userId = userId;
        this.roles = 0;
        this.createdAt = Helpers.getCurrentTimeInSeconds();
    }

    /**
     * Gets the unique invite code.
     *
     * @return The invite code as a {@code String}.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the invite code.
     *
     * @param code The new invite code.
     */
    public Invite setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * Gets the ID of the user who created the invite.
     *
     * @return The user ID of the invite creator.
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the user ID of the creator.
     *
     * @param userId The new user ID of the invite creator.
     */
    public Invite setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Gets the roles associated with the invite.
     *
     * @return The roles as an {@code int} (bit field).
     */
    public int getRoles() {
        return roles;
    }

    /**
     * Sets the roles associated with the invite.
     *
     * @param roles The new roles (bit field) to be associated with the invite.
     */
    public Invite setRoles(int roles) {
        this.roles = roles;
        return this;
    }

    /**
     * Gets the creation timestamp of the invite.
     *
     * @return The creation timestamp as a {@code long}.
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the invite.
     *
     * @param createdAt The new creation timestamp (in seconds).
     */
    public Invite setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}