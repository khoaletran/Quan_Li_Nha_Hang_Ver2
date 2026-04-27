package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Khuyến mãi (promotion/voucher).
 */
@Entity
@Table(name = "KhuyenMai")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maKM")
@ToString(exclude = "hoaDons")
public class KhuyenMai {

    @Id
    @Column(name = "maKM", length = 6)
    private String maKM;

    @Column(name = "tenKM", columnDefinition = "NVARCHAR(200)")
    private String tenKM;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "ngayPhatHanh")
    private java.time.LocalDate ngayPhatHanh;

    @Column(name = "ngayKetThuc")
    private java.time.LocalDate ngayKetThuc;

    @Column(name = "maThayThe", columnDefinition = "NVARCHAR(200)")
    private String maThayThe;

    /** Percentage discount OR fixed amount (depending on uuDai flag). */
    @Column(name = "phanTramGiamGia")
    private int phanTramGiamGia;

    /**
     * true = fixed money discount (VND amount).<br>
     * false = percentage discount.
     */
    @Column(name = "uuDai")
    private boolean uuDai;

    // ─── Relationships ────────────────────────────────────────────────────

    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;
}
