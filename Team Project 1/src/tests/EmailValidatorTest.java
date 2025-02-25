package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import validators.EmailValidator;

public class EmailValidatorTest {

    @Test
    public void testEmptyEmail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("");
        }, "Should throw for empty email");
    }

    @Test
    public void testMissingAtSymbol() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("user.domain.com");
        }, "Missing '@' symbol in email");
    }

    @Test
    public void testEmptyDomain() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("user@");
        }, "Domain part is empty");
    }

    @Test
    public void testDomainWithoutDot() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("user@domain");
        }, "Domain must contain at least one dot");
    }

    @Test
    public void testInvalidLocalCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("us!er@domain.com");
        }, "Should throw for invalid character in local part");
    }

    @Test
    public void testValidEmail() {
//        assertEquals("", EmailValidator.validateEmail("user.name@domain.com"), "Email should be valid");
        Assertions.assertDoesNotThrow(() -> {
            EmailValidator.validateEmail("user.name@domain.com");
        }, "Email should be valid");
    }
}