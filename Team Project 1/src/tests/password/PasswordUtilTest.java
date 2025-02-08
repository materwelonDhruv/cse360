package src.tests.password;

import org.junit.jupiter.api.Test;
import src.utils.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

public class PasswordUtilTest {

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

    @Test
    public void testVerifyPasswordSuccess() {
        String password = "securePassword!";
        String hashed = PasswordUtil.hashPassword(password);

        // Should return true when verifying with the correct password
        assertTrue(PasswordUtil.verifyPassword(hashed, password),
                "Password verification should succeed for the correct password.");
    }

    @Test
    public void testVerifyPasswordFailure() {
        String password = "securePassword!";
        String wrongPassword = "wrongPassword!";
        String hashed = PasswordUtil.hashPassword(password);

        // Should return false when the password is incorrect
        assertFalse(PasswordUtil.verifyPassword(hashed, wrongPassword),
                "Password verification should fail for an incorrect password.");
    }

    @Test
    public void testDifferentSalts() {
        String password = "repeatedPassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        // The two hashes should differ because they use different salts.
        assertNotEquals(hash1, hash2, "Hashes should be different due to different salts.");
    }

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