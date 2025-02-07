package src.database.model.entities;

import src.database.model.BaseEntity;
import src.utils.Helpers;

import java.security.SecureRandom;

public class Invite extends BaseEntity {
    private String code;
    private Integer userId; // The user who created the invite
    private int roles;      // NEW: Bit field for roles associated with this invite
    private long createdAt; // NEW: Unix timestamp representing when the invite was created

    public Invite() {
        this.code = Helpers.generateRandomCode(6, false);
        this.roles = 0;
        this.createdAt = Helpers.getCurrentTimeInSeconds();
    }

    public Invite(Integer userId) {
        this.code = Helpers.generateRandomCode(6, false);
        this.userId = userId;
        this.roles = 0;
        this.createdAt = Helpers.getCurrentTimeInSeconds();
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getRoles() {
        return roles;
    }
    public void setRoles(int roles) {
        this.roles = roles;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
<<<<<<< HEAD
}
=======

    private String generateCode() {
        // Generate a random 6-character alphanumeric code
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}
>>>>>>> origin/feat/DatabaseUpdate
