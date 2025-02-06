package src.database.migration.tables;

import src.database.migration.BaseTable;

public class OneTimePasswordTable extends BaseTable {

    @Override
    public String getTableName() {
        return "OneTimePasswords";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS OneTimePasswords ("
                + " otpID INT AUTO_INCREMENT PRIMARY KEY, "
                + " otpValue VARCHAR(255) NOT NULL, "
                + " creatorID INT NOT NULL, "
                + " targetID INT NOT NULL, "
                + " isUsed BOOLEAN DEFAULT FALSE, "
                + " CONSTRAINT fk_creatorID FOREIGN KEY (creatorID) "
                + "     REFERENCES Users(userID) "
                + "     ON DELETE CASCADE, "
                + " CONSTRAINT fk_targetID FOREIGN KEY (targetID) "
                + "     REFERENCES Users(userID) "
                + "     ON DELETE CASCADE "
                + ")";
    }
}