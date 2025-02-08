package src.database.repository.repos;

import src.database.model.entities.OneTimePassword;
import src.database.repository.Repository;
import src.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OneTimePasswords extends Repository<OneTimePassword> {

    public OneTimePasswords(Connection connection) throws SQLException {
        super(connection);
    }

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

    @Override
    public OneTimePassword getById(int id) {
        String sql = "SELECT * FROM OneTimePasswords WHERE otpID = ?";
        return queryForObject(sql,
                pstmt -> pstmt.setInt(1, id),
                this::build
        );
    }

    @Override
    public List<OneTimePassword> getAll() {
        String sql = "SELECT * FROM OneTimePasswords";
        return queryForList(sql,
                pstmt -> {
                }, // no parameters
                this::build
        );
    }

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