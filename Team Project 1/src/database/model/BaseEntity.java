package database.model;

/**
 * Abstract class representing a base entity with a common primary key field.
 * <p>
 * This class is intended to be extended by other entities that require a primary key.
 * It provides a common {@code id} field along with getter and setter methods for accessing and modifying it.
 *
 * @author Dhruv
 */
public abstract class BaseEntity {
    protected int id; // Common primary key field

    /**
     * Gets the ID of the entity.
     *
     * @return The ID of the entity as an {@code int}.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the entity.
     *
     * @param id The ID to be set for the entity.
     */
    public void setId(int id) {
        this.id = id;
    }
}