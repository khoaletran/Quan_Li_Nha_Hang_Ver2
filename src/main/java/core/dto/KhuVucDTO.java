package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for KhuVuc. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuVucDTO  implements Serializable {
    private String maKhuVuc;
    private String tenKhuVuc;
}
