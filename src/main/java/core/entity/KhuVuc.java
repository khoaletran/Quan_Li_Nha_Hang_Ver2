package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Khu vực (restaurant zone/area).
 */
@Entity
@Table(name = "KhuVuc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maKhuVuc")
@ToString(exclude = "bans")
public class KhuVuc {

    @Id
    @Column(name = "maKhuVuc", length = 6)
    private String maKhuVuc;

    @Column(name = "tenKhuVuc", columnDefinition = "NVARCHAR(200)")
    private String tenKhuVuc;

    // ─── Relationships ────────────────────────────────────────────────────

    @JsonIgnore
    @OneToMany(mappedBy = "khuVuc", fetch = FetchType.LAZY)
    private List<Ban> bans;
}
