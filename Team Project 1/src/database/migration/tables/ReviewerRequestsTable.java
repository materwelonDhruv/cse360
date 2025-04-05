package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "ReviewerRequests" table.
 * <p>
 * This table stores requests for users to become reviewers, including the user ID, instructor ID,
 * request status, and timestamps for creation and updates. The user and instructor IDs are used
 * to establish foreign key relationships with the "Users" table.
 * </p>
 *
 * @author Dhruv
 * @see UsersTable
 */
public class ReviewerRequestsTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "ReviewerRequests";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include request ID, user ID, instructor ID, request status, and timestamps.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("requestID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userID", "INT NOT NULL");
        cols.put("instructorID", "INT NULL");
        cols.put("status", "TINYINT NULL");
        cols.put("createdAt", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        cols.put("updatedAt", "TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationships between the "ReviewerRequests" table and the "Users" table
     * using the {@code userID} and {@code instructorID} columns. If a user or instructor is deleted,
     * all associated requests will also be deleted.
     * </p>
     *
     * @return An array containing the foreign key constraints as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_req_user FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_req_instructor FOREIGN KEY (instructorID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}