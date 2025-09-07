package validators;

/**
 * Provides methods for validating password strings according to specified criteria.
 * <p>
 * Criteria include length, uppercase letters, lowercase letters, numeric digits,
 * special characters, and avoidance of invalid characters.
 * </p>
 *
 * @author Dhruv
 */
public class PasswordValidator {

    private static final String SPECIAL_CHARS = "!@#$%&";

    /**
     * Validates the provided password string against a set of defined criteria.
     *
     * <ul>
     *   <li>Password must be at least 8 characters long.</li>
     *   <li>Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character from: {@literal !@#$%&}.</li>
     *   <li>Password must not contain any invalid characters.</li>
     * </ul>
     *
     * @param input The password string to be validated.
     * @throws IllegalArgumentException if the password is invalid for any reason, including:
     *                                  <ul>
     *                                    <li>Being null or empty.</li>
     *                                    <li>Containing invalid characters.</li>
     *                                    <li>Not meeting any of the specified criteria.</li>
     *                                  </ul>
     * @see IllegalArgumentException
     */
    public static void validatePassword(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Password is empty");
        }

        State state = State.START;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        int length = input.length();

        state = State.PROCESSING;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                hasUpper = true;
            } else if (c >= 'a' && c <= 'z') {
                hasLower = true;
            } else if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (SPECIAL_CHARS.indexOf(c) >= 0) {
                hasSpecial = true;
            } else {
                throw new IllegalArgumentException("Password contains invalid character: " + c);
            }
        }
        state = State.DONE;

        if (length < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        StringBuilder errors = new StringBuilder();
        if (!hasUpper) errors.append("Missing uppercase letter. ");
        if (!hasLower) errors.append("Missing lowercase letter. ");
        if (!hasDigit) errors.append("Missing numeric digit. ");
        if (!hasSpecial) errors.append("Missing special character. ");

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString().trim());
        }
    }

    /**
     * Enumeration representing the different states of password processing.
     *
     * <ul>
     *   <li>{@code START} - Initial state before processing.</li>
     *   <li>{@code PROCESSING} - During password validation processing.</li>
     *   <li>{@code DONE} - After validation is completed.</li>
     * </ul>
     */
    private enum State {START, PROCESSING, DONE}
}