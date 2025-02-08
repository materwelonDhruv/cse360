package src.validators;

public class UsernameValidator {
    public static String validateUserName(String input) {
        if (input == null || input.isEmpty()) {
            return "Username is empty";
        }
        int length = input.length();
        if (length < 6) {
            return "Username must be at least 6 characters";
        }
        if (length > 18) {
            return "Username must be no more than 18 characters";
        }
        State state = State.START;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (state) {
                case START:
                    if (Character.isLetter(c)) {
                        state = State.VALID;
                    } else {
                        return "Username must start with a letter";
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
                return "Invalid character in username: " + c;
            }
        }
        return "";
    }

    private enum State {START, VALID, ERROR}
}