package application.framework;

import database.model.entities.User;

/**
 * Holds application-wide session info
 */
public final class SessionContext {

    private static User activeUser;

    private SessionContext() {
    }

    public static User getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(User user) {
        activeUser = user;
    }
}