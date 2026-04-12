package core.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Cọc / đặt cọc (deposit policy per zone+table type).
 */
@Entity
@Table(name = "Coc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maCoc")
public class Coc {

    @Id
    @Column(name = "maCoc", length = 6)
    private String maCoc;

    /**
     * true  = deposit is a percentage of total bill.<br>
     * false = deposit is a fixed amount (soTienCoc).
     */
    @Column(name = "loaiCoc")
    private boolean loaiCoc;

    /** Percentage value (0–100) when loaiCoc = true. */
    @Column(name = "phanTramCoc")
    private int phanTramCoc;

    /** Fixed deposit amount (VND) when loaiCoc = false. */
    @Column(name = "soTienCoc")
    private double soTienCoc;

    // ─── Relationships ────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhuVuc")
    private KhuVuc khuVuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLoaiBan")
    private LoaiBan loaiBan;
}
