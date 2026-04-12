package core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Phiếu kết ca (shift-end report).
 */
@Entity
@Table(name = "PhieuKetCa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maPhieu")
public class PhieuKetCa {

    @Id
    @Column(name = "maPhieu", length = 14)
    private String maPhieu;

    /**
     * false = ca sáng (morning shift).<br>
     * true  = ca tối (evening shift).
     */
    @Column(name = "ca")
    private boolean ca;

    @Column(name = "soHoaDon")
    private int soHoaDon;

    @Column(name = "tienMat")
    private double tienMat;

    @Column(name = "tienCK")
    private double tienCK;

    @Column(name = "tienChenhLech")
    private double tienChenhLech;

    @Column(name = "ngayKetCa")
    private LocalDateTime ngayKetCa;

    @Column(name = "tgLogIn")
    private LocalDateTime tgLogIn;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(500)")
    private String moTa;

    // ─── Relationships ────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;
}
