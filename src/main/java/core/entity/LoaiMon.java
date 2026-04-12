package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Loại món (food category).
 */
@Entity
@Table(name = "LoaiMon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "maLoaiMon")
@ToString(exclude = {"mons", "phanTramGiaBans"})
public class LoaiMon {

    @Id
    @Column(name = "maLoaiMon", length = 6)
    private String maLoaiMon;

    @Column(name = "tenLoaiMon", columnDefinition = "NVARCHAR(200)")
    private String tenLoaiMon;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(500)")
    private String moTa;

    // ─── Relationships ────────────────────────────────────────────────────

    @JsonIgnore
    @OneToMany(mappedBy = "loaiMon", fetch = FetchType.LAZY)
    private List<Mon> mons;

    @JsonIgnore
    @OneToMany(mappedBy = "loaiMon", fetch = FetchType.LAZY)
    private List<PhanTramGiaBan> phanTramGiaBans;
}
