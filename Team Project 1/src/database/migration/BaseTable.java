package src.database.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseTable {

    /**
     * Returns the SQL statement needed to create this table if it doesn't exist.
     */
    public abstract String getCreateTableSQL();

    /**
     * Returns the name of this table, used for checks or logs.
     */
    public abstract String getTableName();

    /**
     * Called to create or synchronize the table.
     * Attempt to CREATE TABLE IF NOT EXISTS
     */
    public void syncTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(getCreateTableSQL());
        }
    }
}