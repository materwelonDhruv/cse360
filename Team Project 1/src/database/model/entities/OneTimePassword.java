package src.database.model.entities;

import src.database.model.BaseEntity;
import src.utils.Helpers;

import java.security.SecureRandom;

public class OneTimePassword extends BaseEntity {
    private int creatorId;
    private int targetId;
    private boolean isUsed;
    private String otpValue;
    private final transient String plainOtp = Helpers.generateRandomCode(10, true);

    public OneTimePassword() {
        this.isUsed = false;
        this.otpValue = plainOtp;
    }

    // Convenience constructor with creator and target IDs.
    public OneTimePassword(int creatorId, int targetId) {
        this.creatorId = creatorId;
        this.targetId = targetId;
        this.isUsed = false;
        this.otpValue = plainOtp;
    }

    // Getters and setters

    public int getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getTargetId() {
        return targetId;
    }
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public boolean isUsed() {
        return isUsed;
    }
    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getOtpValue() {
        return otpValue;
    }
    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
    }

    // This method returns the plaintext OTP (to display before hashing is done).
    public String getPlainOtp() {
        return plainOtp;
    }
}
