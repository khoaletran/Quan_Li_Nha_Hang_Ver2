package network.handler;

import network.Request;
import network.Response;

/**
 * Handler cho lệnh PING.
 *
 * <p>Mục đích: kiểm tra server còn sống và kết nối hoạt động bình thường.
 *
 * <p>Request : {@code CommandType.PING} — không cần payload.
 * <p>Response: {@code SUCCESS, message = "pong"}.
 */
public class PingHandler implements CommandHandler {

    @Override
    public Response handle(Request request) {
        // Không cần đọc payload, chỉ phản hồi đơn giản.
        return Response.success("pong");
    }
}
