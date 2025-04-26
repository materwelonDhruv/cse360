package database.repository;

import database.model.BaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Generic repository interface for performing CRUD operations on entities extending {@link BaseEntity}.
 * <p>
 * This interface defines the standard methods for database operations such as create, retrieve, update, delete,
 * and build from {@link ResultSet}. Implementations of this interface should provide the specific logic for each method.
 * </p>
 *
 * @param <T> The type of entity managed by the repository, which must extend {@link BaseEntity}.
 * @author Dhruv
 */
public interface IRepository<T extends BaseEntity> {

    /**
     * Creates a new entity in the database.
     *
     * @param entity The entity to be created.
     * @return The created entity with an assigned ID.
     * @throws SQLException if an SQL error occurs during the creation process.
     */
    T create(T entity) throws SQLException;

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return The entity corresponding to the provided ID, or null if not found.
     * @throws SQLException if an SQL error occurs during the retrieval process.
     */
    T getById(int id) throws SQLException;

    /**
     * Retrieves all entities of the specified type from the database.
     *
     * @return A list of all entities.
     * @throws SQLException if an SQL error occurs during the retrieval process.
     */
    List<T> getAll() throws SQLException;

    /**
     * Builds an entity from a {@link ResultSet}.
     *
     * @param rs The result set containing the entity data.
     * @return The constructed entity.
     * @throws SQLException if an SQL error occurs while reading the result set.
     */
    T build(ResultSet rs) throws SQLException;

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The entity with updated data.
     * @return The updated entity.
     * @throws SQLException if an SQL error occurs during the update process.
     */
    T update(T entity) throws SQLException;

    /**
     * Deletes an entity from the database by its ID.
     *
     * @param id The ID of the entity to delete.
     * @throws SQLException if an SQL error occurs during the deletion process.
     */
    void delete(int id) throws SQLException;
}