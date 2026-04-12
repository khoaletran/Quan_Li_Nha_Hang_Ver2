package core.dto;

import lombok.*;

/** DTO for ThoiGianDoiBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ThoiGianDoiBanDTO {
    private String maTGDB;
    private boolean loaiDatBan;
    private int thoiGian;
}
