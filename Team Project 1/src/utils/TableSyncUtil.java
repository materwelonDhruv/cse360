package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides utilities for synchronizing table schemas within a relational database.
 * <p>
 * Supports table creation, detecting missing columns, and adding new columns as required.
 * </p>
 *
 * @author Dhruv
 */
public class TableSyncUtil {

    /**
     * Synchronizes a table schema within the database by creating a new table if it does not exist,
     * or by adding missing columns if the table already exists.
     *
     * @param connection      The database connection to use.
     * @param tableName       The name of the table to synchronize.
     * @param createTableSQL  The SQL statement used to create the table if it does not exist.
     * @param expectedColumns A map of expected column names and their definitions.
     * @throws SQLException If a database access error occurs or the SQL statement is invalid.
     * @see SQLException
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

    /**
     * Creates a new table using the provided SQL statement.
     *
     * @param connection     The database connection to use.
     * @param createTableSQL The SQL statement to execute for table creation.
     * @param tableName      The name of the table to be created.
     * @throws SQLException If a database access error occurs or the SQL statement is invalid.
     */
    private static void createTable(Connection connection, String createTableSQL, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table created: " + tableName);
        }
    }

    /**
     * Retrieves the names of existing columns for a specified table from the database schema.
     *
     * @param connection The database connection to use.
     * @param tableName  The name of the table whose columns are to be retrieved.
     * @return A list of column names in uppercase format.
     * @throws SQLException If a database access error occurs or the SQL statement is invalid.
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
     * Adds missing columns to an existing table based on the provided expected columns map.
     *
     * @param connection      The database connection to use.
     * @param tableName       The name of the table to modify.
     * @param existingColumns The list of currently existing column names.
     * @param expectedColumns A map of expected column names and their definitions.
     * @throws SQLException If a database access error occurs or the SQL statement is invalid.
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

    /**
     * Checks if a table exists within the current database schema.
     *
     * @param connection The database connection to use.
     * @param tableName  The name of the table to check.
     * @return {@code true} if the table exists; {@code false} otherwise.
     * @throws SQLException If a database access error occurs or the SQL statement is invalid.
     */
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