package network;

import network.handler.*;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClientHandler – chạy trên một Thread riêng, phục vụ một client kết nối.
 *
 * <p><b>Trách nhiệm duy nhất:</b>
 * <ol>
 *   <li>Đọc {@link Request} từ client</li>
 *   <li>Tra cứu {@link CommandHandler} phù hợp qua dispatcher</li>
 *   <li>Uỷ quyền xử lý — <em>KHÔNG</em> chứa business logic</li>
 *   <li>Gửi {@link Response} về cho client</li>
 * </ol>
 *
 * <p><b>Thêm command mới</b> (3 bước, không sửa logic hiện có):
 * <ol>
 *   <li>Thêm hằng số vào {@link CommandType}</li>
 *   <li>Tạo class implement {@link CommandHandler}</li>
 *   <li>Đăng ký trong {@link #buildDispatcher()}</li>
 * </ol>
 */
public class ClientHandler implements Runnable {

    private static final Logger log = Logger.getLogger(ClientHandler.class.getName());

    // ── Socket I/O ────────────────────────────────────────────────────────────
    private final Socket socket;
    private ObjectInputStream  in;
    private ObjectOutputStream out;

    /**
     * Map từ CommandType → CommandHandler.
     * Được khởi tạo một lần và dùng chung (thread-safe với unmodifiableMap).
     */
    private final Map<CommandType, CommandHandler> dispatcher;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ClientHandler(Socket socket) {
        this.socket     = socket;
        this.dispatcher = buildDispatcher();
    }

    // ── Dispatcher setup ──────────────────────────────────────────────────────

    /**
     * Đăng ký toàn bộ CommandHandler vào dispatcher.
     *
     * <p>Thêm handler mới: chỉ cần thêm một dòng {@code map.put(...)} ở đây.
     * Không cần sửa {@link #processRequest}.
     */
    private static Map<CommandType, CommandHandler> buildDispatcher() {
        Map<CommandType, CommandHandler> map = new EnumMap<>(CommandType.class);

        // ── System ────────────────────────────────────────────────────────────
        map.put(CommandType.PING,                    new PingHandler());
        map.put(CommandType.DISCONNECT,              new DisconnectHandler());

        // ── Auth ──────────────────────────────────────────────────────────────
        map.put(CommandType.LOGIN,                   new LoginHandler());

        // ── Nhân Viên ─────────────────────────────────────────────────────────
        map.put(CommandType.NV_GET_ALL,              new NvGetAllHandler());
        map.put(CommandType.NV_GET_BY_ID,            new NvGetByIdHandler());
        map.put(CommandType.NV_INSERT,               new NvInsertHandler());
        map.put(CommandType.NV_UPDATE,               new NvUpdateHandler());
        map.put(CommandType.NV_DELETE,               new NvDeleteHandler());

        // ── Món ───────────────────────────────────────────────────────────────
        map.put(CommandType.MON_GET_ALL,             new MonGetAllHandler());
        map.put(CommandType.MON_GET_BY_ID,           new MonGetByIdHandler());
        map.put(CommandType.MON_INSERT,              new MonInsertHandler());
        map.put(CommandType.MON_UPDATE,              new MonUpdateHandler());
        map.put(CommandType.MON_DELETE,              new MonDeleteHandler());

        // ── Hóa Đơn ───────────────────────────────────────────────────────────
        map.put(CommandType.HD_GET_ALL,              new HdGetAllHandler());
        map.put(CommandType.HD_GET_BY_ID,            new HdGetByIdHandler());
        map.put(CommandType.HD_INSERT,               new HdInsertHandler());
        map.put(CommandType.HD_CHECKOUT,             new HdCheckoutHandler());

        // ── Bàn ───────────────────────────────────────────────────────────────
        map.put(CommandType.BAN_GET_ALL,             new BanGetAllHandler());
        map.put(CommandType.BAN_UPDATE_TRANG_THAI,   new BanUpdateTrangThaiHandler());

        // ── Khách Hàng ────────────────────────────────────────────────────────
        map.put(CommandType.KH_GET_ALL,              new KhGetAllHandler());
        map.put(CommandType.KH_GET_BY_ID,            new KhGetByIdHandler());
        map.put(CommandType.KH_INSERT,               new KhInsertHandler());
        map.put(CommandType.KH_UPDATE,               new KhUpdateHandler());

        // ── Khuyến Mãi ────────────────────────────────────────────────────────
        map.put(CommandType.KM_GET_ALL,              new KmGetAllHandler());
        map.put(CommandType.KM_GET_ACTIVE,           new KmGetActiveHandler());

        // ── Thống Kê ──────────────────────────────────────────────────────────
        map.put(CommandType.TK_DOANH_THU,            new TkDoanhThuHandler());
        map.put(CommandType.TK_MON_BAN_CHAY,         new TkMonBanChayHandler());

        return java.util.Collections.unmodifiableMap(map);
    }

    // ── Main loop ─────────────────────────────────────────────────────────────

    @Override
    public void run() {
        String clientAddr = socket.getRemoteSocketAddress().toString();
        log.info("[ClientHandler] Kết nối từ: " + clientAddr);

        try {
            // Khởi tạo stream — ObjectOutputStream TRƯỚC để tránh deadlock
            out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.flush();
            in  = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            // Vòng lặp phục vụ: đọc request → dispatch → gửi response
            while (!socket.isClosed()) {
                Request request = readRequest();
                if (request == null) break;                     // client đóng kết nối

                log.fine("[ClientHandler] Nhận: " + request);
                Response response = processRequest(request);
                sendResponse(response);

                // Nếu client chủ động DISCONNECT → thoát vòng lặp
                if (request.getCommandType() == CommandType.DISCONNECT) {
                    log.info("[ClientHandler] Client yêu cầu ngắt kết nối.");
                    break;
                }
            }

        } catch (EOFException | java.net.SocketException e) {
            // Client đóng kết nối đột ngột — không phải lỗi nghiêm trọng
            log.info("[ClientHandler] Client ngắt kết nối: " + clientAddr);
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.WARNING, "[ClientHandler] Lỗi I/O: " + clientAddr, e);
        } finally {
            closeQuietly();
            log.info("[ClientHandler] Đã đóng kết nối: " + clientAddr);
        }
    }

    // ── Request / Response helpers ────────────────────────────────────────────

    /**
     * Đọc một Request từ stream.
     *
     * @return Request hoặc null nếu stream kết thúc.
     */
    private Request readRequest() throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof Request r) return r;
        log.warning("[ClientHandler] Nhận object không hợp lệ: " + obj);
        return null;
    }

    /**
     * Tra cứu và gọi CommandHandler phù hợp.
     * Không chứa bất kỳ business logic nào.
     *
     * @param request Request đã đọc từ client
     * @return Response sẽ gửi lại
     */
    private Response processRequest(Request request) {
        CommandType cmd     = request.getCommandType();
        CommandHandler handler = dispatcher.get(cmd);

        if (handler == null) {
            log.warning("[ClientHandler] Không tìm thấy handler cho: " + cmd);
            return Response.error("Lệnh '" + cmd + "' chưa được hỗ trợ.");
        }

        try {
            return handler.handle(request);
        } catch (Exception e) {
            log.log(Level.SEVERE, "[ClientHandler] Handler '" + cmd + "' ném ngoại lệ.", e);
            return Response.error("Lỗi server khi xử lý lệnh: " + cmd);
        }
    }

    /**
     * Gửi Response về client và flush buffer.
     */
    private void sendResponse(Response response) throws IOException {
        out.writeObject(response);
        out.flush();
        out.reset();   // tránh object cache gây dữ liệu cũ
    }

    /**
     * Đóng socket và stream, bỏ qua ngoại lệ.
     */
    private void closeQuietly() {
        try { if (in  != null) in.close();  } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
    }
}
