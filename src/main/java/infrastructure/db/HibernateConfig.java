package infrastructure.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Singleton Hibernate SessionFactory.
 * Call HibernateConfig.getSessionFactory() to obtain the factory.
 * Call HibernateConfig.shutdown() on application exit.
 */
public class HibernateConfig {

    private static volatile SessionFactory sessionFactory;

    private HibernateConfig() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            synchronized (HibernateConfig.class) {
                if (sessionFactory == null || sessionFactory.isClosed()) {
                    try {
                        Configuration config = new Configuration();
                        config.configure("hibernate.cfg.xml");
                        sessionFactory = config.buildSessionFactory();
                        System.out.println("[Hibernate] SessionFactory initialized successfully.");
                    } catch (Exception ex) {
                        System.err.println("[Hibernate] SessionFactory initialization failed: " + ex.getMessage());
                        throw new ExceptionInInitializerError(ex);
                    }
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("[Hibernate] SessionFactory closed.");
        }
    }
}
