package src.validators;

public class NameValidator {

    public static String validateName(String input) {
        if (input == null || input.isEmpty()) {
            return "Name is empty";
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
                        return "Name must start with a letter";
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
                return "Invalid character in name: " + c;
            }
        }
        return "";
    }

    private enum State {START, VALID, ERROR}
}
