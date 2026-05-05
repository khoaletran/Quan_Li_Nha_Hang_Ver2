package core.dto;

import lombok.*;

import java.io.Serializable;

/** DTO for Mon — includes computed giaBan. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MonDTO implements Serializable {
    private String maMon;
    private String tenMon;
    private String moTa;
    private String hinhAnh;
    private double giaGoc;
    private int soLuong;

    // Flattened from LoaiMon
    private String maLoaiMon;
    private String tenLoaiMon;

    // Computed by MonService
    private double giaBan;
    private int phanTramLoi;
}
