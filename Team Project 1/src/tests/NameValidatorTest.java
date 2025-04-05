package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import validators.NameValidator;

/**
 * Unit tests for the {@link NameValidator} class.
 * <p>
 * This test class ensures that the {@code validateName} method functions correctly by
 * verifying valid and invalid name inputs according to defined criteria.
 * </p>
 *
 * @author Dhruv
 * @see NameValidator
 */
public class NameValidatorTest {

    /**
     * Tests the {@code validateName} method with an empty name string.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testEmptyName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("");
        }, "Should throw for empty name");
    }

    /**
     * Tests the {@code validateName} method with a null name string.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName(null);
        }, "Should throw for null name");
    }

    /**
     * Tests the {@code validateName} method with names starting with non-letter characters.
     * Expects an {@link IllegalArgumentException} to be thrown for each invalid case.
     */
    @Test
    public void testNameStartsWithNonLetter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("1John");
        }, "Should throw for name starting with a non-letter");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("_Jane");
        }, "Should throw for name starting with a non-letter");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("-Alice");
        }, "Should throw for name starting with a non-letter");
    }

    /**
     * Tests the {@code validateName} method with names containing invalid characters.
     * Expects an {@link IllegalArgumentException} to be thrown for each invalid case.
     */
    @Test
    public void testNameWithInvalidCharacter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("John123");
        }, "Should throw for invalid character in name");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("Alice!");
        }, "Should throw for invalid character in name");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("Bob@");
        }, "Should throw for invalid character in name");
    }

    /**
     * Tests the {@code validateName} method with valid names.
     * Ensures no exception is thrown for properly formatted names.
     */
    @Test
    public void testValidNames() {
        Assertions.assertDoesNotThrow(() -> {
            NameValidator.validateName("John");
        }, "Name should be valid");

        Assertions.assertDoesNotThrow(() -> {
            NameValidator.validateName("Alice");
        }, "Name should be valid");

        Assertions.assertDoesNotThrow(() -> {
            NameValidator.validateName("Michael");
        }, "Name should be valid");
    }
}