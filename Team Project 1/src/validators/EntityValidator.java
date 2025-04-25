package validators;

import database.model.entities.*;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import utils.requests.AdminActions;

/**
 * Provides validation methods for various entities within the system.
 * <p>
 * This class includes validation mechanisms for entities such as Question, Answer, PrivateMessage,
 * Message, Review, and ReviewerRequest, etc. It ensures required fields are present and valid according
 * to specified criteria.
 * </p>
 *
 * @author Dhruv
 */
public class EntityValidator {
    private final static int MIN_TITLE_LENGTH = 5;
    private final static int MAX_TITLE_LENGTH = 100;
    private final static int MIN_CONTENT_LENGTH = 10;
    private final static int MAX_CONTENT_LENGTH = 2000;

    /**
     * Validates a Question object.
     *
     * @param question The Question object to validate.
     * @throws IllegalArgumentException if the question is null, lacks a valid userId,
     *                                  or fails validation for title or content length.
     */
    public static void validateQuestion(Question question) throws IllegalArgumentException {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null.");
        }
        if (question.getMessage().getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for a question.");
        }
        if (question.getTitle() == null || question.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Question title cannot be empty.");
        }
        validateMessageContent(question.getMessage().getContent());

        validateLength(question.getTitle(), MIN_TITLE_LENGTH, MAX_TITLE_LENGTH, "Question title must be between 5 and 100 characters.");
        validateLength(question.getMessage().getContent(), MIN_CONTENT_LENGTH, MAX_CONTENT_LENGTH, "Question content must be between 10 and 2000 characters.");
    }

    /**
     * Validates an Answer object.
     *
     * @param answer The Answer object to validate.
     * @throws IllegalArgumentException if the answer is null, lacks a valid userId,
     *                                  references both a question and an answer, references neither, or has invalid content length.
     */
    public static void validateAnswer(Answer answer) throws IllegalArgumentException {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null.");
        }
        if (answer.getMessage().getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for an answer.");
        }
        validateMessageContent(answer.getMessage().getContent());
        // Must reference exactly one of questionId or parentAnswerId
        boolean hasQuestion = (answer.getQuestionId() != null);
        boolean hasParent = (answer.getParentAnswerId() != null);
        if (hasQuestion && hasParent) {
            throw new IllegalArgumentException("An answer must reference either a question OR another answer, not both.");
        }
        if (!hasQuestion && !hasParent) {
            throw new IllegalArgumentException("An answer must reference either a question OR another answer.");
        }

        validateLength(answer.getMessage().getContent(), MIN_CONTENT_LENGTH, MAX_CONTENT_LENGTH, "Answer content must be between 10 and 2000 characters.");
    }

    /**
     * Validates the length of a provided string.
     *
     * @param value   The string to validate.
     * @param min     The minimum acceptable length.
     * @param max     The maximum acceptable length.
     * @param message The error message to be thrown if validation fails.
     * @throws IllegalArgumentException if the string is null or its length is outside the specified bounds.
     */
    private static void validateLength(String value, int min, int max, String message) {
        if (value == null || value.length() < min || value.length() > max) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates a PrivateMessage object.
     *
     * @param privateMessage The PrivateMessage object to validate.
     * @throws IllegalArgumentException if the privateMessage is null, lacks a valid userId,
     *                                  references both a question and another message, references neither, or has invalid content length.
     */
    public static void validatePrivateMessage(PrivateMessage privateMessage) {
        if (privateMessage == null) {
            throw new IllegalArgumentException("PrivateMessage cannot be null.");
        }
        if (privateMessage.getMessage().getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for a private message.");
        }
        validateMessageContent(privateMessage.getMessage().getContent());

        // Must reference exactly one of questionId or parentPrivateMessageId
        boolean hasQuestion = (privateMessage.getQuestionId() != null);
        boolean hasParent = (privateMessage.getParentPrivateMessageId() != null);
        if (hasQuestion && hasParent) {
            throw new IllegalArgumentException("A private message must reference either a question OR another private message, not both.");
        }
        if (!hasQuestion && !hasParent) {
            throw new IllegalArgumentException("A private message must reference either a question OR another private message.");
        }

        validateLength(privateMessage.getMessage().getContent(), MIN_CONTENT_LENGTH, MAX_CONTENT_LENGTH, "Private message content must be between 10 and 2000 characters.");
    }

    /**
     * Validates a Message object.
     *
     * @param message The Message object to validate.
     * @throws IllegalArgumentException if the message is null, lacks a valid userId, or has invalid content.
     */
    public static void validateMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        if (message.getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for a message.");
        }
        validateMessageContent(message.getContent());
    }

    /**
     * Validates the content of a message.
     *
     * @param content The content string to validate.
     * @throws IllegalArgumentException if the content is null or empty.
     */
    public static void validateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
    }

    /**
     * Validates a Review object.
     *
     * @param review The Review object to validate.
     * @throws IllegalArgumentException if the review is null, lacks a valid reviewer or user,
     *                                  has an invalid rating, or if the reviewer does not have the REVIEWER role.
     */
    public static void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null.");
        }

        if (review.getReviewer() == null || review.getReviewer().getId() <= 0) {
            throw new IllegalArgumentException("A valid reviewer is required for a review.");
        }

        if (review.getUser() == null || review.getUser().getId() <= 0) {
            throw new IllegalArgumentException("A valid user is required for a review.");
        }

        if (review.getRating() <= 0) {
            throw new IllegalArgumentException("Rating order must be greater than 0.");
        }

        if (!RolesUtil.hasRole(review.getReviewer().getRoles(), Roles.REVIEWER)) {
            throw new IllegalArgumentException("Reviewers must have the REVIEWER role.");
        }
    }

    /**
     * Validates a ReviewerRequest object.
     *
     * @param request The ReviewerRequest object to validate.
     * @throws IllegalArgumentException if the request is null, the requester is invalid,
     *                                  or if the instructor is invalid or lacks the INSTRUCTOR role.
     */
    public static void validateReviewerRequest(ReviewerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("ReviewerRequest cannot be null.");
        }
        if (request.getRequester() == null || request.getRequester().getId() <= 0) {
            throw new IllegalArgumentException("A valid requester is required.");
        }
        if (request.getInstructor() != null) {
            if (request.getInstructor().getId() <= 0) {
                throw new IllegalArgumentException("A valid instructor is required.");
            }
            if (!RolesUtil.hasRole(request.getInstructor().getRoles(), Roles.INSTRUCTOR)) {
                throw new IllegalArgumentException("The instructor user must have the INSTRUCTOR role.");
            }
        }
    }

    /**
     * Validates a StaffMessage object.
     *
     * @param staffMessage The StaffMessage object to validate.
     * @throws IllegalArgumentException if the staffMessage or its internal fields are invalid.
     */
    public static void validateStaffMessage(StaffMessage staffMessage) {
        if (staffMessage == null) {
            throw new IllegalArgumentException("StaffMessage cannot be null.");
        }
        if (staffMessage.getMessage() == null) {
            throw new IllegalArgumentException("StaffMessage must contain a valid Message object.");
        }
        if (staffMessage.getUser() == null || staffMessage.getUser().getId() <= 0) {
            throw new IllegalArgumentException("A valid user is required for a staff message.");
        }
        if (staffMessage.getStaff() == null || staffMessage.getStaff().getId() <= 0) {
            throw new IllegalArgumentException("A valid staff member is required for a staff message.");
        }
        // staff != user
        if (staffMessage.getUser().getId() == staffMessage.getStaff().getId()) {
            throw new IllegalArgumentException("Staff and user cannot be the same person.");
        }

        // Validate roles
        if (!RolesUtil.hasRole(staffMessage.getStaff().getRoles(), Roles.STAFF)) {
            throw new IllegalArgumentException("Staff member must have the STAFF role.");
        }

        // Reuse existing message validations
        validateMessage(staffMessage.getMessage());
    }

    /**
     * Validates an {@link AdminRequest} object.
     * <p>
     * Ensures that requester and target are non-null with valid IDs,
     * that type and state enums are set,
     * that reason is non-empty,
     * and if the action is {@link AdminActions#UpdateRole}, that a non-null context (role ID) is provided.
     * </p>
     *
     * @param req the AdminRequest to validate
     * @throws IllegalArgumentException if any required field is missing or invalid
     */
    public static void validateAdminRequest(AdminRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("AdminRequest cannot be null.");
        }
        if (req.getRequester() == null || req.getRequester().getId() <= 0) {
            throw new IllegalArgumentException("A valid requester is required.");
        }
        if (req.getTarget() == null || req.getTarget().getId() <= 0) {
            throw new IllegalArgumentException("A valid target user is required.");
        }
        if (req.getType() == null) {
            throw new IllegalArgumentException("Admin action type must be specified.");
        }
        if (req.getState() == null) {
            throw new IllegalArgumentException("Request state must be specified.");
        }
        if (req.getReason() == null || req.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty.");
        }
        // If action is UpdateRole, context (new rolesInt) must be present
        if (req.getType() == AdminActions.UpdateRole
                && req.getContext() == null) {
            throw new IllegalArgumentException("Context (rolesInt) is required for UpdateRole requests.");
        }

        // If requester is not a user with the Instructor role, throw an exception
        if (req.getRequester() != null && req.getRequester().getId() > 0) {
            if (!RolesUtil.hasAnyRole(RolesUtil.intToRoles(req.getRequester().getRoles()), new Roles[]{Roles.INSTRUCTOR, Roles.ADMIN})) {
                throw new IllegalArgumentException("Requester must have the INSTRUCTOR role.");
            }
        }

        validateLength(req.getReason(), 5, 500, "AdminRequest reason must be between 5 and 500 characters.");
    }
}
