package application.framework;

import database.model.entities.User;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

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
        if (currentRole == null) {
            Roles[] userRoles = RolesUtil.intToRoles(getActiveUser().getRoles());
            return userRoles[0];
        }

        return currentRole;
    }

    public void setCurrentRole(Roles role) {
        currentRole = role;
    }
}