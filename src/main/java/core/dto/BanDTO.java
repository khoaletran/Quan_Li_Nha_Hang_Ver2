package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for Ban — flattens LoaiBan and KhuVuc. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BanDTO implements Serializable {
    private String maBan;
    private boolean trangThai;

    // Flattened from LoaiBan
    private String maLoaiBan;
    private String tenLoaiBan;
    private int soLuongGhe;

    // Flattened from KhuVuc
    private String maKhuVuc;
    private String tenKhuVuc;
}
