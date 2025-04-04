package database.repository;

import database.model.BaseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract repository class providing common database operations for entities extending {@link BaseEntity}.
 * <p>
 * This class provides a centralized mechanism for performing CRUD operations using a shared database connection.
 * Subclasses should implement the specific logic for their respective entities.
 * </p>
 *
 * @param <T> The type of entity managed by the repository, which must extend {@link BaseEntity}.
 * @author Dhruv
 */
public abstract class Repository<T extends BaseEntity> implements IRepository<T> {
    protected final Connection connection;

    /**
     * Constructs a new repository with the provided database connection.
     *
     * @param connection The database connection to be used by all operations.
     * @throws SQLException if there is an error initializing the connection.
     */
    protected Repository(Connection connection) throws SQLException {
        // Store a single DB connection to be used by all operations in the subclass
        this.connection = connection;
    }

    /**
     * Wraps an SQL operation, catching {@link SQLException} and throwing {@link DataAccessException}.
     *
     * @param operation The SQL operation to be executed.
     * @param <R>       The type of result returned by the operation.
     * @return The result of the operation.
     */
    protected <R> R wrap(SqlOperation<R> operation) {
        try {
            return operation.execute();
        } catch (SQLException e) {
            throw new DataAccessException("Data access error", e);
        }
    }

    /**
     * Executes a query expecting a single row result, returning that object or null.
     *
     * @param sql         The SQL query to execute.
     * @param paramSetter A lambda function to set parameters on the {@link PreparedStatement}.
     * @param rowMapper   A lambda function to map the {@link ResultSet} to a result object.
     * @param <R>         The type of the result object.
     * @return The result object, or null if no row was found.
     */
    protected <R> R queryForObject(String sql, SqlConsumer paramSetter, SqlFunction<R> rowMapper) {
        return wrap(() -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                paramSetter.accept(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rowMapper.apply(rs);
                    }
                }
            }
            return null;
        });
    }

    /**
     * Executes a query expecting multiple rows, returning them as a list.
     *
     * @param sql         The SQL query to execute.
     * @param paramSetter A lambda function to set parameters on the {@link PreparedStatement}.
     * @param rowMapper   A lambda function to map the {@link ResultSet} to result objects.
     * @param <R>         The type of the result object.
     * @return A list of result objects.
     */
    protected <R> List<R> queryForList(String sql, SqlConsumer paramSetter, SqlFunction<R> rowMapper) {
        return wrap(() -> {
            List<R> results = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                paramSetter.accept(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(rowMapper.apply(rs));
                    }
                }
            }
            return results;
        });
    }

    /**
     * Executes a query expecting a boolean result.
     *
     * @param sql         The SQL query to execute.
     * @param paramSetter A lambda function to set parameters on the {@link PreparedStatement}.
     * @return true if the query returns a row with a true boolean value, otherwise false.
     */
    protected boolean queryForBoolean(String sql, SqlConsumer paramSetter) {
        return wrap(() -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                paramSetter.accept(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            }
        });
    }

    /**
     * Executes an update (INSERT, UPDATE, DELETE) that does not need generated keys.
     *
     * @param sql         The SQL query to execute.
     * @param paramSetter A lambda function to set parameters on the {@link PreparedStatement}.
     * @return The number of rows affected.
     */
    protected int executeUpdate(String sql, SqlConsumer paramSetter) {
        return wrap(() -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                paramSetter.accept(pstmt);
                return pstmt.executeUpdate();
            }
        });
    }

    /**
     * Executes an INSERT statement that returns a generated key (if any).
     *
     * @param sql         The SQL query to execute.
     * @param paramSetter A lambda function to set parameters on the {@link PreparedStatement}.
     * @return The generated key or -1 if none.
     */
    protected int executeInsert(String sql, SqlConsumer paramSetter) {
        return wrap(() -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                paramSetter.accept(pstmt);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        });
    }

    // CRUD methods (to be implemented by subclasses)
    @Override
    public T create(T entity) throws SQLException {
        throw new UnsupportedOperationException("Create method not implemented");
    }

    @Override
    public T getById(int id) throws SQLException {
        throw new UnsupportedOperationException("getById method not implemented");
    }

    @Override
    public List<T> getAll() throws SQLException {
        throw new UnsupportedOperationException("getAll method not implemented");
    }

    /**
     * Internal method to build an object from a ResultSet row. Does not use this method directly.
     */
    @Override
    public T build(ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("build method not implemented");
    }

    @Override
    public T update(T entity) throws SQLException {
        throw new UnsupportedOperationException("update method not implemented");
    }

    @Override
    public void delete(int id) throws SQLException {
        throw new UnsupportedOperationException("delete method not implemented");
    }

    /**
     * Functional interface for an operation returning a result (possibly a query).
     */
    @FunctionalInterface
    protected interface SqlOperation<R> {
        R execute() throws SQLException;
    }

    /**
     * Functional interface for setting parameters in a PreparedStatement.
     */
    @FunctionalInterface
    protected interface SqlConsumer {
        void accept(PreparedStatement pstmt) throws SQLException;
    }

    /**
     * Functional interface for mapping a single row in a ResultSet to some object.
     */
    @FunctionalInterface
    protected interface SqlFunction<R> {
        R apply(ResultSet rs) throws SQLException;
    }
}