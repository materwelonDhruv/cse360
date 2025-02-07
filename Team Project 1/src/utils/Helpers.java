package src.utils;

import java.security.SecureRandom;

public class Helpers {
    public static int getCurrentTimeInSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static String generateRandomCode(int length, boolean highSecurity) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (highSecurity) {
            chars += "!@#$%^&*()_+-=[]{}|;:,.<>?";
        }
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
