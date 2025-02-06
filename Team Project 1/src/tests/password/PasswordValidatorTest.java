package src.tests.password;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import src.validators.PasswordValidator;

public class PasswordValidatorTest {

    @Test
    public void testEmptyPassword() {
        assertEquals("Password is empty", PasswordValidator.evaluatePassword(""));
    }

    @Test
    public void testValidPassword() {
        String result = PasswordValidator.evaluatePassword("Abcdef1!");
        assertEquals("", result, "Password should be valid");
    }

    @Test
    public void testMissingUppercase() {
        String result = PasswordValidator.evaluatePassword("abcdef1!");
        assertTrue(result.contains("Missing uppercase"), "Should indicate missing uppercase");
    }

    @Test
    public void testMissingLowercase() {
        String result = PasswordValidator.evaluatePassword("ABCDEF1!");
        assertTrue(result.contains("Missing lowercase"), "Should indicate missing lowercase");
    }

    @Test
    public void testMissingDigit() {
        String result = PasswordValidator.evaluatePassword("Abcdefg!");
        assertTrue(result.contains("Missing numeric"), "Should indicate missing numeric digit");
    }

    @Test
    public void testMissingSpecial() {
        String result = PasswordValidator.evaluatePassword("Abcdef12");
        assertTrue(result.contains("Missing special"), "Should indicate missing special character");
    }

    @Test
    public void testTooShortPassword() {
        String result = PasswordValidator.evaluatePassword("Ab1!");
        assertTrue(result.contains("at least 8"), "Should indicate password is too short");
    }
}