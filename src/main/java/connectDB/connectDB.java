package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectDB {
    private static Connection con = null;
    private static final connectDB instance = new connectDB();

    private static final String URL = "jdbc:mariadb://localhost:3307/qlnh_ver2"
            + "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASSWORD = "280405";

    private connectDB() {
    }

    public static connectDB getInstance() {
        return instance;
    }

    // Kết nối lại nếu chưa có hoặc đã đóng
    public void connect() throws SQLException {
        if (con == null || con.isClosed()) {
            try {
                con = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Đã kết nối MariaDB → QLNH_Ver2");
            } catch (SQLException e) {
                System.err.println("Kết nối thất bại: " + e.getMessage());
                throw e;
            }
        }
    }

    // Ngắt kết nối thủ công (ít dùng)
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

    // Lấy connection đang hoạt động
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

    public Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
