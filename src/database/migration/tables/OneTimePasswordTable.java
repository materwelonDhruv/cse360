package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the schema for the "OneTimePasswords" table.
 * <p>
 * This table stores one-time password (OTP) data, including the OTP value, creator ID, target ID,
 * and a flag indicating whether the OTP has been used. It also establishes foreign key relationships
 * with the "Users" table for both the creator and target IDs.
 * </p>
 *
 * @author Dhruv
 * @see UsersTable
 */
public class OneTimePasswordTable extends BaseTable {

    /**
     * Returns the name of the table.
     *
     * @return The name of the table as a {@code String}.
     */
    @Override
    public String getTableName() {
        return "OneTimePasswords";
    }

    /**
     * Returns a map of column definitions used to build the table schema.
     * <p>
     * This includes the column name as the key and its type/definition as the value.
     * The columns include OTP ID, OTP value, creator ID, target ID, and the "isUsed" flag.
     * </p>
     *
     * @return A {@code Map} of column names and their definitions.
     */
    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("otpID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("otpValue", "VARCHAR(255) NOT NULL");
        cols.put("creatorID", "INT NOT NULL");
        cols.put("targetID", "INT NOT NULL");
        cols.put("isUsed", "BOOLEAN DEFAULT FALSE");
        return cols;
    }

    /**
     * Returns an array of inline constraints for the table.
     * <p>
     * This defines the foreign key relationship between the "OneTimePasswords" table and the "Users" table
     * using the {@code creatorID} and {@code targetID} columns. If a user is deleted, all associated OTPs
     * will also be deleted.
     * </p>
     *
     * @return An array containing the foreign key constraints as {@code String}.
     */
    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_creatorID FOREIGN KEY (creatorID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_targetID FOREIGN KEY (targetID)  REFERENCES Users(userID) ON DELETE CASCADE"
        };
    }
}