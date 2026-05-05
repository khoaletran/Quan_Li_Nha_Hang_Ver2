package network;

/**
 * Enum liệt kê tất cả các lệnh (command) được hỗ trợ trong giao tiếp client–server.
 *
 * Quy tắc đặt tên:
 *  - Nhóm SYSTEM  : PING, DISCONNECT
 *  - Nhóm AUTH    : LOGIN, LOGOUT
 *  - Nhóm NHAN_VIEN: NV_GET_ALL, NV_GET_BY_ID, NV_INSERT, NV_UPDATE, NV_DELETE
 *  - Nhóm MON     : MON_GET_ALL, MON_GET_BY_ID, MON_INSERT, MON_UPDATE, MON_DELETE
 *  - Nhóm HOA_DON : HD_GET_ALL, HD_GET_BY_ID, HD_INSERT, HD_CHECKOUT
 *  - Nhóm BAN     : BAN_GET_ALL, BAN_UPDATE_TRANG_THAI
 *  - Nhóm KHACH_HANG: KH_GET_ALL, KH_GET_BY_ID, KH_INSERT, KH_UPDATE
 *  - Nhóm KHUYEN_MAI: KM_GET_ALL, KM_GET_ACTIVE
 *  - Nhóm THONG_KE: TK_DOANH_THU, TK_MON_BAN_CHAY
 *
 * Thêm lệnh mới: chỉ cần thêm hằng số vào đây và tạo CommandHandler tương ứng.
 */
public enum CommandType {

    // ── SYSTEM ──────────────────────────────────────────────────────────────
    /** Kiểm tra kết nối; server trả về "pong". */
    PING,

    /** Client yêu cầu ngắt kết nối. */
    DISCONNECT,

    // ── AUTH ─────────────────────────────────────────────────────────────────
    /** Đăng nhập: payload chứa maNV + matKhau. */
    LOGIN,

    /** Đăng xuất phiên hiện tại. */
    LOGOUT,

    // ── NHÂN VIÊN ────────────────────────────────────────────────────────────
    NV_GET_ALL,
    NV_GET_BY_ID,
    NV_INSERT,
    NV_UPDATE,
    NV_DELETE,

    // ── MÓN ──────────────────────────────────────────────────────────────────
    MON_GET_ALL,
    MON_GET_BY_ID,
    MON_INSERT,
    MON_UPDATE,
    MON_DELETE,

    // ── HÓA ĐƠN ──────────────────────────────────────────────────────────────
    HD_GET_ALL,
    HD_GET_BY_ID,
    HD_INSERT,
    HD_CHECKOUT,

    // ── BÀN ──────────────────────────────────────────────────────────────────
    BAN_GET_ALL,
    BAN_UPDATE_TRANG_THAI,

    // ── KHÁCH HÀNG ────────────────────────────────────────────────────────────
    KH_GET_ALL,
    KH_GET_BY_ID,
    KH_INSERT,
    KH_UPDATE,

    // ── KHUYẾN MÃI ───────────────────────────────────────────────────────────
    KM_GET_ALL,
    KM_GET_ACTIVE,

    // ── THỐNG KÊ ─────────────────────────────────────────────────────────────
    TK_DOANH_THU,
    TK_MON_BAN_CHAY
}
