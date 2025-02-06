package src.tests.username;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import src.validators.UsernameValidator;

public class UsernameValidatorTest {

    @Test
    public void testEmptyUsername() {
        assertEquals("Username is empty", UsernameValidator.validateUserName(""));
    }

    @Test
    public void testTooShortUsername() {
        assertEquals("Username must be at least 6 characters", UsernameValidator.validateUserName("abc12"));
    }

    @Test
    public void testTooLongUsername() {
        String longUsername = "a".repeat(19);
        assertEquals("Username must be no more than 18 characters", UsernameValidator.validateUserName(longUsername));
    }

    @Test
    public void testInvalidCharacterInUsername() {
        String result = UsernameValidator.validateUserName("validUser!");
        assertTrue(result.contains("Invalid character"), "Should detect an invalid character");
    }

    @Test
    public void testValidUsername() {
        assertEquals("", UsernameValidator.validateUserName("valid_user.123"), "Username should be valid");
    }
}