package validators;

import database.model.entities.*;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

public class EntityValidator {
    private final static int MIN_TITLE_LENGTH = 5;
    private final static int MAX_TITLE_LENGTH = 100;
    private final static int MIN_CONTENT_LENGTH = 10;
    private final static int MAX_CONTENT_LENGTH = 2000;

    /**
     * Validates a Question object. Throws IllegalArgumentException if invalid.
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
     * Validates an Answer object. Throws IllegalArgumentException if invalid.
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
     * Private method to validate the length of a string.
     */
    private static void validateLength(String value, int min, int max, String message) {
        if (value == null || value.length() < min || value.length() > max) {
            throw new IllegalArgumentException(message);
        }
    }

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

    public static void validateMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        if (message.getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for a message.");
        }
        validateMessageContent(message.getContent());
    }

    public static void validateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
    }

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

        if (review.getRating() < 0 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }

        if (!RolesUtil.hasRole(review.getReviewer().getRoles(), Roles.REVIEWER)) {
            throw new IllegalArgumentException("Reviewers must have the REVIEWER role.");
        }
    }
}
