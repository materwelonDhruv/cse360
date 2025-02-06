package src.database.model.entities;

import src.database.model.BaseEntity;

import java.security.SecureRandom;

public class OneTimePassword extends BaseEntity {
    private int creatorId;
    private int targetId;
    private boolean isUsed;
    private String otpValue;         // This will hold the hashed OTP in the DB.
    private transient String plainOtp; // Holds the plaintext OTP temporarily.

    // Default constructor auto-generates the OTP.
    public OneTimePassword() {
        generateOtp();
    }

    // Convenience constructor with creator and target IDs.
    public OneTimePassword(int creatorId, int targetId) {
        this.creatorId = creatorId;
        this.targetId = targetId;
        this.isUsed = false;
        generateOtp();
    }

    // Auto-generates a 10-character OTP with letters, digits, and special characters.
    private void generateOtp() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        plainOtp = sb.toString();
        // Initially, store the plaintext in otpValue; the repository will hash it upon creation.
        otpValue = plainOtp;
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
