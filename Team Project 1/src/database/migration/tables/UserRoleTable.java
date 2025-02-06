package src.database.migration.tables;

import src.database.migration.BaseTable;

public class UserRoleTable extends BaseTable {

    @Override
    public String getTableName() {
        return "UserRoles";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS UserRoles ("
                + " userID INT NOT NULL, "
                + " roleID INT NOT NULL, "
                + " PRIMARY KEY (userID, roleID), "
                + " CONSTRAINT fk_userRole_userID FOREIGN KEY (userID) "
                + "     REFERENCES Users(userID) "
                + "     ON DELETE CASCADE, "
                + " CONSTRAINT fk_userRole_roleID FOREIGN KEY (roleID) "
                + "     REFERENCES Roles(roleID) "
                + "     ON DELETE CASCADE "
                + ")";
    }
}