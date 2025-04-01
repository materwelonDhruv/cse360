package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReviewerRequestsTable extends BaseTable {
    @Override
    public String getTableName() {
        return "ReviewerRequests";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("requestID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userID", "INT NOT NULL");
        cols.put("instructorID", "INT NULL");
        cols.put("status", "TINYINT NULL");
        cols.put("createdAt", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        cols.put("updatedAt", "TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_req_user FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_req_instructor FOREIGN KEY (instructorID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}