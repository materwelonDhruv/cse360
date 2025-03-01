package database.repository;

import database.model.BaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IRepository<T extends BaseEntity> {
    T create(T entity) throws SQLException;

    T getById(int id) throws SQLException;

    List<T> getAll() throws SQLException;

    T build(ResultSet rs) throws SQLException;

    T update(T entity) throws SQLException;

    void delete(int id) throws SQLException;
}