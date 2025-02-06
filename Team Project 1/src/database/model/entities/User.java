package src.database.model.entities;

import src.database.model.BaseEntity;

public class User extends BaseEntity {
    private String userName;
    private String password; // stores the hashed password
    private String email;
    private Integer inviteUsed; // may be null
    private int roles; // new bit field integer representing user roles

    public User() {}

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password; // plain text initially; repository will hash it
        this.email = email;
        this.roles = 0; // default no roles set
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

    public int getRoles() {
        return roles;
    }

    public void setRoles(int roles) {
        this.roles = roles;
    }
}
