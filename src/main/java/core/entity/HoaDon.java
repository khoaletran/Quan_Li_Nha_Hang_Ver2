package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Hóa đơn (invoice/bill).
 * All business logic (tổng tiền, cọc, khuyến mãi) has been moved to HoaDonService.
 */
@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maHD")
@ToString(exclude = "chiTietHoaDons")
public class HoaDon {

    @Id
    @Column(name = "maHD", length = 14)
    private String maHD;

    @Column(name = "tgLapHD")
    private LocalDateTime tgLapHD;

    @Column(name = "tgCheckIn")
    private LocalDateTime tgCheckIn;

    @Column(name = "tgCheckout")
    private LocalDateTime tgCheckout;

    @Column(name = "kieuThanhToan")
    private boolean kieuThanhToan;

    @Column(name = "kieuDatBan")
    private boolean kieuDatBan;

    /** 0=chờ, 1=đang dùng, 2=hoàn tất */
    @Column(name = "trangThai")
    private int trangThai;

    /** Số lượng khách (number of guests). */
    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(500)")
    private String moTa;

    // ─── Relationships ────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maBan")
    private Ban ban;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKM")
    private KhuyenMai khuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSK")
    private SuKien suKien;

    @JsonIgnore
    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private List<ChiTietHoaDon> chiTietHoaDons;
}
