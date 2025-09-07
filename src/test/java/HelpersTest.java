import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import utils.Helpers;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Helpers} utility class.
 * <p>
 * This test class verifies the behavior of time retrieval, random code generation,
 * and timestamp formatting, including edge cases such as zero-length codes
 * and the Unix epoch.
 * </p>
 *
 * @author Dhruv
 * @see Helpers
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HelpersTest {

    /**
     * Tests that {@link Helpers#getCurrentTimeInSeconds()}
     * returns a value within one second of the system clock.
     */
    @Test
    @Order(1)
    public void testGetCurrentTimeInSeconds() {
        int helperTime = Helpers.getCurrentTimeInSeconds();
        int systemTime = (int) (System.currentTimeMillis() / 1000);
        assertTrue(Math.abs(systemTime - helperTime) <= 1,
                "getCurrentTimeInSeconds should be within one second of system time");
    }

    /**
     * Tests that {@link Helpers#generateRandomCode(int, boolean)}
     * returns an empty string when length is zero, regardless of security flag.
     */
    @Test
    @Order(2)
    public void testGenerateRandomCodeZeroLength() {
        String codeNoSec = Helpers.generateRandomCode(0, false);
        assertEquals("", codeNoSec, "Expected empty string for zero length, no high security");
        String codeHighSec = Helpers.generateRandomCode(0, true);
        assertEquals("", codeHighSec, "Expected empty string for zero length, high security");
    }

    /**
     * Tests that {@link Helpers#generateRandomCode(int, boolean)}
     * with highSecurity=false produces only alphanumeric characters of the correct length.
     */
    @Test
    @Order(3)
    public void testGenerateRandomCodeAlphanumericOnly() {
        int length = 10;
        String code = Helpers.generateRandomCode(length, false);
        assertEquals(length, code.length(), "Code length should match requested length");
        assertTrue(code.matches("^[A-Za-z0-9]+$"),
                "Code should contain only alphanumeric characters when highSecurity is false: " + code);
    }

    /**
     * Tests that {@link Helpers#generateRandomCode(int, boolean)}
     * with highSecurity=true produces characters only from the allowed set.
     */
    @Test
    @Order(4)
    public void testGenerateRandomCodeWithSpecialCharacters() {
        int length = 15;
        String code = Helpers.generateRandomCode(length, true);
        assertEquals(length, code.length(), "Code length should match requested length");
        assertTrue(code.matches("^[A-Za-z0-9!@#$%&]+$"),
                "Code should contain only allowed characters when highSecurity is true: " + code);
    }

    /**
     * Tests that {@link Helpers#formatTimestamp(Timestamp)}
     * formats the Unix epoch (0 ms) as "12:00:00 01 Jan, 1970".
     */
    @Test
    @Order(5)
    public void testFormatTimestampEpoch() {
        Timestamp epoch = new Timestamp(0);
        String formatted = Helpers.formatTimestamp(epoch);
        // In America/Phoenix this will be 5 PM on Dec 31, 1969
        assertEquals("5:00 PM 31 Dec, 1969", formatted,
                "Epoch timestamp should format with local timezone and AM/PM");
    }

    /**
     * Tests that {@link Helpers#formatTimestamp(Timestamp)}
     * correctly formats a known timestamp value.
     */
    @Test
    @Order(6)
    public void testFormatTimestampSpecificDate() {
        // Dec 31, 2021 23:59:59 local time
        Timestamp ts = Timestamp.valueOf("2021-12-31 23:59:59");
        String formatted = Helpers.formatTimestamp(ts);
        assertEquals("11:59 PM 31 Dec, 2021", formatted,
                "Specific timestamp should include PM marker");
    }
}