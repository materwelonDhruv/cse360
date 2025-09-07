package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "Reviews" table.
 * <p>
 * This table stores review data, including reviewer ID, user ID, and rating. It establishes foreign
 * key relationships with the "Users" table for both reviewer and user IDs.
 * </p>
 *
 * @author Dhruv
 * @see UsersTable
 */
public class ReviewsTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "Reviews";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include reviewer ID, user ID, and rating.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("reviewerID", "INT NOT NULL");
        cols.put("userID", "INT NOT NULL");
        cols.put("rating", "INT NOT NULL DEFAULT 0");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines:
     * <ul>
     *     <li>The primary key constraint on {@code reviewerID} and {@code userID}.</li>
     *     <li>A foreign key constraint for {@code userID} referencing the "Users" table, with cascading deletes.</li>
     *     <li>A foreign key constraint for {@code reviewerID} referencing the "Users" table, with cascading deletes.</li>
     * </ul>
     *
     * @return An array containing the foreign key constraints and primary key constraint as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "PRIMARY KEY (reviewerID, userID)",
                "CONSTRAINT fk_reviewerUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_reviewerReviewer FOREIGN KEY (reviewerID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}