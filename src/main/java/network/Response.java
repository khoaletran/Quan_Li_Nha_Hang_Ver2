package network;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Đối tượng Response được gửi từ Server về Client qua ObjectOutputStream.
 *
 * <p>Cấu trúc:
 * <ul>
 *   <li>{@code status}  – SUCCESS | ERROR | UNAUTHORIZED | NOT_FOUND</li>
 *   <li>{@code message} – mô tả ngắn kết quả (có thể null)</li>
 *   <li>{@code data}    – dữ liệu trả về (có thể null nếu chỉ cần status)</li>
 * </ul>
 *
 * <p>Ví dụ nhanh:
 * <pre>{@code
 *   Response.success("pong")                       // PING
 *   Response.success("Đăng nhập thành công", dto)  // LOGIN
 *   Response.error("Sai mật khẩu")                 // AUTH fail
 * }</pre>
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Status enum ───────────────────────────────────────────────────────────

    /** Trạng thái kết quả trả về từ server. */
    public enum Status {
        SUCCESS,
        ERROR,
        UNAUTHORIZED,
        NOT_FOUND
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final Status status;
    private final String message;

    /**
     * Dữ liệu payload trả về (entity, DTO, List, …).
     * Phải là Serializable để truyền qua socket.
     */
    private final Object data;

    /**
     * Metadata bổ sung (phân trang, tổng số, …).
     * Tuỳ chọn – có thể để null.
     */
    private final Map<String, Object> meta;

    // ── Private constructor ───────────────────────────────────────────────────

    private Response(Status status, String message, Object data, Map<String, Object> meta) {
        this.status  = status;
        this.message = message;
        this.data    = data;
        this.meta    = (meta != null) ? Collections.unmodifiableMap(new HashMap<>(meta)) : Collections.emptyMap();
    }

    // ── Static factory methods ────────────────────────────────────────────────

    /** Trả về SUCCESS kèm message ngắn (không có data). */
    public static Response success(String message) {
        return new Response(Status.SUCCESS, message, null, null);
    }

    /** Trả về SUCCESS kèm data. */
    public static Response success(String message, Object data) {
        return new Response(Status.SUCCESS, message, data, null);
    }

    /** Trả về SUCCESS kèm data và metadata (phân trang, …). */
    public static Response success(String message, Object data, Map<String, Object> meta) {
        return new Response(Status.SUCCESS, message, data, meta);
    }

    /** Trả về ERROR kèm message mô tả lỗi. */
    public static Response error(String message) {
        return new Response(Status.ERROR, message, null, null);
    }

    /** Trả về UNAUTHORIZED khi client chưa đăng nhập / không đủ quyền. */
    public static Response unauthorized(String message) {
        return new Response(Status.UNAUTHORIZED, message, null, null);
    }

    /** Trả về NOT_FOUND khi tài nguyên không tồn tại. */
    public static Response notFound(String message) {
        return new Response(Status.NOT_FOUND, message, null, null);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Status getStatus() { return status; }

    public String getMessage() { return message; }

    public Object getData() { return data; }

    public Map<String, Object> getMeta() { return meta; }

    /** Kiểm tra nhanh xem phản hồi có thành công không. */
    public boolean isSuccess() {
        return Status.SUCCESS == status;
    }

    @Override
    public String toString() {
        return "Response{status=" + status + ", message='" + message + "', data=" + data + "}";
    }
}
