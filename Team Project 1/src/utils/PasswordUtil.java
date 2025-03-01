package utils;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;      // 32 bytes = 256 bits
    private static final int ITERATIONS = 3;
    private static final int MEMORY_COST = 65536;     // in kilobytes (64 MB)
    private static final int PARALLELISM = 1;

    public static String hashPassword(String plainPassword) {
        // Generate a new salt for this password
        PasswordUtil util = new PasswordUtil();
        byte[] salt = util.generateSalt();

        // Set up Argon2 parameters
        Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_COST)
                .withParallelism(PARALLELISM)
                .build();

        // Generate the hash
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parameters);
        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(plainPassword.getBytes(StandardCharsets.UTF_8), hash);

        // Encode salt and hash using Base64 and concatenate with a delimiter
        return Base64.getEncoder().encodeToString(salt)
                + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String hashed, String plain) {
        // The stored hashed password should be in the format "salt$hash"
        String[] parts = hashed.split("\\$");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid stored password format.");
        }

        // Decode the Base64 encoded salt and stored hash
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);

        // Recreate the Argon2 parameters using the original salt
        Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_COST)
                .withParallelism(PARALLELISM)
                .build();

        // Generate the hash for the provided plain text password
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parameters);
        byte[] computedHash = new byte[storedHash.length];
        generator.generateBytes(plain.getBytes(StandardCharsets.UTF_8), computedHash);

        // Constant-time comparison to prevent timing attacks
        int diff = 0;
        for (int i = 0; i < storedHash.length; i++) {
            diff |= storedHash[i] ^ computedHash[i];
        }
        return diff == 0;
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
}