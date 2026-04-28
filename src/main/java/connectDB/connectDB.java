package connectDB;

import infrastructure.db.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection utility.
 *
 * All connection details are read from {@link DatabaseConfig} (db.properties).
 * No credentials are hardcoded here.
 */
public class connectDB {

    private static Connection con = null;
    private static final connectDB instance = new connectDB();

    private connectDB() {}

    public static connectDB getInstance() {
        return instance;
    }

    /** Connects (or reconnects) to the database using settings from db.properties. */
    public void connect() throws SQLException {
        if (con == null || con.isClosed()) {
            try {
                con = DriverManager.getConnection(
                        DatabaseConfig.getUrl(),
                        DatabaseConfig.getUser(),
                        DatabaseConfig.getPassword());
                System.out.println("[DB] Đã kết nối MariaDB → QLNH_Ver2");
            } catch (SQLException e) {
                System.err.println("Kết nối thất bại: " + e.getMessage());
                throw e;
            }
        }
    }

    /** Closes the shared connection (rarely needed). */
    public void disconnect() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                con = null;
                System.out.println("Đã ngắt kết nối DB");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi ngắt kết nối: " + e.getMessage());
        }
    }

    /** Returns the active shared connection, reconnecting if necessary. */
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                instance.connect();
            }
        } catch (SQLException e) {
            System.err.println("Không thể tạo lại connection: " + e.getMessage());
        }
        return con;
    }

    /** Opens and returns a brand-new connection (caller must close it). */
    public Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword());
    }
}
