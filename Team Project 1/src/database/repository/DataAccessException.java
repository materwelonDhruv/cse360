package src.database.repository;

/**
 * A runtime exception that wraps any SQLException or data access error.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}