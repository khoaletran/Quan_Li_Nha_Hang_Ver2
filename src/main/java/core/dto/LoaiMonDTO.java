package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for LoaiMon. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoaiMonDTO implements Serializable {
    private String maLoaiMon;
    private String tenLoaiMon;
    private String moTa;
}
