package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReviewsTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Reviews";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("reviewerID", "INT NOT NULL");
        cols.put("userID", "INT NOT NULL");
        cols.put("rating", "INT NOT NULL DEFAULT 0");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "PRIMARY KEY (reviewerID, userID)",
                "CONSTRAINT fk_reviewerUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_reviewerReviewer FOREIGN KEY (reviewerID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}