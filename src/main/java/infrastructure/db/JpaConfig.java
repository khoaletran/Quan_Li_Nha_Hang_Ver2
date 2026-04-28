package infrastructure.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton JPA EntityManagerFactory.
 *
 * Connection credentials are read from {@link DatabaseConfig} (which loads
 * {@code db.properties}) and injected programmatically, so persistence.xml
 * does NOT need to contain any hardcoded credentials.
 *
 * Call {@link #getEntityManagerFactory()} to obtain the factory.
 * Call {@link #shutdown()} on application exit.
 */
public class JpaConfig {

    private static volatile EntityManagerFactory entityManagerFactory;

    private JpaConfig() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            synchronized (JpaConfig.class) {
                if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
                    try {
                        entityManagerFactory = Persistence.createEntityManagerFactory(
                                "mariadb-pu", buildJpaProperties());
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

    /**
     * Builds JPA overrides from {@link DatabaseConfig}.
     * These values override any matching properties in persistence.xml.
     */
    private static Map<String, Object> buildJpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.driver",   DatabaseConfig.getDriver());
        props.put("jakarta.persistence.jdbc.url",      DatabaseConfig.getUrl());
        props.put("jakarta.persistence.jdbc.user",     DatabaseConfig.getUser());
        props.put("jakarta.persistence.jdbc.password", DatabaseConfig.getPassword());
        return props;
    }

    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("[JPA] EntityManagerFactory closed.");
        }
    }
}
