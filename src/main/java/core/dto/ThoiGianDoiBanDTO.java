package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for ThoiGianDoiBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ThoiGianDoiBanDTO implements Serializable {
    private String maTGDB;
    private boolean loaiDatBan;
    private int thoiGian;
}
