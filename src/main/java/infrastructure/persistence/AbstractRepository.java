package infrastructure.persistence;

import core.repository.GenericRepository;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Base JPA repository that implements GenericRepository CRUD via EntityManager API.
 * Extend this class and implement any additional query methods needed.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {

    protected final Class<T> entityClass;

    protected AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /** Returns a brand-new JPA EntityManager — caller must close it. */
    protected EntityManager openEntityManager() {
        return JpaConfig.getEntityManagerFactory().createEntityManager();
    }

    // ─── GenericRepository implementation ────────────────────────────────

    @Override
    public List<T> findAll() {
        try (EntityManager em = openEntityManager()) {
            String hql = "FROM " + entityClass.getSimpleName();
            return em.createQuery(hql, entityClass).getResultList();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (EntityManager em = openEntityManager()) {
            return Optional.ofNullable(em.find(entityClass, id));
        }
    }

    @Override
    public void save(T entity) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("save() failed for " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void update(T entity) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("update() failed for " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void delete(ID id) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("delete() failed for " + entityClass.getSimpleName(), e);
        }
    }

    // ─── Helpers for subclasses ───────────────────────────────────────────

    /** Execute a named JPQL query and return a list. */
    protected List<T> findByHql(String jpql, Object... params) {
        try (EntityManager em = openEntityManager()) {
            var query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }
            return query.getResultList();
        }
    }

    /** Execute a named JPQL query and return the first result. */
    protected Optional<T> findFirstByHql(String jpql, Object... params) {
        try (EntityManager em = openEntityManager()) {
            var query = em.createQuery(jpql, entityClass).setMaxResults(1);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }
            return query.getResultStream().findFirst();
        }
    }
}
