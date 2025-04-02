package application.framework;

import database.model.entities.User;
import utils.permissions.Roles;

/**
 * Holds application-wide session info
 */
public class Session {

    private User activeUser;
    private Roles currentRole;

    public Session() {
    }

    public User getActiveUser() {

        if (activeUser == null) {
            throw new IllegalStateException("No active user");
        }

        return activeUser;
    }

    public void setActiveUser(User user) {
        activeUser = user;
    }

    public Roles getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(Roles role) {
        currentRole = role;
    }
}