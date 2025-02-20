package src.validators;

import src.database.model.entities.Answer;
import src.database.model.entities.Question;

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
        if (question.getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for a question.");
        }
        if (question.getTitle() == null || question.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Question title cannot be empty.");
        }
        if (question.getContent() == null || question.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Question content cannot be empty.");
        }

        validateLength(question.getTitle(), MIN_TITLE_LENGTH, MAX_TITLE_LENGTH, "Question title must be between 5 and 100 characters.");
        validateLength(question.getContent(), MIN_CONTENT_LENGTH, MAX_CONTENT_LENGTH, "Question content must be between 10 and 2000 characters.");
    }

    /**
     * Validates an Answer object. Throws IllegalArgumentException if invalid.
     */
    public static void validateAnswer(Answer answer) throws IllegalArgumentException {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null.");
        }
        if (answer.getUserId() <= 0) {
            throw new IllegalArgumentException("A valid userID is required for an answer.");
        }
        if (answer.getContent() == null || answer.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Answer content cannot be empty.");
        }
        // Must reference exactly one of questionId or parentAnswerId
        boolean hasQuestion = (answer.getQuestionId() != null);
        boolean hasParent = (answer.getParentAnswerId() != null);
        if (hasQuestion && hasParent) {
            throw new IllegalArgumentException("An answer must reference either a question OR another answer, not both.");
        }
        if (!hasQuestion && !hasParent) {
            throw new IllegalArgumentException("An answer must reference either a question OR another answer.");
        }

        validateLength(answer.getContent(), MIN_CONTENT_LENGTH, MAX_CONTENT_LENGTH, "Answer content must be between 10 and 2000 characters.");
    }

    /**
     * Private method to validate the length of a string.
     */
    private static void validateLength(String value, int min, int max, String message) {
        if (value == null || value.length() < min || value.length() > max) {
            throw new IllegalArgumentException(message);
        }
    }
}
