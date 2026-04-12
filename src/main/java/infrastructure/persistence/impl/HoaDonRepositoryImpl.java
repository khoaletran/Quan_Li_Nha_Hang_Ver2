package infrastructure.persistence.impl;

import core.entity.HoaDon;
import core.repository.HoaDonRepository;
import infrastructure.persistence.AbstractRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HoaDonRepositoryImpl extends AbstractRepository<HoaDon, String>
        implements HoaDonRepository {

    public HoaDonRepositoryImpl() {
        super(HoaDon.class);
    }

    @Override
    public List<HoaDon> findAllNgayHomNay() {
        String hql = """
            FROM HoaDon hd
            WHERE (
                (hd.kieuDatBan = true
                 AND CAST(hd.tgCheckIn AS date) = CURRENT_DATE)
                OR
                (hd.kieuDatBan = false
                 AND CAST(hd.tgLapHD AS date) = CURRENT_DATE)
            )
            """;
        return findByHql(hql);
    }

    @Override
    public List<HoaDon> findByMaNV(String maNV) {
        return findByHql("FROM HoaDon WHERE nhanVien.maNV = :maNV", "maNV", maNV);
    }

    @Override
    public List<HoaDon> findByTrangThai(int trangThai) {
        return findByHql("FROM HoaDon WHERE trangThai = :tt", "tt", trangThai);
    }

    @Override
    public List<HoaDon> findByMaBan(String maBan) {
        return findByHql("FROM HoaDon WHERE ban.maBan = :maBan", "maBan", maBan);
    }

    @Override
    public List<HoaDon> findWaitlist() {
        return findByHql(
            "FROM HoaDon WHERE kieuDatBan = false AND trangThai = 0 AND ban.maBan LIKE 'W%' ORDER BY maHD DESC"
        );
    }

    @Override
    public Optional<String> findLastMaHDByPrefix(String prefix) {
        try (Session session = openSession()) {
            String result = (String) session
                .createQuery("SELECT maHD FROM HoaDon WHERE maHD LIKE :prefix ORDER BY maHD DESC")
                .setParameter("prefix", prefix + "%")
                .setMaxResults(1)
                .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    @Override
    public List<HoaDon> findByNgay(LocalDate ngay) {
        return findByHql(
            "FROM HoaDon WHERE CAST(tgLapHD AS date) = :ngay OR CAST(tgCheckIn AS date) = :ngay",
            "ngay", ngay
        );
    }
}
