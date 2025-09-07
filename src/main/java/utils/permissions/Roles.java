package utils.permissions;

/**
 * This enumeration defines various user roles within the system.
 * Each role is represented by a unique bit value for permission handling via bitwise operations.
 *
 * @author Dhruv
 */
public enum Roles {
    /**
     * Represents a standard user role.
     */
    USER(1),

    /**
     * Represents an administrator role.
     */
    ADMIN(1 << 1),

    /**
     * Represents an instructor role.
     */
    INSTRUCTOR(1 << 2),

    /**
     * Represents a student role.
     */
    STUDENT(1 << 3),

    /**
     * Represents a reviewer role.
     */
    REVIEWER(1 << 4),

    /**
     * Represents a staff role.
     */
    STAFF(1 << 5);

    private final int bit;

    /**
     * Constructs a role with the specified bit value.
     *
     * @param bit The bit value representing the role.
     */
    Roles(int bit) {
        this.bit = bit;
    }

    /**
     * Retrieves the bit value associated with the role.
     *
     * @return The integer bit value representing the role.
     */
    public int getBit() {
        return bit;
    }

    /**
     * Returns the name of the role with the first letter capitalized.
     *
     * @return A formatted string representation of the role name.
     */
    @Override
    public String toString() {
        String name = this.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}