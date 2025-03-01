package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrivateMessageTable extends BaseTable {

    @Override
    public String getTableName() {
        return "PrivateMessages";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("privateMessageID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        cols.put("questionID", "INT NULL"); // If top-level private message
        cols.put("parentPrivateMessageID", "INT NULL"); // For threaded replies
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_privateMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE",
                "CONSTRAINT fk_privateQuestion FOREIGN KEY (questionID) REFERENCES Questions(questionID) ON DELETE CASCADE",
                "CONSTRAINT fk_parentPrivateMessage FOREIGN KEY (parentPrivateMessageID) REFERENCES PrivateMessages(privateMessageID) ON DELETE CASCADE"
        };
    }
}