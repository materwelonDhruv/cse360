package application.framework;

import database.model.entities.User;

/**
 * Holds application-wide session info
 */
public class Session {

    private User activeUser;

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
}