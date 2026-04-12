package core.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO for HoaDon — fully flattened for controller use.
 * Computed billing fields are populated by HoaDonService.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonDTO {

    private String maHD;
    private LocalDateTime tgLapHD;
    private LocalDateTime tgCheckIn;
    private LocalDateTime tgCheckout;
    private boolean kieuThanhToan;
    private boolean kieuDatBan;
    private int trangThai;
    private int soLuong;
    private String moTa;

    // ── Flattened: KhachHang ──────────────────────────────────────────────
    private String maKH;
    private String tenKH;
    private String sdtKH;
    private boolean gioiTinhKH;
    private int diemTichLuy;

    // ── Flattened: HangKhachHang ──────────────────────────────────────────
    private String maHang;
    private int giamGiaHang;       // % discount for loyalty tier

    // ── Flattened: NhanVien ───────────────────────────────────────────────
    private String maNV;
    private String tenNV;

    // ── Flattened: Ban ────────────────────────────────────────────────────
    private String maBan;
    private String maKhuVuc;
    private String tenKhuVuc;
    private String maLoaiBan;
    private String tenLoaiBan;

    // ── Flattened: KhuyenMai ─────────────────────────────────────────────
    private String maKM;
    private String tenKM;
    private int phanTramGiamGia;   // % or fixed amount
    private boolean uuDai;

    // ── Flattened: SuKien ────────────────────────────────────────────────
    private String maSK;
    private String tenSK;
    private double giaSuKien;

    // ── Computed billing (populated by HoaDonService) ─────────────────────
    private double tongTienTruoc;
    private double tienMaKM;       // discount from voucher
    private double tienHangKM;     // discount from loyalty tier
    private double tongTienKhuyenMai;
    private double thue;
    private double coc;
    private double tongTienSau;
}
