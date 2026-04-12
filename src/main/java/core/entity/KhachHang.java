package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Khách hàng (customer).
 */
@Entity
@Table(name = "KhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maKH")
@ToString(exclude = "hoaDons")
public class KhachHang {

    @Id
    @Column(name = "maKH", length = 6)
    private String maKH;

    @Column(name = "tenKH", columnDefinition = "NVARCHAR(200)")
    private String tenKH;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Column(name = "gioiTinh")
    private boolean gioiTinh;

    @Column(name = "diemTichLuy")
    private int diemTichLuy;

    // ─── Relationships ────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHang")
    private HangKhachHang hangKhachHang;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;
}
