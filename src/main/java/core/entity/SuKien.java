package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Sự kiện (special events, e.g. anniversary, birthday).
 */
@Entity
@Table(name = "SuKien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maSK")
@ToString(exclude = "hoaDons")
public class SuKien {

    @Id
    @Column(name = "maSK", length = 6)
    private String maSK;

    @Column(name = "tenSK", columnDefinition = "NVARCHAR(200)")
    private String tenSK;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(500)")
    private String moTa;

    @Column(name = "gia")
    private double gia;

    // ─── Relationships ────────────────────────────────────────────────────

    @JsonIgnore
    @OneToMany(mappedBy = "suKien", fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;
}
