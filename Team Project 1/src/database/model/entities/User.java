package src.database.model.entities;

import src.database.model.BaseEntity;

public class User extends BaseEntity {
    private String userName;
    private String password; // Will store the hashed password
    private String email;
    private Integer inviteUsed; // May be null

    public User() {}

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password; // plain text initially; will be hashed by the repository
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getInviteUsed() {
        return inviteUsed;
    }
    public void setInviteUsed(Integer inviteUsed) {
        this.inviteUsed = inviteUsed;
    }
}