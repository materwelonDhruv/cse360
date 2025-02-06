package src.database.migration.tables;

import src.database.migration.BaseTable;

public class InviteTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Invites";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS Invites ("
                + " inviteID INT AUTO_INCREMENT PRIMARY KEY, "
                + " code VARCHAR(50) NOT NULL UNIQUE, "
                + " userID INT, "  // FK referencing the user who created this invite
                + " roles INT NOT NULL DEFAULT 0, "       // roles bit field
                + " createdAt BIGINT NOT NULL, "           // Unix timestamp (in seconds)
                + " CONSTRAINT fk_userID FOREIGN KEY (userID) "
                + "     REFERENCES Users(userID) "
                + "     ON DELETE CASCADE"
                + ")";
    }
}
