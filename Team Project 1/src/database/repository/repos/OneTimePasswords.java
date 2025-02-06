package src.database.repository.repos;

import src.database.model.entities.OneTimePassword;
import src.database.repository.Repository;
import src.utils.PasswordUtil;

import java.sql.ResultSet;
import java.sql.Connection;
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
                pstmt -> {}, // no parameters
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
     * Checks if the provided OTP is valid for the given otpID.
     * <pre>
     * - Only works if the OTP is not already used.
     * - If verification succeeds, marks it as used and returns true.
     * - Otherwise, returns false.
     * </pre>
     */
    public boolean check(int otpId, String providedOtp) {
        String sql = "SELECT otpValue, isUsed FROM OneTimePasswords WHERE otpID = ?";
        Boolean result = queryForObject(sql,
                pstmt -> pstmt.setInt(1, otpId),
                rs -> {
                    if (rs.getBoolean("isUsed")) {
                        return false;
                    }
                    String storedHash = rs.getString("otpValue");
                    if (PasswordUtil.verifyPassword(storedHash, providedOtp)) {
                        // Mark as used
                        String updateSql = "UPDATE OneTimePasswords SET isUsed = TRUE WHERE otpID = ?";
                        int rows = executeUpdate(updateSql, pstmt2 -> pstmt2.setInt(1, otpId));
                        return rows > 0;
                    }
                    return false;
                }
        );
        return result != null && result;
    }
}