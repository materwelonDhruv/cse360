package database.model.entities;

import database.model.BaseEntity;
import utils.requests.AdminActions;
import utils.requests.RequestState;

/**
 * Represents an administrative request initiated by one user against another.
 * <p>
 * Fields:
 * <ul>
 *   <li>requester: the User who made the request</li>
 *   <li>target:    the User who is the target of the request</li>
 *   <li>type:      the action being requested ({@link AdminActions})</li>
 *   <li>state:     the current state of the request ({@link RequestState})</li>
 *   <li>reason:    the textual reason for the request</li>
 *   <li>context:   an optional integer context (e.g., rolesInt), or null</li>
 * </ul>
 * </p>
 *
 * @author Dhruv
 * @see User
 * @see AdminActions
 * @see RequestState
 */
public class AdminRequest extends BaseEntity {
    private User requester;
    private User target;
    private AdminActions type;
    private RequestState state;
    private String reason;
    private Integer context;

    public AdminRequest() {
    }

    public AdminRequest(User requester,
                        User target,
                        AdminActions type,
                        RequestState state,
                        String reason,
                        Integer context) {
        this.requester = requester;
        this.target = target;
        this.type = type;
        this.state = state;
        this.reason = reason;
        this.context = context;
    }

    /**
     * @return the user who initiated the request
     */
    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    /**
     * @return the user who is the target of the request
     */
    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    /**
     * @return the requested action type
     */
    public AdminActions getType() {
        return type;
    }

    public void setType(AdminActions type) {
        this.type = type;
    }

    /**
     * @return the current request state
     */
    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    /**
     * @return the textual reason for the request
     */
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the optional context integer, or null
     */
    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }
}