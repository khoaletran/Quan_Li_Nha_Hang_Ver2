package core.dto;

import lombok.*;

/** DTO for LoaiBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoaiBanDTO {
    private String maLoaiBan;
    private String tenLoaiBan;
    private int soLuong;
}
