package network.handler;

import network.Request;
import network.Response;

/**
 * Handler cho lệnh DISCONNECT.
 *
 * <p>Server không cần làm gì đặc biệt — chỉ phản hồi để client biết
 * server đã nhận tín hiệu trước khi đóng socket.
 * Thực tế đóng kết nối được thực hiện trong {@link network.ClientHandler}.
 */
public class DisconnectHandler implements CommandHandler {

    @Override
    public Response handle(Request request) {
        return Response.success("Đã ngắt kết nối.");
    }
}
