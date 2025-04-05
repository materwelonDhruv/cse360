package validators;

/**
 * The {@code UsernameValidator} class provides a static method for validating usernames
 * based on specific criteria such as length, allowed characters, and starting character.
 *
 * <p>The username must meet the following requirements:
 * <ul>
 *   <li>It must be between 6 and 18 characters long (inclusive).</li>
 *   <li>It must start with a letter.</li>
 *   <li>It can only contain letters, digits, underscores ('_'), or periods ('.').</li>
 * </ul>
 *
 * @author Dhruv
 */
public class UsernameValidator {

    /**
     * Validates the given username string against the specified rules.
     *
     * <p>The validation process involves:
     * <ul>
     *   <li>Checking if the username is null or empty.</li>
     *   <li>Ensuring the username length is between 6 and 18 characters (inclusive).</li>
     *   <li>Verifying that the username starts with a letter.</li>
     *   <li>Ensuring all subsequent characters are letters, digits, underscores, or periods.</li>
     * </ul>
     *
     * @param input the username string to validate
     * @throws IllegalArgumentException if the username is invalid according to the rules
     * @see IllegalArgumentException
     * @since 1.0
     */
    public static void validateUserName(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Username is empty");
        }
        int length = input.length();
        if (length < 6) {
            throw new IllegalArgumentException("Username must be at least 6 characters");
        }
        if (length > 18) {
            throw new IllegalArgumentException("Username must be no more than 18 characters");
        }
        State state = State.START;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (state) {
                case START:
                    if (Character.isLetter(c)) {
                        state = State.VALID;
                    } else {
                        throw new IllegalArgumentException("Username must start with a letter");
                    }
                    break;
                case VALID:
                    if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                        // Remain in VALID state.
                    } else {
                        state = State.ERROR;
                    }
                    break;
                default:
                    break;
            }
            if (state == State.ERROR) {
                throw new IllegalArgumentException("Invalid character in username: " + c);
            }
        }
    }

    /**
     * The {@code State} enum represents the states used in the validation process.
     *
     * <ul>
     *   <li>{@link #START} - The initial state, expecting a letter as the first character.</li>
     *   <li>{@link #VALID} - The valid state, allowing letters, digits, underscores, or periods.</li>
     *   <li>{@link #ERROR} - The error state, indicating an invalid character was encountered.</li>
     * </ul>
     *
     * @since 1.0
     */
    private enum State {START, VALID, ERROR}
}