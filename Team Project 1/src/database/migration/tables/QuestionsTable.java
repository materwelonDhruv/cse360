package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "Questions" table.
 * <p>
 * This table stores question data, including the question's unique ID, associated message ID,
 * and the question title. It establishes a foreign key relationship with the "Messages" table
 * through the {@code messageID} column.
 * </p>
 *
 * @author Dhruv
 * @see MessagesTable
 */
public class QuestionsTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "Questions";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include question ID, message ID, and the title.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("questionID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        cols.put("title", "VARCHAR(255) NOT NULL");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationship between the "Questions" table and the "Messages" table
     * using the {@code messageID} column. If a message is deleted, all associated questions will also be deleted.
     * </p>
     *
     * @return An array containing the foreign key constraint as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_questionMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE"
        };
    }
}