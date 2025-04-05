package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "Invites" table.
 * <p>
 * This table stores invite information, including unique invite codes, associated user IDs,
 * roles, and creation timestamps. The user ID is used to establish a foreign key relationship
 * with the "Users" table.
 * </p>
 *
 * @author Dhruv
 * @see UsersTable
 */
public class InviteTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "Invites";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include invite ID, code, user ID, roles, and creation timestamp.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("inviteID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("code", "VARCHAR(50) NOT NULL UNIQUE");
        cols.put("userID", "INT");
        cols.put("roles", "INT NOT NULL DEFAULT 0");
        cols.put("createdAt", "BIGINT NOT NULL");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationship between the "Invites" table and the "Users" table
     * using the {@code userID} column. If a user is deleted, all associated invites will also be deleted.
     * </p>
     *
     * @return An array containing the foreign key constraint as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_userID FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}