package database.repository.repos;

import database.model.entities.OneTimePassword;
import database.repository.Repository;
import utils.PasswordUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository class for managing {@link OneTimePassword} entities in the database.
 * <p>
 * This class provides methods for performing CRUD operations on the "OneTimePasswords" table, including creating,
 * retrieving, updating, and deleting one-time passwords (OTPs). It also provides functionality for checking the validity
 * of an OTP.
 * </p>
 *
 * @author Dhruv
 * @see Repository
 * @see OneTimePassword
 */
public class OneTimePasswords extends Repository<OneTimePassword> {

    /**
     * Constructor for {@code OneTimePasswords} repository.
     *
     * @param connection The database connection to be used by this repository.
     * @throws SQLException If an error occurs during the initialization of the repository.
     */
    public OneTimePasswords(Connection connection) throws SQLException {
        super(connection);
    }

    /**
     * Creates a new one-time password (OTP) in the "OneTimePasswords" table.
     * <p>
     * The OTP value is hashed before being stored in the database.
     * </p>
     *
     * @param otp The {@link OneTimePassword} object to be created.
     * @return The created {@link OneTimePassword} object, with its ID set.
     */
    @Override
    public OneTimePassword create(OneTimePassword otp) {
        String plainOtp = otp.getOtpValue();  // generated or assigned in the entity
        String hashedOtp = PasswordUtil.hashPassword(plainOtp);
        otp.setOtpValue(hashedOtp);

        String sql = "INSERT INTO OneTimePasswords (otpValue, creatorID, targetID, isUsed) VALUES (?, ?, ?, ?)";
        int generatedId = executeInsert(sql, pstmt -> {
            pstmt.setString(1, hashedOtp);
            pstmt.setInt(2, otp.getCreatorId());
            pstmt.setInt(3, otp.getTargetId());
            pstmt.setBoolean(4, otp.isUsed());
        });

        if (generatedId > 0) {
            otp.setId(generatedId);
        }
        return otp;
    }

    /**
     * Retrieves a one-time password (OTP) by its ID.
     *
     * @param id The ID of the OTP to be retrieved.
     * @return The {@link OneTimePassword} object corresponding to the provided ID, or {@code null} if not found.
     */
    @Override
    public OneTimePassword getById(int id) {
        String sql = "SELECT * FROM OneTimePasswords WHERE otpID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    /**
     * Retrieves all one-time passwords (OTPs) from the "OneTimePasswords" table.
     *
     * @return A list of all {@link OneTimePassword} objects in the table.
     */
    @Override
    public List<OneTimePassword> getAll() {
        String sql = "SELECT * FROM OneTimePasswords";
        return queryForList(sql,
                pstmt -> {
                }, // no parameters
                this::build
        );
    }

    /**
     * Builds a {@link OneTimePassword} object from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet} containing the OTP data.
     * @return The {@link OneTimePassword} object created from the result set.
     * @throws SQLException If an error occurs while extracting data from the result set.
     */
    @Override
    public OneTimePassword build(ResultSet rs) throws SQLException {
        OneTimePassword otp = new OneTimePassword();
        otp.setId(rs.getInt("otpID"));
        otp.setCreatorId(rs.getInt("creatorID"));
        otp.setTargetId(rs.getInt("targetID"));
        otp.setUsed(rs.getBoolean("isUsed"));
        otp.setOtpValue(rs.getString("otpValue"));
        return otp;
    }

    /**
     * Updates an existing one-time password (OTP) in the "OneTimePasswords" table.
     *
     * @param otp The {@link OneTimePassword} object containing the updated information.
     * @return The updated {@link OneTimePassword} object if the update was successful, or {@code null} if no rows were affected.
     */
    @Override
    public OneTimePassword update(OneTimePassword otp) {
        String sql = "UPDATE OneTimePasswords SET creatorID = ?, targetID = ?, isUsed = ?, otpValue = ? WHERE otpID = ?";
        int rows = executeUpdate(sql, pstmt -> {
            pstmt.setInt(1, otp.getCreatorId());
            pstmt.setInt(2, otp.getTargetId());
            pstmt.setBoolean(3, otp.isUsed());
            pstmt.setString(4, otp.getOtpValue());
            pstmt.setInt(5, otp.getId());
        });
        return rows > 0 ? otp : null;
    }

    /**
     * Deletes a one-time password (OTP) from the "OneTimePasswords" table by its ID.
     *
     * @param id The ID of the OTP to be deleted.
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM OneTimePasswords WHERE otpID = ?";
        executeUpdate(sql, pstmt -> pstmt.setInt(1, id));
    }

    /**
     * Checks if the provided OTP is valid for the given targetID.
     * <pre>
     * - Selects all OTPs where targetID = ? and isUsed = FALSE.
     * - For each, if PasswordUtil.verifyPassword(stored, provided) returns true,
     *   it marks that OTP as used (UPDATE) and returns true.
     * - Otherwise, returns false.
     * </pre>
     *
     * @param targetId    The target user ID associated with the OTP.
     * @param providedOtp The OTP value provided by the user.
     * @return {@code true} if the OTP is valid and successfully marked as used, {@code false} otherwise.
     */
    public boolean check(int targetId, String providedOtp) {
        String sql = "SELECT otpID, otpValue FROM OneTimePasswords WHERE targetID = ? AND isUsed = FALSE";

        // Helper class to store OTP records.
        class OTPRecord {
            final int otpID;
            final String otpValue;

            OTPRecord(int id, String value) {
                this.otpID = id;
                this.otpValue = value;
            }
        }
        // Query for all OTP records for the target.
        List<OTPRecord> otpRecords = queryForList(sql,
                pstmt -> pstmt.setInt(1, targetId),
                rs -> new OTPRecord(rs.getInt("otpID"), rs.getString("otpValue"))
        );
        for (OTPRecord record : otpRecords) {
            if (PasswordUtil.verifyPassword(record.otpValue, providedOtp)) {
                // Mark this OTP as used
                String updateSql = "UPDATE OneTimePasswords SET isUsed = TRUE WHERE otpID = ?";
                int rows = executeUpdate(updateSql, pstmt -> pstmt.setInt(1, record.otpID));
                return rows > 0;
            }
        }
        return false;
    }

}