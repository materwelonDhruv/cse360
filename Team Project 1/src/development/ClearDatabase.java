package development;

import database.connection.DatabaseConnection;

public class ClearDatabase {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initialize();
            DatabaseConnection.clearDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}