package validators;

/**
 * Provides methods for validating email strings based on standard formatting rules.
 * <p>
 * The email must contain a valid local part, an '@' symbol, and a valid domain with at least one dot.
 * </p>
 *
 * @author Dhruv
 */
public class EmailValidator {

    /**
     * Validates the provided email string according to standard email format rules.
     *
     * <ul>
     *   <li>The email must contain a valid local part consisting of alphanumeric characters, dots, underscores, or hyphens.</li>
     *   <li>The email must include an '@' symbol separating the local part and the domain.</li>
     *   <li>The domain must consist of alphanumeric characters or hyphens, and must contain at least one dot.</li>
     * </ul>
     *
     * @param input The email string to be validated.
     * @throws IllegalArgumentException if the email is null, empty, missing an '@' symbol,
     *                                  contains invalid characters, lacks a valid domain, or if the domain does not contain a dot.
     * @see IllegalArgumentException
     */
    public static void validateEmail(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Email is empty");
        }
        State state = State.LOCAL;
        int length = input.length();
        boolean dotInDomain = false;

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (state) {
                case LOCAL:
                    if (c == '@') {
                        state = State.AT;
                    } else if (Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '-') {
                        // Remain in LOCAL
                    } else {
                        state = State.ERROR;
                    }
                    break;
                case AT:
                    if (Character.isLetterOrDigit(c)) {
                        state = State.DOMAIN;
                    } else {
                        state = State.ERROR;
                    }
                    break;
                case DOMAIN:
                    if (c == '.') {
                        dotInDomain = true;
                    } else if (Character.isLetterOrDigit(c) || c == '-') {
                        // Remain in DOMAIN
                    } else {
                        state = State.ERROR;
                    }
                    break;
                default:
                    break;
            }
            if (state == State.ERROR) {
                throw new IllegalArgumentException("Invalid email format at character: " + c);
            }
        }
        if (state == State.LOCAL) {
            throw new IllegalArgumentException("Missing '@' symbol in email");
        }
        if (state == State.AT) {
            throw new IllegalArgumentException("Domain part is empty");
        }
        if (!dotInDomain) {
            throw new IllegalArgumentException("Domain must contain at least one dot");
        }
    }

    /**
     * Enumeration representing the different states of email processing.
     *
     * <ul>
     *   <li>{@code LOCAL} - Processing the local part of the email address.</li>
     *   <li>{@code AT} - Processing the '@' symbol.</li>
     *   <li>{@code DOMAIN} - Processing the domain part of the email address.</li>
     *   <li>{@code ERROR} - When an invalid character or format is encountered.</li>
     * </ul>
     */
    private enum State {LOCAL, AT, DOMAIN, ERROR}
}