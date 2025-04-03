package application.framework;

/**
 * Enum of all page routes. Each entry has a string route.
 */
public enum MyPages {
    START("/start"),
    ADMIN_HOME("/adminHome"),
    ADMIN_SETUP("/adminSetup"),
    ADMIN_USER("/adminUser"),
    ADMIN_USER_MODIFY("/adminUserModify"),
    INVITATION("/invitation"),
    RESET_PASSWORD("/resetPassword"),
    SET_PASS("/setPass"),
    FIRST("/first"),
    SETUP_LOGIN("/setupLogin"),
    SETUP_ACCOUNT("/setupAccount"),
    WELCOME_LOGIN("/welcomeLogin"),
    USER_HOME("/userHome"),
    USER_LOGIN("/userLogin"),
    USER_QUESTION_DISPLAY("/userQuestionDisplay"),
    PRIVATE_MESSAGE("/privateMessagePage"),
    PRIVATE_CONVERSATION("/privateConversation"),
    INSTRUCTOR_HOME("/instructorHome"),
    REVIEW_HOME("/reviewHome");

    private final String route;

    MyPages(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}