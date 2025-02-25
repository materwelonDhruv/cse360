package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class OneTimePasswordTable extends BaseTable {

    @Override
    public String getTableName() {
        return "OneTimePasswords";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("otpID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("otpValue", "VARCHAR(255) NOT NULL");
        cols.put("creatorID", "INT NOT NULL");
        cols.put("targetID", "INT NOT NULL");
        cols.put("isUsed", "BOOLEAN DEFAULT FALSE");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_creatorID FOREIGN KEY (creatorID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_targetID FOREIGN KEY (targetID)  REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}