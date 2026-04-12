package core.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository contract for all entities.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface GenericRepository<T, ID> {

    /** Return all records. */
    List<T> findAll();

    /** Find by primary key. */
    Optional<T> findById(ID id);

    /** Persist a new entity. */
    void save(T entity);

    /** Merge (update) an existing entity. */
    void update(T entity);

    /** Delete by primary key. */
    void delete(ID id);
}
