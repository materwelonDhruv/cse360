package application.framework;

import database.model.entities.User;
import utils.permissions.Roles;

/**
 * Holds application-wide session information.
 * <p>
 * This class is responsible for storing the currently active user and their assigned role. It provides methods
 * for getting and setting the active user and the current role. This is useful for maintaining session-specific data
 * during the application's runtime.
 * </p>
 *
 * @author Dhruv
 * @see User
 * @see Roles
 */
public class Session {

    private User activeUser;
    private Roles currentRole;

    /**
     * Constructs a new {@code Session} object with no active user or role initially set.
     */
    public Session() {
    }

    /**
     * Returns the currently active user for the session.
     * <p>
     * If there is no active user set, this method throws an {@link IllegalStateException}.
     * </p>
     *
     * @return The active {@link User} for the session.
     * @throws IllegalStateException if no active user has been set.
     */
    public User getActiveUser() {

        if (activeUser == null) {
            throw new IllegalStateException("No active user");
        }

        return activeUser;
    }

    /**
     * Sets the active user for the session.
     *
     * @param user The {@link User} to set as the active user.
     */
    public void setActiveUser(User user) {
        activeUser = user;
    }

    /**
     * Returns the current role of the active user in the session.
     *
     * @return The {@link Roles} object representing the current role of the active user.
     */
    public Roles getCurrentRole() {
        return currentRole;
    }

    /**
     * Sets the current role for the active user in the session.
     *
     * @param role The {@link Roles} object to set as the current role.
     */
    public void setCurrentRole(Roles role) {
        currentRole = role;
    }
}