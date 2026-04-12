package core.dto;

import lombok.*;

/** DTO for SuKien. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SuKienDTO {
    private String maSK;
    private String tenSK;
    private String moTa;
    private double gia;
}
