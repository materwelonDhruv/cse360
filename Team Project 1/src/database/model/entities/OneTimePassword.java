package database.model.entities;

import database.model.BaseEntity;
import utils.Helpers;

/**
 * Represents a one-time password (OTP) used for authentication or validation.
 * <p>
 * This class generates a random OTP, stores the creator and target IDs, and provides methods to check if the OTP
 * has been used, as well as to retrieve the OTP value and its plain text form.
 * </p>
 *
 * @author Dhruv
 * @see Helpers
 */
public class OneTimePassword extends BaseEntity {
    private final transient String plainOtp = Helpers.generateRandomCode(10, true);
    private int creatorId;
    private int targetId;
    private boolean isUsed;
    private String otpValue;

    /**
     * Default constructor for {@code OneTimePassword}.
     * Initializes a new OTP with the default values (not used) and a randomly generated OTP value.
     */
    public OneTimePassword() {
        this.isUsed = false;
        this.otpValue = plainOtp;
    }

    /**
     * Convenience constructor for {@code OneTimePassword} with the specified creator and target IDs.
     *
     * @param creatorId The ID of the user who created the OTP.
     * @param targetId  The ID of the user who is the target of the OTP.
     */
    public OneTimePassword(int creatorId, int targetId) {
        this.creatorId = creatorId;
        this.targetId = targetId;
        this.isUsed = false;
        this.otpValue = plainOtp;
    }

    /**
     * Gets the ID of the creator of the OTP.
     *
     * @return The creator's ID.
     */
    public int getCreatorId() {
        return creatorId;
    }

    /**
     * Sets the ID of the creator of the OTP.
     *
     * @param creatorId The new creator ID to be set.
     */
    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Gets the ID of the target user for whom the OTP is generated.
     *
     * @return The target user's ID.
     */
    public int getTargetId() {
        return targetId;
    }

    /**
     * Sets the ID of the target user for whom the OTP is generated.
     *
     * @param targetId The new target ID to be set.
     */
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    /**
     * Gets whether the OTP has been used.
     *
     * @return {@code true} if the OTP has been used, {@code false} otherwise.
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * Sets the status of whether the OTP has been used.
     *
     * @param used The new status of the OTP's usage.
     */
    public void setUsed(boolean used) {
        isUsed = used;
    }

    /**
     * Gets the OTP value, which is the hashed version of the OTP.
     *
     * @return The hashed OTP value.
     */
    public String getOtpValue() {
        return otpValue;
    }

    /**
     * Sets the OTP value (hashed).
     *
     * @param otpValue The new OTP value to be set.
     */
    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
    }

    /**
     * Returns the plain OTP value (before it is hashed).
     * <p>
     * This method is used to retrieve the OTP in its raw form before any hashing or encryption is applied.
     * </p>
     *
     * @return The plaintext OTP value.
     */
    public String getPlainOtp() {
        return plainOtp;
    }
}