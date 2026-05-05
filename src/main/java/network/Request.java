package network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Đối tượng Request được gửi từ Client lên Server qua ObjectOutputStream.
 *
 * <p>Thiết kế:
 * <ul>
 *   <li>{@code commandType} – loại lệnh (bắt buộc, không null)</li>
 *   <li>{@code payload}     – dữ liệu kèm theo, key-value linh hoạt (có thể null)</li>
 * </ul>
 *
 * <p>Ví dụ sử dụng:
 * <pre>{@code
 *   // PING
 *   Request ping = new Request(CommandType.PING);
 *
 *   // LOGIN
 *   Request login = Request.of(CommandType.LOGIN)
 *                          .param("maNV",    "NV0001")
 *                          .param("matKhau", "Secret@1")
 *                          .build();
 * }</pre>
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Loại lệnh cần thực thi. */
    private final CommandType commandType;

    /**
     * Dữ liệu đi kèm lệnh.
     * Giá trị có thể là bất kỳ Serializable nào (String, DTO, List, …).
     */
    private final Map<String, Object> payload;

    // ── Constructors ─────────────────────────────────────────────────────────

    /** Tạo request không có payload (ví dụ: PING, LOGOUT). */
    public Request(CommandType commandType) {
        this(commandType, new HashMap<>());
    }

    /** Tạo request với payload đã có sẵn. */
    public Request(CommandType commandType, Map<String, Object> payload) {
        if (commandType == null) throw new IllegalArgumentException("commandType không được null.");
        this.commandType = commandType;
        this.payload = (payload != null) ? new HashMap<>(payload) : new HashMap<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public CommandType getCommandType() {
        return commandType;
    }

    /** Trả về toàn bộ payload map (read-only view). */
    public Map<String, Object> getPayload() {
        return java.util.Collections.unmodifiableMap(payload);
    }

    /**
     * Lấy giá trị theo key, trả về null nếu không tồn tại.
     * Dùng trong CommandHandler để đọc tham số.
     */
    public Object getParam(String key) {
        return payload.get(key);
    }

    /**
     * Lấy giá trị dạng String theo key.
     * Trả về null nếu key không tồn tại hoặc giá trị không phải String.
     */
    public String getStringParam(String key) {
        Object val = payload.get(key);
        return (val instanceof String s) ? s : (val != null ? val.toString() : null);
    }

    // ── Builder ───────────────────────────────────────────────────────────────

    /** Trả về Builder để xây dựng Request theo kiểu fluent. */
    public static Builder of(CommandType commandType) {
        return new Builder(commandType);
    }

    /** Builder nội bộ, hỗ trợ fluent API. */
    public static final class Builder {
        private final CommandType commandType;
        private final Map<String, Object> payload = new HashMap<>();

        private Builder(CommandType commandType) {
            this.commandType = commandType;
        }

        /** Thêm một tham số vào payload. */
        public Builder param(String key, Object value) {
            payload.put(key, value);
            return this;
        }

        /** Tạo đối tượng Request hoàn chỉnh. */
        public Request build() {
            return new Request(commandType, payload);
        }
    }

    @Override
    public String toString() {
        return "Request{commandType=" + commandType + ", payload=" + payload + "}";
    }
}
