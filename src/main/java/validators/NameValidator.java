package validators;

/**
 * Provides methods for validating name strings according to specified criteria.
 * <p>
 * Names must start with a letter and can only contain alphabetic characters.
 * </p>
 *
 * @author Dhruv
 */
public class NameValidator {

    /**
     * Validates the provided name string based on the following criteria:
     *
     * <ul>
     *   <li>Name must not be null or empty.</li>
     *   <li>Name must start with an alphabetic letter.</li>
     *   <li>Name can only contain alphabetic characters.</li>
     * </ul>
     *
     * @param input The name string to be validated.
     * @throws IllegalArgumentException if the name is null, empty,
     *                                  starts with a non-alphabetic character, or contains invalid characters.
     * @see IllegalArgumentException
     */
    public static void validateName(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Name is empty");
        }
        int length = input.length();
        State state = State.START;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (state) {
                case START:
                    if (Character.isLetter(c)) {
                        state = State.VALID;
                    } else {
                        throw new IllegalArgumentException("Name must start with a letter");
                    }
                    break;
                case VALID:
                    if (Character.isLetter(c)) {
                        // Remain in VALID state.
                    } else {
                        state = State.ERROR;
                    }
                    break;
                default:
                    break;
            }
            if (state == State.ERROR) {
                throw new IllegalArgumentException("Invalid character in name: " + c);
            }
        }
    }

    /**
     * Enumeration representing the different states of name validation processing.
     *
     * <ul>
     *   <li>{@code START} - Initial state before processing.</li>
     *   <li>{@code VALID} - While the name is valid and matches criteria.</li>
     *   <li>{@code ERROR} - When an invalid character is encountered.</li>
     * </ul>
     */
    private enum State {START, VALID, ERROR}
}
