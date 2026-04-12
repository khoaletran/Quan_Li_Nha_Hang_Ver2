package core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Phần trăm giá bán (profit margin percentage for pricing).
 * Applied either to a specific Mon or to an entire LoaiMon.
 */
@Entity
@Table(name = "PhanTramGiaBan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maPTGB")
public class PhanTramGiaBan {

    @Id
    @Column(name = "maPTGB", length = 6)
    private String maPTGB;

    /** Profit percentage to add on top of giaGoc. */
    @Column(name = "phanTramLoi")
    private int phanTramLoi;

    @Column(name = "ngayApDung")
    private LocalDateTime ngayApDung;

    // ─── Relationships ────────────────────────────────────────────────────

    /** Nullable: if set, this rule applies only to this specific Mon. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maMon", nullable = true)
    private Mon mon;

    /** Nullable: if set, this rule applies to the entire LoaiMon. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLoaiMon", nullable = true)
    private LoaiMon loaiMon;
}
