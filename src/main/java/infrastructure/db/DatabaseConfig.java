package infrastructure.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized database configuration loader.
 *
 * Reads {@code db.properties} from the classpath once at startup and exposes
 * typed getters used by every component that needs a connection (JPA, JDBC).
 *
 * To change the database host, port, username, or password, edit
 * {@code src/main/resources/db.properties} — no Java source files need to change.
 */
public final class DatabaseConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (in == null) {
                throw new ExceptionInInitializerError(
                        "[DatabaseConfig] db.properties not found in classpath. " +
                        "Make sure src/main/resources/db.properties exists.");
            }
            PROPS.load(in);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "[DatabaseConfig] Failed to load db.properties: " + e.getMessage());
        }
    }

    // Prevent instantiation
    private DatabaseConfig() {}

    /** Full JDBC URL including host, port, database name, and query parameters. */
    public static String getUrl() {
        return PROPS.getProperty("db.url");
    }

    /** Database username. */
    public static String getUser() {
        return PROPS.getProperty("db.user");
    }

    /** Database password. */
    public static String getPassword() {
        return PROPS.getProperty("db.password");
    }

    /** Fully-qualified JDBC driver class name. */
    public static String getDriver() {
        return PROPS.getProperty("db.driver");
    }
}
