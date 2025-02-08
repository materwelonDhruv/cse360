package src.database.migration;

import src.database.migration.tables.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for creating or syncing all tables whenever the app starts.
 */
public class SchemaManager {

    private final List<BaseTable> tables = new ArrayList<>();

    public SchemaManager() {
        // Users always need to be created first because of FK constraints
        tables.add(new UserTable());

        // Other tables
        tables.add(new InviteTable());
        tables.add(new OneTimePasswordTable());
    }

    public void syncTables(Connection connection) throws SQLException {
        if (connection.isValid(5)) {
            for (BaseTable table : tables) {
                System.out.println("Synchronizing table: " + table.getTableName());
                table.syncTable(connection);
            }
        } else {
            throw new IllegalArgumentException("Connection is null or not valid");
        }
    }

    public void inspectTables(Connection connection) throws SQLException {
        for (BaseTable table : tables) {
            String tableName = table.getTableName();
            System.out.println("=== Table: " + tableName + " ===");

            // Log column details from INFORMATION_SCHEMA.COLUMNS
            String colQuery = "SELECT COLUMN_NAME, TYPE_NAME, CHARACTER_MAXIMUM_LENGTH " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = SCHEMA() " +
                    "ORDER BY ORDINAL_POSITION";
            try (PreparedStatement pstmt = connection.prepareStatement(colQuery)) {
                pstmt.setString(1, tableName.toUpperCase());
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("Columns:");
                    while (rs.next()) {
                        String colName = rs.getString("COLUMN_NAME");
                        String typeName = rs.getString("TYPE_NAME");
                        int charMax = rs.getInt("CHARACTER_MAXIMUM_LENGTH");
                        String lengthInfo = (charMax > 0) ? "(" + charMax + ")" : "";
                        System.out.println("  " + colName + " - " + typeName + lengthInfo);
                    }
                }
            }

            // Log the number of rows in the table
            String countQuery = "SELECT COUNT(*) FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(countQuery)) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Row count: " + count);
                }
            }
            System.out.println("-----------------------------------");
        }
    }
}