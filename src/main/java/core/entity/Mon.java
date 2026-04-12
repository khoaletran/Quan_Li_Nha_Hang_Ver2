package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Món ăn (menu item).
 * All pricing logic has been moved to MonService.
 */
@Entity
@Table(name = "Mon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maMon")
@ToString(exclude = {"chiTietHoaDons", "phanTramGiaBans"})
public class Mon {

    @Id
    @Column(name = "maMon", length = 6)
    private String maMon;

    @Column(name = "tenMon", columnDefinition = "NVARCHAR(200)")
    private String tenMon;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(500)")
    private String moTa;

    @Column(name = "hinhAnh", length = 500)
    private String hinhAnh;

    @Column(name = "giaGoc")
    private double giaGoc;

    @Column(name = "soLuong")
    private int soLuong;

    // ─── Relationships ────────────────────────────────────────────────────

    /**
     * FK column in Mon table is named "loaiMon" referencing LoaiMon.maLoaiMon.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiMon", referencedColumnName = "maLoaiMon")
    private LoaiMon loaiMon;

    @JsonIgnore
    @OneToMany(mappedBy = "mon", fetch = FetchType.LAZY)
    private List<ChiTietHoaDon> chiTietHoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "mon", fetch = FetchType.LAZY)
    private List<PhanTramGiaBan> phanTramGiaBans;
}
