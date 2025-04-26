package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "PrivateMessages" table.
 * <p>
 * This table stores private message data, including message ID, question ID, and a reference to
 * parent private messages for threading. It establishes foreign key relationships with the
 * "Messages" and "Questions" tables and itself for threaded replies.
 * </p>
 *
 * @author Dhruv
 * @see MessagesTable
 * @see QuestionsTable
 */
public class PrivateMessageTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "PrivateMessages";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include private message ID, message ID, question ID, and parent private message ID for threading.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("privateMessageID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        cols.put("questionID", "INT NULL"); // If top-level private message
        cols.put("parentPrivateMessageID", "INT NULL"); // For threaded replies
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationships between the "PrivateMessages" table and the "Messages" and
     * "Questions" tables. It also includes a foreign key constraint for parent private messages, allowing
     * threaded replies.
     * </p>
     *
     * @return An array containing the foreign key constraints as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_privateMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE",
                "CONSTRAINT fk_privateQuestion FOREIGN KEY (questionID) REFERENCES Questions(questionID) ON DELETE CASCADE",
                "CONSTRAINT fk_parentPrivateMessage FOREIGN KEY (parentPrivateMessageID) REFERENCES PrivateMessages(privateMessageID) ON DELETE CASCADE"
        };
    }
}