package src.database.migration;

import src.database.migration.tables.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for creating or syncing all tables whenever the app starts.
 */
public class SchemaManager {

    private final List<BaseTable> tables = new ArrayList<>();

    public SchemaManager() {
        tables.add(new UserTable());
        tables.add(new InviteTable());
        tables.add(new OneTimePasswordTable());
    }

    public void syncDatabases(Connection connection) throws SQLException {
        if (connection.isValid(5)) {
            try (Statement stmt = connection.createStatement()) {
                // Disable referential integrity to allow circular dependencies.
                stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            }

            for (BaseTable table : tables) {
                System.out.println("Synchronizing table: " + table.getTableName());
                table.syncTable(connection);
            }

            try (Statement stmt = connection.createStatement()) {
                // Re-enable referential integrity after tables are created.
                stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        } else {
            throw new IllegalArgumentException("Connection is null or not valid");
        }
    }
}