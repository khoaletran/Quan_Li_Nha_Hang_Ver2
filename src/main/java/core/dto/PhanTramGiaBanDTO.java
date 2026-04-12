package core.dto;

import lombok.*;
import java.time.LocalDateTime;

/** DTO for PhanTramGiaBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PhanTramGiaBanDTO {
    private String maPTGB;
    private int phanTramLoi;
    private LocalDateTime ngayApDung;
    // Flattened FKs
    private String maMon;
    private String tenMon;
    private String maLoaiMon;
    private String tenLoaiMon;
}
