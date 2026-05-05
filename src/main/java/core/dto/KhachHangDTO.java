package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for KhachHang — flattens HangKhachHang relationship. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KhachHangDTO  implements Serializable {
    private String maKH;
    private String tenKH;
    private String sdt;
    private boolean gioiTinh;
    private int diemTichLuy;

    // Flattened from HangKhachHang
    private String maHang;
    private String tenHang;
    private int giamGiaHang;
    private int diemHang;
}
