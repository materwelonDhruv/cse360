import org.junit.jupiter.api.*;
import validators.UsernameValidator;

/**
 * Unit tests for the {@link UsernameValidator} class.
 * <p>
 * This test class ensures that the {@code validateUserName} method functions correctly by
 * verifying valid and invalid username inputs according to defined criteria.
 * </p>
 *
 * @author Dhruv
 * @see UsernameValidator
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsernameValidatorTest {

    /**
     * Tests the {@code validateUserName} method with an empty username.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(1)
    public void testEmptyUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName("");
        }, "Should throw for empty username");
    }

    /**
     * Tests the {@code validateUserName} method with a username that is too short.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(2)
    public void testTooShortUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName("abc12");
        }, "Should throw for too short username");
    }

    /**
     * Tests the {@code validateUserName} method with a username that is too long.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(3)
    public void testTooLongUsername() {
        String longUsername = "a".repeat(19);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName(longUsername);
        }, "Should throw for too long username");
    }

    /**
     * Tests the {@code validateUserName} method with a username containing invalid characters.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @Order(4)
    public void testInvalidCharacterInUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            UsernameValidator.validateUserName("validUser!");
        }, "Should throw for invalid character");
    }

    /**
     * Tests the {@code validateUserName} method with a valid username.
     * Ensures no exception is thrown for a properly formatted username.
     */
    @Test
    @Order(5)
    public void testValidUsername() {
        Assertions.assertDoesNotThrow(() -> {
            UsernameValidator.validateUserName("valid_user.123");
        }, "Username should be valid");
    }
}