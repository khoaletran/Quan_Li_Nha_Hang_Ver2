package core.repository;

import core.entity.PhanTramGiaBan;
import java.time.LocalDateTime;
import java.util.Optional;

/** Repository for PhanTramGiaBan. */
public interface PhanTramGiaBanRepository extends GenericRepository<PhanTramGiaBan, String> {
    Optional<PhanTramGiaBan> findLatestForMon(String maMon);
    Optional<PhanTramGiaBan> findLatestForLoaiMon(String maLoaiMon);
    Optional<PhanTramGiaBan> findEffectiveForMonAtDate(String maMon, LocalDateTime date);
    Optional<PhanTramGiaBan> findEffectiveForLoaiMonAtDate(String maLoaiMon, LocalDateTime date);
}
