package database.migration;

import utils.TableSyncUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Base class each table extends to define columns and optional inline constraints (FKs).
 */
public abstract class BaseTable {

    /**
     * The table name.
     */
    public abstract String getTableName();

    /**
     * A map of columnName -> columnDefinition (e.g. "userName" -> "VARCHAR(255) NOT NULL UNIQUE").
     * Used to build the create-table statement and to detect missing columns for ALTER TABLE.
     */
    public abstract Map<String, String> getExpectedColumns();

    /**
     * Inline constraints (e.g. foreign keys). For example:
     * "CONSTRAINT fk_userID FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
     * Each string in this array is appended to the CREATE TABLE statement after the columns.
     * Return an empty array if no inline constraints.
     */
    public String[] getInlineConstraints() {
        return new String[0];
    }

    /**
     * Called by the schema manager to sync the table schema.
     * This builds a CREATE TABLE statement from the columns + constraints if needed,
     * then calls TableSyncUtil to create or alter the table.
     */
    public void syncTable(Connection connection) throws SQLException {
        String createTableSql = buildCreateTableSQL();
        TableSyncUtil.syncTableSchema(connection, getTableName(), createTableSql, getExpectedColumns());
    }

    /**
     * Dynamically builds the CREATE TABLE statement from the columns and inline constraints.
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