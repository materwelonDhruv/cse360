package validators;

public class PasswordValidator {

    private static final String SPECIAL_CHARS = "!@#$%&";

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

    private enum State {START, PROCESSING, DONE}
}