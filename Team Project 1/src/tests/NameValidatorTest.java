package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import validators.NameValidator;

public class NameValidatorTest {

    @Test
    public void testEmptyName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName("");
        }, "Should throw for empty name");
    }

    @Test
    public void testNullName() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            NameValidator.validateName(null);
        }, "Should throw for null name");
    }

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
