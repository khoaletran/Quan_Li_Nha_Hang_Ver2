package core.dto;

import lombok.*;
import java.time.LocalDate;

/** DTO for NhanVien. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVienDTO {
    private String maNV;
    private String tenNV;
    private String sdt;
    private boolean gioiTinh;
    private boolean quanLi;
    private LocalDate ngayVaoLam;
    private boolean trangThai;
    /** Never expose raw password in DTO — keep empty or masked when reading. */
    private String matKhau;
}
