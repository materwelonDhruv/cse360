package database.repository;

import java.sql.SQLException;

/**
 * A runtime exception that wraps any {@link SQLException} or general data access error.
 * <p>
 * This exception is used to handle SQL-related errors and propagate them as unchecked exceptions,
 * allowing the application to fail gracefully when data access issues occur.
 * </p>
 *
 * <p>
 * It is thrown by repository classes when database operations encounter exceptions.
 * </p>
 *
 * @author Dhruv
 */
public class DataAccessException extends RuntimeException {

    /**
     * Constructs a new {@code DataAccessException} with the specified detail message and cause.
     *
     * @param message The detail message explaining the reason for the exception.
     * @param cause   The underlying cause of the exception, typically a {@link SQLException}.
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}