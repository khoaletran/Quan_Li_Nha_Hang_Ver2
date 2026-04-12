package core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite primary key for ChiTietHoaDon (maHD + maMon).
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietHoaDonId implements Serializable {

    @Column(name = "maHD", length = 14)
    private String maHD;

    @Column(name = "maMon", length = 6)
    private String maMon;
}
