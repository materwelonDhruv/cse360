package utils;

import java.security.SecureRandom;
import java.sql.Timestamp;

/**
 * This utility class provides general-purpose helper methods for various functionalities such as
 * generating random codes and retrieving the current timestamp.
 *
 * @author Dhruv
 */
public class Helpers {

    /**
     * Retrieves the current time in seconds since the epoch (January 1, 1970, 00:00:00 UTC).
     *
     * @return The current time as an integer representing seconds since the epoch.
     */
    public static int getCurrentTimeInSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * Generates a random code of the specified length.
     * <p>
     * The generated code can include uppercase and lowercase letters, digits, and optionally special characters.
     *
     * @param length       The length of the random code to be generated.
     * @param highSecurity If true, special characters {@literal !@#$%&} will be included in the code.
     * @return A randomly generated code of the specified length.
     */
    public static String generateRandomCode(int length, boolean highSecurity) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (highSecurity) {
            chars += "!@#$%&";
        }
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Method converts provided {@link Timestamp} to a human-readable string format:
     * "h:mm a d MMM, yyyy"
     *
     * @param timestamp The {@link Timestamp} to be converted.
     * @return A formatted string representing the timestamp, including an AM/PM marker.
     */
    public static String formatTimestamp(Timestamp timestamp) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("h:mm a d MMM, yyyy");
        return sdf.format(timestamp);
    }
}