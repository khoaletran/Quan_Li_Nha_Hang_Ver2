package network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClientConnection – phía client quản lý kết nối socket đến {@link MainServer}.
 *
 * <p><b>Chức năng:</b>
 * <ul>
 *   <li>Mở / đóng kết nối đến server</li>
 *   <li>Gửi {@link Request} và nhận {@link Response} đồng bộ</li>
 *   <li>Tự động reconnect nếu kết nối bị đứt (tuỳ chọn)</li>
 * </ul>
 *
 * <p><b>Cách dùng từ JavaFX Controller:</b>
 * <pre>{@code
 *   ClientConnection conn = ClientConnection.getInstance();
 *   conn.connect("localhost", 9999);
 *
 *   // PING
 *   Response pong = conn.send(new Request(CommandType.PING));
 *
 *   // LOGIN
 *   Response res = conn.send(
 *       Request.of(CommandType.LOGIN)
 *              .param("maNV",    "NV0001")
 *              .param("matKhau", "Secret@1")
 *              .build()
 *   );
 *
 *   if (res.isSuccess()) {
 *       NhanVienDTO dto = (NhanVienDTO) res.getData();
 *   }
 *
 *   conn.disconnect();
 * }</pre>
 *
 * <p><b>Thread safety:</b> Các cuộc gọi {@link #send} được đồng bộ hóa để tránh
 * xung đột khi nhiều thread cùng gửi request (ví dụ: background task JavaFX).
 */
public class ClientConnection {

    private static final Logger log = Logger.getLogger(ClientConnection.class.getName());

    // ── Singleton ─────────────────────────────────────────────────────────────

    /** Singleton instance – dùng chung trong toàn ứng dụng client. */
    private static volatile ClientConnection instance;

    public static ClientConnection getInstance() {
        if (instance == null) {
            synchronized (ClientConnection.class) {
                if (instance == null) instance = new ClientConnection();
            }
        }
        return instance;
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream  in;
    private boolean connected = false;

    /** Constructor private — dùng getInstance(). */
    private ClientConnection() {}

    // ── Connection lifecycle ──────────────────────────────────────────────────

    /**
     * Kết nối đến server.
     *
     * @param host hostname hoặc IP của server (vd: "localhost", "192.168.1.10")
     * @param port cổng server đang lắng nghe (vd: 9999)
     * @return true nếu kết nối thành công
     */
    public synchronized boolean connect(String host, int port) {
        if (connected) {
            log.info("[ClientConnection] Đã kết nối rồi, bỏ qua.");
            return true;
        }
        try {
            socket = new Socket(host, port);
            socket.setKeepAlive(true);          // giữ kết nối sống

            // ObjectOutputStream TRƯỚC để server không bị deadlock khi tạo OIS
            out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            in  = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            connected = true;
            log.info("[ClientConnection] Kết nối thành công → " + host + ":" + port);
            return true;

        } catch (IOException e) {
            log.log(Level.SEVERE, "[ClientConnection] Không thể kết nối đến " + host + ":" + port, e);
            return false;
        }
    }

    /**
     * Ngắt kết nối khỏi server (gửi DISCONNECT trước rồi đóng socket).
     */
    public synchronized void disconnect() {
        if (!connected) return;

        try {
            // Báo server biết client sẽ ngắt kết nối
            sendRaw(new Request(CommandType.DISCONNECT));
        } catch (Exception ignored) {
            // Bỏ qua nếu gửi không được (server đã chết, …)
        }

        closeStreams();
        log.info("[ClientConnection] Đã ngắt kết nối.");
    }

    /** Đóng stream và socket, reset trạng thái. */
    private void closeStreams() {
        connected = false;
        try { if (out    != null) out.close();    } catch (IOException ignored) {}
        try { if (in     != null) in.close();     } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    // ── Send / Receive ────────────────────────────────────────────────────────

    /**
     * Gửi một Request lên server và đợi Response trả về.
     *
     * <p>Phương thức synchronized: an toàn khi gọi từ nhiều thread.
     *
     * @param request Request cần gửi (không null)
     * @return Response từ server, hoặc Response.error nếu có sự cố kết nối
     */
    public synchronized Response send(Request request) {
        if (!connected) {
            return Response.error("Client chưa kết nối đến server.");
        }
        try {
            sendRaw(request);
            return receiveRaw();
        } catch (SocketException e) {
            log.log(Level.WARNING, "[ClientConnection] Mất kết nối khi gửi request.", e);
            closeStreams();
            return Response.error("Mất kết nối đến server.");
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.WARNING, "[ClientConnection] Lỗi I/O khi gửi/nhận.", e);
            return Response.error("Lỗi I/O: " + e.getMessage());
        }
    }

    /**
     * Ghi Request ra stream (không synchronized — gọi từ phương thức đã synchronized).
     */
    private void sendRaw(Request request) throws IOException {
        out.writeObject(request);
        out.flush();
        out.reset();   // xóa object cache — quan trọng khi gửi nhiều request liên tiếp
    }

    /**
     * Đọc Response từ stream.
     */
    private Response receiveRaw() throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof Response r) return r;
        throw new IOException("Nhận dữ liệu không hợp lệ từ server: " + obj);
    }

    // ── Status ────────────────────────────────────────────────────────────────

    /** Kiểm tra trạng thái kết nối. */
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    /**
     * Trả về địa chỉ server đang kết nối, hoặc null nếu chưa kết nối.
     */
    public String getServerAddress() {
        if (socket == null) return null;
        return socket.getRemoteSocketAddress().toString();
    }
}
