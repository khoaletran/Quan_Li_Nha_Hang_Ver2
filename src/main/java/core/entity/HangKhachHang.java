package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Hạng khách hàng (VIP levels).
 */
@Entity
@Table(name = "HangKhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maHang")
@ToString(exclude = "khachHangs")
public class HangKhachHang {

    @Id
    @Column(name = "maHang", length = 6)
    private String maHang;

    @Column(name = "diemHang")
    private int diemHang;

    @Column(name = "giamGia")
    private int giamGia;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(500)")
    private String moTa;

    // ─── Relationships ────────────────────────────────────────────────────

    @JsonIgnore
    @OneToMany(mappedBy = "hangKhachHang", fetch = FetchType.LAZY)
    private List<KhachHang> khachHangs;
}
