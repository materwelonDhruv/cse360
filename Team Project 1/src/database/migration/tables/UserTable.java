package src.database.migration.tables;

import src.database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Users";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("userID",    "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userName",  "VARCHAR(255) NOT NULL UNIQUE");
        cols.put("firstName", "VARCHAR(255)");
        cols.put("lastName",  "VARCHAR(255)");
        cols.put("password",  "VARCHAR(255) NOT NULL");
        cols.put("email",     "VARCHAR(255)");
        cols.put("roles",     "INT NOT NULL DEFAULT 0");
        return cols;
    }
}