package database.model.entities;

import database.model.BaseEntity;

/**
 * Represents a user in the system with personal and role-based information.
 * <p>
 * This class stores user details including username, first name, last name, password (hashed),
 * email address, and user roles. It extends {@link BaseEntity} to inherit a unique ID.
 * </p>
 *
 * @author Dhruv
 * @see BaseEntity
 */
public class User extends BaseEntity {
    private String userName;
    private String firstName;
    private String lastName;
    private String password; // stores the hashed password
    private String email;
    private int roles; // new bit field integer representing user roles

    /**
     * Default constructor for User.
     * Initializes a new instance of the User class.
     */
    public User() {
    }

    /**
     * Constructs a new User with the specified details.
     *
     * @param userName  The username for the user.
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @param password  The user's password (plain text initially).
     * @param email     The user's email address.
     * @param roles     The user's roles represented as an integer.
     */
    public User(String userName, String firstName, String lastName, String password, String email, int roles) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password; // plain text initially; repository will hash it
        this.email = email;
        this.roles = roles;
    }

    /**
     * Gets the user's username.
     *
     * @return The username of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's username.
     *
     * @param userName The new username for the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The new first name for the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The new last name for the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's password.
     *
     * @return The hashed password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The new password for the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's email.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email The new email address for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's roles.
     *
     * @return The roles of the user as an integer.
     */
    public int getRoles() {
        return roles;
    }

    /**
     * Sets the user's roles.
     *
     * @param roles The new roles for the user represented as an integer.
     */
    public void setRoles(int roles) {
        this.roles = roles;
    }
}