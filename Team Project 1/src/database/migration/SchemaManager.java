package src.database.migration;

import src.database.migration.tables.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for creating or syncing all tables whenever the app starts.
 */
public class SchemaManager {

    private final List<BaseTable> tables = new ArrayList<>();

    public SchemaManager() {
        // Register the tables in the order you want them created
        tables.add(new UserTable());
        tables.add(new InviteTable());
        tables.add(new OneTimePasswordTable());
    }

    public void syncDatabases(Connection connection) throws SQLException {
        if (connection.isValid(5)) {
            for (BaseTable table : tables) {
                System.out.println("Synchronizing table: " + table.getTableName());
                table.syncTable(connection);
            }
        } else{
            throw new IllegalArgumentException("Connection is null");
        }
    }
}