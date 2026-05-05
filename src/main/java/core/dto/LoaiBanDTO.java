package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for LoaiBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoaiBanDTO implements Serializable {
    private String maLoaiBan;
    private String tenLoaiBan;
    private int soLuong;
}
