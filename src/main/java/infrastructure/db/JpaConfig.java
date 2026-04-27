package infrastructure.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton JPA EntityManagerFactory.
 * Call JpaConfig.getEntityManagerFactory() to obtain the factory.
 * Call JpaConfig.shutdown() on application exit.
 */
public class JpaConfig {

    private static volatile EntityManagerFactory entityManagerFactory;

    private JpaConfig() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            synchronized (JpaConfig.class) {
                if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
                    try {
                        entityManagerFactory = Persistence.createEntityManagerFactory("mariadb-pu");
                        System.out.println("[JPA] EntityManagerFactory initialized successfully.");
                    } catch (Exception ex) {
                        System.err.println("[JPA] EntityManagerFactory initialization failed: " + ex.getMessage());
                        throw new ExceptionInInitializerError(ex);
                    }
                }
            }
        }
        return entityManagerFactory;
    }

    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("[JPA] EntityManagerFactory closed.");
        }
    }
}
