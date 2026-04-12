package core.dto;

import lombok.*;

/** DTO for KhuVuc. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuVucDTO {
    private String maKhuVuc;
    private String tenKhuVuc;
}
