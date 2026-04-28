package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Chi tiết hóa đơn (order line item).
 * Composite PK: (maHD, maMon).
 */
@Entity
@Table(name = "ChiTietHoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"hoaDon", "mon"})
public class ChiTietHoaDon {

    @EmbeddedId
    private ChiTietHoaDonId id;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "thanhTien")
    private Double thanhTien;   // Nullable — computed on-the-fly when null (insert không set cột này)

    // ─── Relationships ────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHD")
    @JoinColumn(name = "maHD")
    @JsonIgnore
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maMon")
    @JoinColumn(name = "maMon")
    private Mon mon;
}
