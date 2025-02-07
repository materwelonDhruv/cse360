package src.database.model.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import src.database.model.BaseEntity;

public class User extends BaseEntity {
    private String firstName;
    private String lastName;
    private String userName;
    private String password; // stores the hashed password
    private String email;
    private int roles; // new bit field integer representing user roles

    //public User() {}

    public User(String userName, String password, String email, int roles) {
        this.userName = userName;
        this.password = password; // plain text initially; repository will hash it
        this.email = email;
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {

        return userName;
    }
    public StringProperty userNameProperty() {
        return new SimpleStringProperty(userName);
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

    public int getRoles() {
        return roles;
    }

    public void setRoles(int roles) {
        this.roles = roles;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

}

