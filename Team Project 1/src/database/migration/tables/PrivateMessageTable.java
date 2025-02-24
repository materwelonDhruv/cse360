package src.database.migration.tables;

import src.database.migration.BaseTable;

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
        cols.put("userID", "INT NOT NULL");
        cols.put("content", "TEXT NOT NULL");
        cols.put("questionID", "INT NOT NULL");
        cols.put("createdAt", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_questionUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}
