package core.dto;

import lombok.*;

/** DTO for LoaiMon. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoaiMonDTO {
    private String maLoaiMon;
    private String tenLoaiMon;
    private String moTa;
}
