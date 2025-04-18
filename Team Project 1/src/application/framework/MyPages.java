package application.framework;

/**
 * Enum representing all the page routes in the application.
 * <p>
 * Each entry in this enum corresponds to a route string that is used to navigate between different pages.
 * The routes are used by the {@link PageRouter} for page navigation.
 * </p>
 *
 * @author Dhruv
 * @see PageRouter
 * @see BasePage
 * @see Route
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
    REVIEWER_PROFILE("/reviewerprofile"),
    REVIEW_HOME("/reviewHome"),
    REPLY_LIST("/replyList"),
    TRUSTED_REVIEWER("/trustedReviewer"),
    ADD_TRUSTED_REVIEWER("/addTrustedReviewer"),
    STAFF_HOME("/staffHome"),
    STAFF_PRIVATE_CHATS("/staffPrivateChats"),
    REMOVE_REVIEWER("/removeReviewer"),
    ANNOUNCEMENTS("/announcements");

    private final String route;

    /**
     * Constructor to initialize the route for each page.
     *
     * @param route The route string for the page.
     */
    MyPages(String route) {
        this.route = route;
    }

    /**
     * Returns the route associated with the page.
     *
     * @return The route string of the page.
     */
    public String getRoute() {
        return route;
    }
}