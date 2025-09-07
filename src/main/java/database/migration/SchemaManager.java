package database.migration;

import database.migration.tables.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for creating or syncing all tables whenever the app starts.
 * <p>
 * This class manages the initialization and synchronization of all database tables
 * by invoking the {@code syncTable()} method of each table.
 *
 * <p>
 * The {@link SchemaManager} ensures that tables are created or modified to match their expected schemas.
 * It also provides an inspection feature to log table structures and row counts for verification purposes.
 *
 * <p><strong>Note:</strong> The {@code UserTable} must be created first due to foreign key dependencies.
 *
 * @author Dhruv
 * @see BaseTable
 */
public class SchemaManager {

    private final List<BaseTable> tables = new ArrayList<>();

    /**
     * Initializes the {@code SchemaManager} with a predefined list of tables.
     * <p>
     * The {@code UserTable} is added first to ensure that foreign key constraints are respected.
     * Additional tables are added afterward.
     * </p>
     */
    public SchemaManager() {
        // Users always need to be created first because of FK constraints
        tables.add(new UsersTable());
        tables.add(new MessagesTable());

        // Other tables
        tables.add(new InviteTable());
        tables.add(new OneTimePasswordTable());
        tables.add(new QuestionsTable());
        tables.add(new AnswersTable());
        tables.add(new PrivateMessageTable());
        tables.add(new ReadMessagesTable());
        tables.add(new ReviewsTable());
        tables.add(new ReviewerRequestsTable());
        tables.add(new StaffMessagesTable());
        tables.add(new AnnouncementsTable());
        tables.add(new AdminRequestsTable());
    }

    /**
     * Synchronizes all tables with the database.
     * <p>
     * This method iterates over all registered tables and calls their {@code syncTable()} method.
     * It ensures that each table is updated to match its expected schema.
     * </p>
     *
     * @param connection The active database connection to use for table synchronization.
     * @throws SQLException             If the connection is invalid or if a table fails to synchronize.
     * @throws IllegalArgumentException If the provided connection is null or not valid.
     */
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

    /**
     * Inspects the structure and row count of all tables.
     * <p>
     * For each table, this method logs:
     * <ul>
     *     <li>Column names, types, and sizes.</li>
     *     <li>The number of rows present in the table.</li>
     * </ul>
     * This method helps verify that tables are correctly synchronized and populated as expected.
     *
     * @param connection The active database connection to use for table inspection.
     * @throws SQLException If a database access error occurs.
     */
    public void inspectTables(Connection connection) throws SQLException {
        for (BaseTable table : tables) {
            String tableName = table.getTableName();
            System.out.println("=== Table: " + tableName + " ===");

            // Log column details from INFORMATION_SCHEMA.COLUMNS
            String colQuery = "SELECT COLUMN_NAME, DATA_TYPE AS TYPE_NAME, CHARACTER_MAXIMUM_LENGTH " +
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