package core.dto;

import lombok.*;

/** DTO for HangKhachHang. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HangKhachHangDTO {
    private String maHang;
    private int diemHang;
    private int giamGia;
    private String moTa;
}
