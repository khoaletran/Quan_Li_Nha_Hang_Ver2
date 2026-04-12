package core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Nhân viên (employee).
 */
@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maNV")
public class NhanVien {

    @Id
    @Column(name = "maNV", length = 6)
    private String maNV;

    @Column(name = "tenNV", columnDefinition = "NVARCHAR(200)")
    private String tenNV;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Column(name = "gioiTinh")
    private boolean gioiTinh;

    @Column(name = "quanLi")
    private boolean quanLi;

    @Column(name = "ngayVaoLam")
    private LocalDate ngayVaoLam;

    @Column(name = "trangThai")
    private boolean trangThai;

    @Column(name = "matKhau", length = 255)
    private String matKhau;
}
