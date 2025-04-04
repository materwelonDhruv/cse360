package database.migration;

import utils.TableSyncUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Base class that each table extends to define columns and optional inline constraints (e.g., foreign keys).
 * <p>
 * Provides a standardized way for defining and synchronizing table schemas with the database using
 * {@link TableSyncUtil}. Subclasses must define table name, column definitions, and optionally,
 * inline constraints.
 * </p>
 *
 * <p>
 * Usage involves implementing {@code getTableName()} and {@code getExpectedColumns()} to define
 * the schema structure. Inline constraints are optional and can be provided via {@code getInlineConstraints()}.
 * </p>
 *
 * @author Dhruv
 * @see TableSyncUtil
 */
public abstract class BaseTable {

    /**
     * Returns the name of the table.
     * <p>
     * This name is used when creating or altering the table in the database.
     * </p>
     *
     * @return The name of the table as a {@code String}.
     */
    public abstract String getTableName();

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * Each entry in the map consists of a column name (key) and its definition (value), such as:
     * <pre>
     * "userName" -> "VARCHAR(255) NOT NULL UNIQUE"
     * "createdAt" -> "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
     * </pre>
     * This information is used to generate the CREATE TABLE statement or detect missing columns for ALTER TABLE.
     *
     * @return A {@code Map} containing column names and their definitions.
     */
    public abstract Map<String, String> getExpectedColumns();

    /**
     * Returns an array of inline constraints to be appended to the table creation statement.
     * <p>
     * Inline constraints can include foreign keys, unique constraints, or any other SQL constraint
     * definitions that should be applied at table creation. For example:
     * <pre>
     * "CONSTRAINT fk_userID FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
     * </pre>
     * Each string in the array is appended to the CREATE TABLE statement after the column definitions.
     * Return an empty array if no constraints are required.
     *
     * @return An array of constraint definitions as {@code String}s.
     */
    public String[] getInlineConstraints() {
        return new String[0];
    }

    /**
     * Synchronizes the table schema with the database.
     * <p>
     * Builds a {@code CREATE TABLE} statement from the defined columns and constraints if the table
     * does not exist, or alters the table to match the provided schema if it already exists.
     * Delegates the actual synchronization logic to {@link TableSyncUtil#syncTableSchema}.
     * </p>
     *
     * @param connection The active database connection.
     * @throws SQLException If a database access error occurs or the sync operation fails.
     */
    public void syncTable(Connection connection) throws SQLException {
        String createTableSql = buildCreateTableSQL();
        TableSyncUtil.syncTableSchema(connection, getTableName(), createTableSql, getExpectedColumns());
    }

    /**
     * Dynamically builds the {@code CREATE TABLE} statement from the columns and inline constraints.
     * <p>
     * The statement is constructed by iterating over the column definitions and appending them
     * to the base statement. Inline constraints are added after all columns are defined.
     * Example output:
     * <pre>
     * CREATE TABLE IF NOT EXISTS Users (
     *     userID INT PRIMARY KEY,
     *     userName VARCHAR(255) NOT NULL UNIQUE,
     *     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     *     CONSTRAINT fk_roleID FOREIGN KEY (roleID) REFERENCES Roles(roleID)
     * )
     * </pre>
     *
     * @return The full {@code CREATE TABLE} statement as a {@code String}.
     */
    private String buildCreateTableSQL() {
        // Start building "CREATE TABLE IF NOT EXISTS TableName ( col1 def, col2 def, ... )"
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(getTableName())
                .append(" (");

        // Append each column definition
        boolean first = true;
        for (Map.Entry<String, String> entry : getExpectedColumns().entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(" ").append(entry.getValue());
            first = false;
        }

        // Append inline constraints
        for (String constraint : getInlineConstraints()) {
            sb.append(", ").append(constraint);
        }

        sb.append(")");
        return sb.toString();
    }
}