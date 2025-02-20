package src.validators;

public class NameValidator {

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

    private enum State {START, VALID, ERROR}
}
