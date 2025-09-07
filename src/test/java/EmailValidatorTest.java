import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import validators.EmailValidator;

/**
 * Unit tests for the {@link EmailValidator} class.
 * <p>
 * This test class verifies the correct functionality of the {@code validateEmail} method
 * by checking various valid and invalid email inputs according to defined criteria.
 * </p>
 *
 * @author Dhruv
 * @see EmailValidator
 */
public class EmailValidatorTest {

    /**
     * Tests the {@code validateEmail} method with an empty email string.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testEmptyEmail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("");
        }, "Should throw for empty email");
    }

    /**
     * Tests the {@code validateEmail} method with a missing '@' symbol.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testMissingAtSymbol() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("user.domain.com");
        }, "Missing '@' symbol in email");
    }

    /**
     * Tests the {@code validateEmail} method with an empty domain part.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testEmptyDomain() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("user@");
        }, "Domain part is empty");
    }

    /**
     * Tests the {@code validateEmail} method with a domain that lacks a dot.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testDomainWithoutDot() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("user@domain");
        }, "Domain must contain at least one dot");
    }

    /**
     * Tests the {@code validateEmail} method with an invalid character in the local part of the email.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testInvalidLocalCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            EmailValidator.validateEmail("us!er@domain.com");
        }, "Should throw for invalid character in local part");
    }

    /**
     * Tests the {@code validateEmail} method with a valid email address.
     * Ensures no exception is thrown for a properly formatted email.
     */
    @Test
    public void testValidEmail() {
        Assertions.assertDoesNotThrow(() -> {
            EmailValidator.validateEmail("user.name@domain.com");
        }, "Email should be valid");
    }
}