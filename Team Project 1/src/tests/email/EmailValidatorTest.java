package src.tests.email;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import src.validators.EmailValidator;

public class EmailValidatorTest {

    @Test
    public void testEmptyEmail() {
        assertEquals("Email is empty", EmailValidator.validateEmail(""));
    }

    @Test
    public void testMissingAtSymbol() {
        assertEquals("Missing '@' symbol in email", EmailValidator.validateEmail("user.domain.com"));
    }

    @Test
    public void testEmptyDomain() {
        assertEquals("Domain part is empty", EmailValidator.validateEmail("user@"));
    }

    @Test
    public void testDomainWithoutDot() {
        assertEquals("Domain must contain at least one dot", EmailValidator.validateEmail("user@domain"));
    }

    @Test
    public void testInvalidLocalCharacter() {
        String result = EmailValidator.validateEmail("us!er@domain.com");
        assertTrue(result.contains("Invalid email format at character:"), "Should detect an invalid character in local part");
    }

    @Test
    public void testValidEmail() {
        assertEquals("", EmailValidator.validateEmail("user.name@domain.com"), "Email should be valid");
    }
}