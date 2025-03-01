package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class InviteTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Invites";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("inviteID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("code", "VARCHAR(50) NOT NULL UNIQUE");
        cols.put("userID", "INT");
        cols.put("roles", "INT NOT NULL DEFAULT 0");
        cols.put("createdAt", "BIGINT NOT NULL");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_userID FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}