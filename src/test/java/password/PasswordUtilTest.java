package password;

import org.junit.jupiter.api.Test;
import utils.PasswordUtil;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PasswordUtil} class.
 * <p>
 * This test class verifies the correct functionality of the password hashing and verification
 * methods in {@link PasswordUtil} by checking various scenarios such as valid hashes,
 * incorrect passwords, differing salts, and invalid hash formats.
 *
 * @author Dhruv
 * @see PasswordUtil
 */
public class PasswordUtilTest {

    /**
     * Tests the {@code hashPassword} method to ensure that the generated hash contains a valid salt
     * and hash separated by a single '$' delimiter. Also verifies that the salt and hash are valid
     * Base64 strings.
     */
    @Test
    public void testHashFormat() {
        String password = "password123";
        String hashed = PasswordUtil.hashPassword(password);

        // Check that the hashed password contains exactly one '$'
        String[] parts = hashed.split("\\$");
        assertEquals(2, parts.length, "Hashed password should contain exactly one '$' delimiter.");

        // Validate that both parts are valid Base64 strings
        try {
            Base64.getDecoder().decode(parts[0]);
            Base64.getDecoder().decode(parts[1]);
        } catch (IllegalArgumentException e) {
            fail("Either the salt or the hash is not valid Base64.");
        }
    }

    /**
     * Tests the {@code verifyPassword} method to ensure successful verification when the
     * correct password is provided.
     */
    @Test
    public void testVerifyPasswordSuccess() {
        String password = "securePassword!";
        String hashed = PasswordUtil.hashPassword(password);

        // Should return true when verifying with the correct password
        assertTrue(PasswordUtil.verifyPassword(hashed, password),
                "Password verification should succeed for the correct password.");
    }

    /**
     * Tests the {@code verifyPassword} method to ensure failure when an incorrect password
     * is provided.
     */
    @Test
    public void testVerifyPasswordFailure() {
        String password = "securePassword!";
        String wrongPassword = "wrongPassword!";
        String hashed = PasswordUtil.hashPassword(password);

        // Should return false when the password is incorrect
        assertFalse(PasswordUtil.verifyPassword(hashed, wrongPassword),
                "Password verification should fail for an incorrect password.");
    }

    /**
     * Tests the {@code hashPassword} method to ensure that different salts are used
     * for the same password across different hashing attempts, resulting in different hashes.
     */
    @Test
    public void testDifferentSalts() {
        String password = "repeatedPassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        // The two hashes should differ because they use different salts.
        assertNotEquals(hash1, hash2, "Hashes should be different due to different salts.");
    }

    /**
     * Tests the {@code verifyPassword} method to ensure that an invalid hash format
     * triggers an {@link IllegalArgumentException}.
     */
    @Test
    public void testVerifyPasswordInvalidFormat() {
        // An invalid hash format should trigger an IllegalArgumentException.
        String invalidHash = "invalidFormatHashWithoutDelimiter";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verifyPassword(invalidHash, "anyPassword");
        });
        String expectedMessage = "Invalid stored password format";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage),
                "Exception message should indicate an invalid stored password format.");
    }
}