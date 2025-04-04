package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "Messages" table.
 * <p>
 * This table stores messages sent by users, including message content, creation time,
 * and the associated user ID. The user ID is used to establish a foreign key relationship
 * with the "Users" table.
 * </p>
 *
 * @author Dhruv
 */
public class MessagesTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "Messages";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include message ID, user ID, message content, and the creation timestamp.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("messageID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userID", "INT NOT NULL");
        cols.put("content", "TEXT NOT NULL");
        cols.put("createdAt", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * In this case, it defines a foreign key constraint for the user ID column that references
     * the {@code userID} column in the "Users" table. The constraint ensures that if a user is deleted,
     * all associated messages are also deleted.
     * </p>
     *
     * @return An array containing the foreign key constraint as a {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_messageUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}