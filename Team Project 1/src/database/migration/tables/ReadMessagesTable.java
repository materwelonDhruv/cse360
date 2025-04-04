package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "ReadMessages" table.
 * <p>
 * This table stores information about messages that have been read by users. It includes the user ID
 * and message ID, creating a relationship between users and the messages they have read.
 * </p>
 *
 * @author Dhruv
 * @see UsersTable
 * @see MessagesTable
 */
public class ReadMessagesTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "ReadMessages";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns are userID and messageID, both of which are required to create the record.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("userID", "INT NOT NULL");
        cols.put("messageID", "INT NOT NULL");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This includes:
     * <ul>
     *     <li>A primary key constraint on both {@code userID} and {@code messageID}.</li>
     *     <li>A foreign key constraint on {@code userID} referencing the "Users" table, with cascading deletes.</li>
     *     <li>A foreign key constraint on {@code messageID} referencing the "Messages" table, with cascading deletes.</li>
     * </ul>
     *
     * @return An array containing the foreign key constraints and primary key constraint as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "PRIMARY KEY (userID, messageID)",
                "CONSTRAINT fk_readMessageUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_readMessageMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE"
        };
    }
}