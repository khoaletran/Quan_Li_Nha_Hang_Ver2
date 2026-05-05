package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for SuKien. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SuKienDTO  implements Serializable {
    private String maSK;
    private String tenSK;
    private String moTa;
    private double gia;
}
