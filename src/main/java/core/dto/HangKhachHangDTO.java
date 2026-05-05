package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for HangKhachHang. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HangKhachHangDTO  implements Serializable {
    private String maHang;
    private int diemHang;
    private int giamGia;
    private String moTa;
}
