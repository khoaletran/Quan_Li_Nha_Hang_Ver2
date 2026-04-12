package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Loại bàn (table type, e.g. VIP, thường).
 */
@Entity
@Table(name = "LoaiBan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maLoaiBan")
@ToString(exclude = "bans")
public class LoaiBan {

    @Id
    @Column(name = "maLoaiBan", length = 6)
    private String maLoaiBan;

    @Column(name = "tenLoaiBan", columnDefinition = "NVARCHAR(200)")
    private String tenLoaiBan;

    /** Number of seats this table type supports. */
    @Column(name = "soLuong")
    private int soLuong;

    // ─── Relationships ────────────────────────────────────────────────────

    @JsonIgnore
    @OneToMany(mappedBy = "loaiBan", fetch = FetchType.LAZY)
    private List<Ban> bans;
}
