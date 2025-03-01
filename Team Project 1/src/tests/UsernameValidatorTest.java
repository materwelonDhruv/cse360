package tests;

import org.junit.jupiter.api.*;
import validators.UsernameValidator;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsernameValidatorTest {

    @Test
    @Order(1)
    public void testEmptyUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName("");
        }, "Should throw for empty username");
    }

    @Test
    @Order(2)
    public void testTooShortUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName("abc12");
        }, "Should throw for too short username");
    }

    @Test
    @Order(3)
    public void testTooLongUsername() {
        String longUsername = "a".repeat(19);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName(longUsername);
        }, "Should throw for too long username");
    }

    @Test
    @Order(4)
    public void testInvalidCharacterInUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName("validUser!");
        }, "Should throw for invalid character");
    }

    @Test
    @Order(5)
    public void testValidUsername() {
        Assertions.assertDoesNotThrow(() -> {
            UsernameValidator.validateUserName("valid_user.123");
        }, "Username should be valid");
    }
}