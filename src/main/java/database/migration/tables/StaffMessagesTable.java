package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "StaffMessages" table.
 * <p>
 * This table stores messages between a staff member and any other user.
 * It'll have a foreign key relationship with the "Messages" table through the {@code messageID} column.
 * </p>
 *
 * @author Dhruv
 * @see MessagesTable
 */
public class StaffMessagesTable extends BaseTable {
    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "StaffMessages";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include userId, staffId, and messageId.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("staffMessageID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userID", "INT NOT NULL");
        cols.put("staffID", "INT NOT NULL");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationship between the "StaffMessages" table and the "Messages" table
     * using the {@code messageID} column. If a message is deleted, all associated staff messages will also be deleted.
     * </p>
     *
     * @return An array containing the foreign key constraint as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_staffMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE",
                "CONSTRAINT fk_staffMessageUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_staffMessageStaff FOREIGN KEY (staffID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}