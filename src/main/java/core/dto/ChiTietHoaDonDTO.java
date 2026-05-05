package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for ChiTietHoaDon — flattens Mon info. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChiTietHoaDonDTO  implements Serializable {
    private String maHD;
    private String maMon;
    private String tenMon;
    private String hinhAnh;
    private int soLuong;
    private double thanhTien;

    // Computed by service at the time of the invoice
    private double giaBanTaiLucLapHD;
    private int phanTramLoiTaiLucLapHD;
}
