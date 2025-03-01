package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableSyncUtil {

    /**
     * Syncs the table schema:
     * 1) If table doesn't exist -> CREATE TABLE
     * 2) If table exists -> compare columns, add any that are missing
     * 3) (Placeholder) If columns changed or removed -> handle rename or drop
     */
    public static void syncTableSchema(Connection connection,
                                       String tableName,
                                       String createTableSQL,
                                       Map<String, String> expectedColumns) throws SQLException {
        boolean tableExists = doesTableExist(connection, tableName);
        if (!tableExists) {
            createTable(connection, createTableSQL, tableName);
            return;
        }
        // Table does exist: handle differences
        List<String> existingColumns = getExistingColumns(connection, tableName);
        addMissingColumns(connection, tableName, existingColumns, expectedColumns);
    }

    private static void createTable(Connection connection, String createTableSQL, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table created: " + tableName);
        }
    }

    /**
     * Gathers the existing columns in the DB
     */
    private static List<String> getExistingColumns(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = SCHEMA()";
        List<String> columns = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tableName.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME").toUpperCase());
                }
            }
        }
        return columns;
    }

    /**
     * Adds columns that are in 'expectedColumns' but not in 'existingColumns'.
     */
    private static void addMissingColumns(Connection connection,
                                          String tableName,
                                          List<String> existingColumns,
                                          Map<String, String> expectedColumns) throws SQLException {
        for (Map.Entry<String, String> entry : expectedColumns.entrySet()) {
            String columnName = entry.getKey().toUpperCase();
            String columnDef = entry.getValue();
            if (!existingColumns.contains(columnName)) {
                String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s",
                        tableName, entry.getKey(), columnDef);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(alterSql);
                    System.out.println("Column added: " + tableName + "." + entry.getKey());
                }
            }
        }
    }

    private static boolean doesTableExist(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = SCHEMA()";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tableName.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}