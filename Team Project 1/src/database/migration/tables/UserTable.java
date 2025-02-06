package src.database.migration.tables;

import src.database.migration.BaseTable;

public class UserTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Users";
    }

    @Override
    public String getCreateTableSQL() {
        // Note: The new "roles" column stores an integer bit field (default 0)
        return "CREATE TABLE IF NOT EXISTS Users ("
                + " userID INT AUTO_INCREMENT PRIMARY KEY, "
                + " userName VARCHAR(255) NOT NULL UNIQUE, "
                + " password VARCHAR(255) NOT NULL, "
                + " email VARCHAR(255), "
                + " roles INT NOT NULL DEFAULT 0, "  // NEW column for roles bit field
                + " inviteUsed INT, "               // FK to Invites table
                + " CONSTRAINT fk_inviteUsed FOREIGN KEY (inviteUsed) "
                + "     REFERENCES Invites(inviteID) "
                + "     ON DELETE SET NULL"
                + ")";
    }
}
