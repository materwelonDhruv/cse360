package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessagesTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Messages";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("messageID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userID", "INT NOT NULL");
        cols.put("content", "TEXT NOT NULL");
        cols.put("createdAt", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_messageUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}