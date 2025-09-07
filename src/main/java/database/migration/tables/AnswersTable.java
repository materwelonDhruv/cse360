package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "Answers" table.
 * <p>
 * This table stores answers to questions. It includes fields for the answer's ID, the associated message ID,
 * the associated question ID, and optional fields for threaded replies and pinned answers.
 * </p>
 *
 * @author Dhruv
 * @see MessagesTable
 * @see UsersTable
 */
public class AnswersTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "Answers";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include answer ID, message ID, question ID, parent answer ID, and a pinned status.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("answerID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        cols.put("questionID", "INT NULL");            // If top-level answer
        cols.put("parentAnswerID", "INT NULL");         // For threaded replies
        cols.put("isPinned", "BOOLEAN NOT NULL DEFAULT FALSE");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationships between the "Answers" table and other tables:
     * - A foreign key constraint to the "Messages" table for the {@code messageID}.
     * - A foreign key constraint to the "Questions" table for the {@code questionID}.
     * - A foreign key constraint to the same "Answers" table for the {@code parentAnswerID} for threaded replies.
     * </p>
     *
     * @return An array containing the foreign key constraints as {@code String}s.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_answerMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE",
                "CONSTRAINT fk_questionID FOREIGN KEY (questionID) REFERENCES Questions(questionID) ON DELETE CASCADE",
                "CONSTRAINT fk_parentAnswerID FOREIGN KEY (parentAnswerID) REFERENCES Answers(answerID) ON DELETE CASCADE"
        };
    }
}