package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReadMessagesTable extends BaseTable {

    @Override
    public String getTableName() {
        return "ReadMessages";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("userID", "INT NOT NULL");
        cols.put("messageID", "INT NOT NULL");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "PRIMARY KEY (userID, messageID)",
                "CONSTRAINT fk_readMessageUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_readMessageMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE"
        };
    }
}