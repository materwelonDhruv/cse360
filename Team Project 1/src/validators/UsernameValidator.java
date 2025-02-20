package src.validators;

public class UsernameValidator {
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

    private enum State {START, VALID, ERROR}
}