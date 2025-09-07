package password;

import org.junit.jupiter.api.*;
import validators.PasswordValidator;

/**
 * Unit tests for the {@link PasswordValidator} class.
 * <p>
 * This test class verifies the correct functionality of the {@code validatePassword} method
 * by checking various valid and invalid password inputs according to defined criteria.
 * </p>
 *
 * @author Dhruv
 * @see PasswordValidator
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordValidatorTest {

    /**
     * Tests the {@code validatePassword} method with an empty password string.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(1)
    public void testEmptyPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("");
        }, "Should throw for empty password");
    }

    /**
     * Tests the {@code validatePassword} method with a valid password.
     * Ensures no exception is thrown for a properly formatted password.
     */
    @Test
    @Order(2)
    public void testValidPassword() {
        Assertions.assertDoesNotThrow(() -> {
            PasswordValidator.validatePassword("Abcdef1!");
        }, "Password should be valid");
    }

    /**
     * Tests the {@code validatePassword} method with a password missing an uppercase letter.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(3)
    public void testMissingUppercase() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("abcdef1!");
        }, "Should throw for missing uppercase");
    }

    /**
     * Tests the {@code validatePassword} method with a password missing a lowercase letter.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(4)
    public void testMissingLowercase() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("ABCDEF1!");
        }, "Should throw for missing lowercase");
    }

    /**
     * Tests the {@code validatePassword} method with a password missing a numeric digit.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(5)
    public void testMissingDigit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("Abcdefg!");
        }, "Should throw for missing numeric digit");
    }

    /**
     * Tests the {@code validatePassword} method with a password missing a special character.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(6)
    public void testMissingSpecial() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("Abcdef12");
        }, "Should throw for missing special character");
    }

    /**
     * Tests the {@code validatePassword} method with a password that is too short.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(7)
    public void testTooShortPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("Ab1!");
        }, "Should throw for password too short");
    }
}