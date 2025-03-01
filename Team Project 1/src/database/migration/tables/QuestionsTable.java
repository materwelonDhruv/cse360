package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionsTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Questions";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("questionID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        cols.put("title", "VARCHAR(255) NOT NULL");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_questionMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE"
        };
    }
}