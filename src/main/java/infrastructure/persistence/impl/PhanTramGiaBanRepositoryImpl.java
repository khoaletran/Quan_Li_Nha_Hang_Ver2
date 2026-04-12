package infrastructure.persistence.impl;

import core.entity.PhanTramGiaBan;
import core.repository.PhanTramGiaBanRepository;
import infrastructure.persistence.AbstractRepository;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public class PhanTramGiaBanRepositoryImpl extends AbstractRepository<PhanTramGiaBan, String>
        implements PhanTramGiaBanRepository {

    public PhanTramGiaBanRepositoryImpl() {
        super(PhanTramGiaBan.class);
    }

    @Override
    public Optional<PhanTramGiaBan> findLatestForMon(String maMon) {
        return findFirstByHql(
            "FROM PhanTramGiaBan WHERE mon.maMon = :maMon ORDER BY ngayApDung DESC",
            "maMon", maMon
        );
    }

    @Override
    public Optional<PhanTramGiaBan> findLatestForLoaiMon(String maLoaiMon) {
        return findFirstByHql(
            "FROM PhanTramGiaBan WHERE loaiMon.maLoaiMon = :maLM AND mon IS NULL ORDER BY ngayApDung DESC",
            "maLM", maLoaiMon
        );
    }

    @Override
    public Optional<PhanTramGiaBan> findEffectiveForMonAtDate(String maMon, LocalDateTime date) {
        return findFirstByHql(
            "FROM PhanTramGiaBan WHERE mon.maMon = :maMon AND ngayApDung <= :date ORDER BY ngayApDung DESC",
            "maMon", maMon, "date", date
        );
    }

    @Override
    public Optional<PhanTramGiaBan> findEffectiveForLoaiMonAtDate(String maLoaiMon, LocalDateTime date) {
        return findFirstByHql(
            "FROM PhanTramGiaBan WHERE loaiMon.maLoaiMon = :maLM AND mon IS NULL AND ngayApDung <= :date ORDER BY ngayApDung DESC",
            "maLM", maLoaiMon, "date", date
        );
    }
}
