package src.database.migration.tables;

import src.database.migration.BaseTable;

public class RoleTable extends BaseTable {
    @Override
    public String getTableName() {
        return "Roles";
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS Roles ("
                + " roleID INT AUTO_INCREMENT PRIMARY KEY, "
                + " roleName VARCHAR(50) NOT NULL UNIQUE "
                + ")";
    }
}