package infrastructure.persistence;

import core.repository.GenericRepository;
import infrastructure.db.HibernateConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Base Hibernate repository that implements GenericRepository CRUD via Session API.
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

    /** Returns a brand-new Hibernate Session — caller must close it. */
    protected Session openSession() {
        return HibernateConfig.getSessionFactory().openSession();
    }

    // ─── GenericRepository implementation ────────────────────────────────

    @Override
    public List<T> findAll() {
        try (Session session = openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            return session.createQuery(hql, entityClass).list();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = openSession()) {
            return Optional.ofNullable(session.get(entityClass, id));
        }
    }

    @Override
    public void save(T entity) {
        Transaction tx = null;
        try (Session session = openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("save() failed for " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void update(T entity) {
        Transaction tx = null;
        try (Session session = openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("update() failed for " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void delete(ID id) {
        Transaction tx = null;
        try (Session session = openSession()) {
            tx = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("delete() failed for " + entityClass.getSimpleName(), e);
        }
    }

    // ─── Helpers for subclasses ───────────────────────────────────────────

    /** Execute a named HQL query and return a list. */
    protected List<T> findByHql(String hql, Object... params) {
        try (Session session = openSession()) {
            var query = session.createQuery(hql, entityClass);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }
            return query.list();
        }
    }

    /** Execute a named HQL query and return the first result. */
    protected Optional<T> findFirstByHql(String hql, Object... params) {
        try (Session session = openSession()) {
            var query = session.createQuery(hql, entityClass).setMaxResults(1);
            for (int i = 0; i < params.length; i += 2) {
                query.setParameter((String) params[i], params[i + 1]);
            }
            return query.uniqueResultOptional();
        }
    }
}
