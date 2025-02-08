package src.tests;

import org.junit.jupiter.api.Test;
import src.validators.NameValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameValidatorTest {

    @Test
    public void testEmptyName() {
        assertEquals("Name is empty", NameValidator.validateName(""));
    }

    @Test
    public void testNullName() {
        assertEquals("Name is empty", NameValidator.validateName(null));
    }

    @Test
    public void testNameStartsWithNonLetter() {
        assertEquals("Name must start with a letter", NameValidator.validateName("1John"));
        assertEquals("Name must start with a letter", NameValidator.validateName("_Jane"));
        assertEquals("Name must start with a letter", NameValidator.validateName("-Alice"));
    }

    @Test
    public void testNameWithInvalidCharacter() {
        assertTrue(NameValidator.validateName("John123").contains("Invalid character"), "Should detect an invalid character");
        assertTrue(NameValidator.validateName("Alice!").contains("Invalid character"), "Should detect an invalid character");
        assertTrue(NameValidator.validateName("Bob@").contains("Invalid character"), "Should detect an invalid character");
    }

    @Test
    public void testValidNames() {
        assertEquals("", NameValidator.validateName("John"));
        assertEquals("", NameValidator.validateName("Alice"));
        assertEquals("", NameValidator.validateName("Michael"));
    }
}
