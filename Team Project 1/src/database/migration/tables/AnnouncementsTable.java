package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "Announcements" table.
 * <p>
 * This table stores announcement entries that reference rows in the Messages table.
 * Each announcement has a title and a corresponding message record (which holds userId, content, etc.).
 * </p>
 *
 * @author Dhruv
 */
public class AnnouncementsTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "Announcements";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * The columns include announcementID (PK), messageID (FK to Messages), and title.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("announcementID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL"); // references Messages table
        cols.put("title", "VARCHAR(200) NOT NULL");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * Defines the foreign key relationship to the Messages table. If a message is deleted,
     * the corresponding announcement will also be deleted (ON DELETE CASCADE).
     * </p>
     *
     * @return An array containing the foreign key constraint as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_announcementMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE"
        };
    }
}