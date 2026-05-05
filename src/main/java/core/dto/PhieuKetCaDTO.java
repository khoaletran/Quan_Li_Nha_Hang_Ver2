package core.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/** DTO for PhieuKetCa — flattens NhanVien. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuKetCaDTO  implements Serializable {
    private String maPhieu;
    private boolean ca;
    private int soHoaDon;
    private double tienMat;
    private double tienCK;
    private double tienChenhLech;
    private LocalDateTime ngayKetCa;
    private LocalDateTime tgLogIn;
    private String moTa;
    // Flattened from NhanVien
    private String maNV;
    private String tenNV;
}
