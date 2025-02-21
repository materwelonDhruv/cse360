package src.tests.password;

import org.junit.jupiter.api.*;
import src.validators.PasswordValidator;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordValidatorTest {

    @Test
    @Order(1)
    public void testEmptyPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("");
        }, "Should throw for empty password");
    }

    @Test
    @Order(2)
    public void testValidPassword() {
        Assertions.assertDoesNotThrow(() -> {
            PasswordValidator.validatePassword("Abcdef1!");
        }, "Password should be valid");
    }

    @Test
    @Order(3)
    public void testMissingUppercase() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("abcdef1!");
        }, "Should throw for missing uppercase");
    }

    @Test
    @Order(4)
    public void testMissingLowercase() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("ABCDEF1!");
        }, "Should throw for missing lowercase");
    }

    @Test
    @Order(5)
    public void testMissingDigit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("Abcdefg!");
        }, "Should throw for missing numeric digit");
    }

    @Test
    @Order(6)
    public void testMissingSpecial() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("Abcdef12");
        }, "Should throw for missing special character");
    }

    @Test
    @Order(7)
    public void testTooShortPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("Ab1!");
        }, "Should throw for password too short");
    }
}