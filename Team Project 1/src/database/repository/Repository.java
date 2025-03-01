package database.repository;

import database.model.BaseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T extends BaseEntity> implements IRepository<T> {
    protected final Connection connection;

    protected Repository(Connection connection) throws SQLException {
        // Store a single DB connection to be used by all operations in the subclass
        this.connection = connection;
    }

    /**
     * Wraps an SQL operation, catching SQLException and throwing DataAccessException.
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
     * @return number of rows affected
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
     * @return the generated key or -1 if none
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