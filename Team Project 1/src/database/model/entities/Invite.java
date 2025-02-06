package src.database.model.entities;

import src.database.model.BaseEntity;

public class Invite extends BaseEntity {
    private String code;
    private Integer userId; // The user who created the invite
    private int roles;      // NEW: Bit field for roles associated with this invite
    private long createdAt; // NEW: Unix timestamp representing when the invite was created

    public Invite() {}

    public Invite(String code, Integer userId) {
        this.code = code;
        this.userId = userId;
        this.roles = 0;      // default value
        this.createdAt = System.currentTimeMillis(); // default to current time
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
}
