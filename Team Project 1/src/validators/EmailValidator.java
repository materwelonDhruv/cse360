package src.validators;

public class EmailValidator {

    private enum State { LOCAL, AT, DOMAIN, ERROR }

    public static String validateEmail(String input) {
        if (input == null || input.isEmpty()) {
            return "Email is empty";
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
                return "Invalid email format at character: " + c;
            }
        }
        if (state == State.LOCAL) {
            return "Missing '@' symbol in email";
        }
        if (state == State.AT) {
            return "Domain part is empty";
        }
        if (!dotInDomain) {
            return "Domain must contain at least one dot";
        }
        return "";
    }
}