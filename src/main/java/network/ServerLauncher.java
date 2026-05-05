package network;

/**
 * Entry point chạy Server độc lập — KHÔNG cần JavaFX.
 *
 * Chạy bằng:
 *   mvn exec:java -Dexec.mainClass="network.ServerLauncher"
 *
 * hoặc tạo Run Configuration trong IntelliJ với main class = network.ServerLauncher
 */
public class ServerLauncher {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   QUAN LY NHA HANG — SOCKET SERVER   ║");
        System.out.println("╚══════════════════════════════════════╝");

        // Khởi tạo JPA/DB trước khi lắng nghe
        try {
            infrastructure.db.JpaConfig.getEntityManagerFactory();
            System.out.println("[Server] Kết nối DB thành công.");
        } catch (Exception e) {
            System.err.println("[Server] KHÔNG kết nối được DB: " + e.getMessage());
            System.err.println("[Server] Server vẫn khởi động nhưng các lệnh DB sẽ thất bại.");
        }

        // Khởi động server
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : MainServer.DEFAULT_PORT;
        new MainServer(port).start();
    }
}
