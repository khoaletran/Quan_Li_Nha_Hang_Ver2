package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bàn (restaurant table).
 */
@Entity
@Table(name = "Ban")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maBan")
@ToString(exclude = "hoaDons")
public class Ban {

    @Id
    @Column(name = "maBan", length = 6)
    private String maBan;

    @Column(name = "trangThai")
    private boolean trangThai;

    // ─── Relationships ────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLoaiBan")
    private LoaiBan loaiBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhuVuc")
    private KhuVuc khuVuc;

    @JsonIgnore
    @OneToMany(mappedBy = "ban", fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;
}
