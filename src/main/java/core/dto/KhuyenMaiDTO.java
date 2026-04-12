package core.dto;

import lombok.*;
import java.time.LocalDate;

/** DTO for KhuyenMai. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuyenMaiDTO {
    private String maKM;
    private String tenKM;
    private int soLuong;
    private LocalDate ngayPhatHanh;
    private LocalDate ngayKetThuc;
    private String maThayThe;
    private int phanTramGiamGia;
    private boolean uuDai;
}
