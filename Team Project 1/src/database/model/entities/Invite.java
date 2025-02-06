package src.database.model.entities;

import src.database.model.BaseEntity;

public class Invite extends BaseEntity {
    private String code;
    private Integer userId; // The user who created the invite

    public Invite() {}

    public Invite(String code, Integer userId) {
        this.code = code;
        this.userId = userId;
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
}