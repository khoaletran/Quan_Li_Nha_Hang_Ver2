package network.handler;

import network.Request;
import network.Response;

/**
 * Interface cốt lõi của Command–Handler pattern.
 *
 * <p>Mỗi implementation chịu trách nhiệm xử lý <em>đúng một</em> CommandType.
 * ClientHandler sẽ tra cứu handler phù hợp qua Map&lt;CommandType, CommandHandler&gt;
 * và uỷ quyền toàn bộ logic nghiệp vụ cho handler đó.
 *
 * <p>Lợi ích:
 * <ul>
 *   <li>Tuân thủ Single Responsibility Principle (SRP)</li>
 *   <li>Dễ kiểm thử độc lập từng handler</li>
 *   <li>Mở rộng command mới không cần sửa ClientHandler (Open/Closed Principle)</li>
 * </ul>
 *
 * <p>Ví dụ implement:
 * <pre>{@code
 * public class PingHandler implements CommandHandler {
 *     public Response handle(Request request) {
 *         return Response.success("pong");
 *     }
 * }
 * }</pre>
 */
public interface CommandHandler {

    /**
     * Xử lý request và trả về response tương ứng.
     *
     * @param request Yêu cầu từ client (không null, đã được dispatcher kiểm tra)
     * @return Response sẽ được gửi ngược lại cho client
     */
    Response handle(Request request);
}
