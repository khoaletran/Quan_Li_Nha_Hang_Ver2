package core.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Thời gian đổi bàn (table swap time policy).
 * Independent entity — no FK relationships.
 */
@Entity
@Table(name = "ThoiGianDoiBan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maTGDB")
public class ThoiGianDoiBan {

    @Id
    @Column(name = "maTGDB", length = 6)
    private String maTGDB;

    /**
     * true  = Online (đặt bàn trước)<br>
     * false = Offline (walk-in, waitlist khi hết bàn)
     */
    @Column(name = "loaiDatBan")
    private boolean loaiDatBan;

    /** Time in minutes before a table is released. */
    @Column(name = "thoiGian")
    private int thoiGian;
}
