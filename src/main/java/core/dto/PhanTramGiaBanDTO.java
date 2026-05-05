package core.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/** DTO for PhanTramGiaBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PhanTramGiaBanDTO  implements Serializable {
    private String maPTGB;
    private int phanTramLoi;
    private LocalDateTime ngayApDung;
    // Flattened FKs
    private String maMon;
    private String tenMon;
    private String maLoaiMon;
    private String tenLoaiMon;
}
