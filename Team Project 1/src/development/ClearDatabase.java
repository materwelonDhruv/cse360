package src.development;

import src.application.AppContext;
import src.database.connection.DatabaseConnection;

public class ClearDatabase {
    public static void main(String[] args) {
        try {
            AppContext context = AppContext.getInstance();

            DatabaseConnection.clearDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}