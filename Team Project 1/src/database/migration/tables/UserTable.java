package src.database.migration.tables;

import src.database.migration.BaseTable;

public class UserTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Users";
    }

    @Override
    public String getCreateTableSQL() {
        // You can store password as hashed Argon2 string
        // userID is PK, auto-increment
        return "CREATE TABLE IF NOT EXISTS Users ("
                + " userID INT AUTO_INCREMENT PRIMARY KEY, "
                + " userName VARCHAR(255) NOT NULL UNIQUE, "
                + " password VARCHAR(255) NOT NULL, "
                + " email VARCHAR(255), "
                + " inviteUsed INT, " // FK to Invites table
                + " CONSTRAINT fk_inviteUsed FOREIGN KEY (inviteUsed) "
                + "     REFERENCES Invites(inviteID) "
                + "     ON DELETE SET NULL"
                + ")";
    }
}