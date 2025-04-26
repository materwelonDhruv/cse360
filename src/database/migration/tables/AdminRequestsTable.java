package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "AdminRequests" table.
 * <p>
 * This table stores administrative requests, each with a unique requestId primary key,
 * the requester and target user IDs, the action type, the request state, a textual reason,
 * and an optional integer context value.
 * Both requesterID and targetID reference Users(userID) with ON DELETE CASCADE.
 * </p>
 *
 * @author Dhruv
 * @see UsersTable
 */
public class AdminRequestsTable extends BaseTable {

    /**
     * Name of the table.
     */
    @Override
    public String getTableName() {
        return "AdminRequests";
    }

    /**
     * Column definitions: requestId is auto-incremented primary key.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("requestId", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("requesterID", "INT NOT NULL");
        cols.put("targetID", "INT NOT NULL");
        cols.put("type", "INT NOT NULL");
        cols.put("state", "INT NOT NULL");
        cols.put("reason", "TEXT NOT NULL");
        cols.put("context", "INT");
        return cols;
    }

    /**
     * Primary key on requestId, plus foreign keys on requesterID and targetID.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_adminreq_requester FOREIGN KEY (requesterID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_adminreq_target FOREIGN KEY (targetID) REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}