package validators;

public class EmailValidator {

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

    private enum State {LOCAL, AT, DOMAIN, ERROR}
}