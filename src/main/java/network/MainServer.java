package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MainServer – điểm khởi động của phía Server trong kiến trúc Client–Server.
 *
 * <p><b>Chức năng:</b>
 * <ul>
 *   <li>Mở cổng lắng nghe (mặc định {@value #DEFAULT_PORT})</li>
 *   <li>Chấp nhận kết nối từ client</li>
 *   <li>Tạo {@link ClientHandler} và giao cho ThreadPool xử lý</li>
 *   <li>Hỗ trợ graceful shutdown</li>
 * </ul>
 *
 * <p><b>Cách chạy:</b>
 * <pre>{@code
 *   // Cổng mặc định 9999
 *   MainServer server = new MainServer();
 *   server.start();
 *
 *   // Cổng tùy chỉnh
 *   MainServer server = new MainServer(8080);
 *   server.start();
 * }</pre>
 *
 * <p><b>Thread pool:</b> Sử dụng {@link Executors#newCachedThreadPool()} —
 * tạo thread mới khi cần, tái sử dụng thread nhàn rỗi. Phù hợp cho số lượng
 * client vừa phải (< vài trăm kết nối đồng thời).
 * Thay bằng {@link Executors#newFixedThreadPool(int)} nếu muốn giới hạn thread.
 */
public class MainServer {

    private static final Logger log = Logger.getLogger(MainServer.class.getName());

    /** Cổng mặc định nếu không truyền vào constructor. */
    public static final int DEFAULT_PORT = 9999;

    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = false;

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Tạo server với cổng mặc định {@value #DEFAULT_PORT}. */
    public MainServer() {
        this(DEFAULT_PORT);
    }

    /** Tạo server với cổng tùy chỉnh. */
    public MainServer(int port) {
        this.port = port;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Khởi động server: mở socket, bắt đầu vòng lặp chấp nhận kết nối.
     * Phương thức này block thread gọi nó cho đến khi {@link #stop()} được gọi.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            threadPool   = Executors.newCachedThreadPool();
            running      = true;

            log.info("══════════════════════════════════════════");
            log.info("  Server khởi động thành công tại cổng " + port);
            log.info("  Đang chờ kết nối từ client…");
            log.info("══════════════════════════════════════════");

            // Đăng ký shutdown hook để dọn dẹp khi JVM thoát (Ctrl+C)
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            acceptLoop();

        } catch (IOException e) {
            log.log(Level.SEVERE, "Không thể khởi động server trên cổng " + port, e);
        }
    }

    /**
     * Vòng lặp chính: chấp nhận kết nối và giao cho ClientHandler.
     */
    private void acceptLoop() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("[MainServer] Client kết nối: " + clientSocket.getRemoteSocketAddress());

                // Mỗi client chạy trên một thread riêng qua thread pool
                threadPool.execute(new ClientHandler(clientSocket));

            } catch (IOException e) {
                if (running) {
                    log.log(Level.WARNING, "[MainServer] Lỗi khi chấp nhận kết nối.", e);
                }
                // Nếu !running → server đã dừng, bỏ qua lỗi
            }
        }
    }

    /**
     * Dừng server: đóng ServerSocket, tắt thread pool gracefully.
     * An toàn khi gọi nhiều lần.
     */
    public void stop() {
        if (!running) return;
        running = false;

        log.info("[MainServer] Đang dừng server…");

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();   // ngắt accept() đang block
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Lỗi khi đóng ServerSocket.", e);
        }

        if (threadPool != null) {
            threadPool.shutdown();
            try {
                // Đợi tối đa 10 giây để các request đang xử lý hoàn thành
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                    log.warning("[MainServer] Thread pool bị force-shutdown sau 10 giây.");
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("[MainServer] Server đã dừng hoàn toàn.");
    }

    /** Trả về cổng server đang chạy. */
    public int getPort() {
        return port;
    }

    /** Kiểm tra server có đang chạy không. */
    public boolean isRunning() {
        return running;
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    /**
     * Chạy server độc lập (không phụ thuộc JavaFX).
     * Để chạy server song song với JavaFX, gọi {@link #start()} trong một thread riêng.
     */
    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new MainServer(port).start();
    }
}
