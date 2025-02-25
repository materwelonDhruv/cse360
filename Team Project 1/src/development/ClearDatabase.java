package development;

import application.AppContext;
import database.connection.DatabaseConnection;

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