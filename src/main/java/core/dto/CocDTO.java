package core.dto;

import lombok.*;

/** DTO for Coc. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CocDTO {
    private String maCoc;
    private boolean loaiCoc;
    private int phanTramCoc;
    private double soTienCoc;
    // Flattened FKs
    private String maKhuVuc;
    private String tenKhuVuc;
    private String maLoaiBan;
    private String tenLoaiBan;
}
