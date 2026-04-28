package infrastructure.persistence.impl;

import core.entity.HoaDon;
import core.repository.HoaDonRepository;
import infrastructure.persistence.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HoaDonRepositoryImpl extends AbstractRepository<HoaDon, String>
        implements HoaDonRepository {

    public HoaDonRepositoryImpl() {
        super(HoaDon.class);
    }

    /**
     * JOIN FETCH tất cả association LAZY cần thiết cho toDto() và billing service.
     * Override AbstractRepository.findById() để tránh LazyInitializationException.
     */
    @Override
    public Optional<HoaDon> findById(String id) {
        try (EntityManager em = openEntityManager()) {
            List<HoaDon> rs = em.createQuery("""
                FROM HoaDon hd
                LEFT JOIN FETCH hd.khachHang kh
                LEFT JOIN FETCH kh.hangKhachHang
                LEFT JOIN FETCH hd.nhanVien
                LEFT JOIN FETCH hd.ban b
                LEFT JOIN FETCH b.khuVuc
                LEFT JOIN FETCH b.loaiBan
                LEFT JOIN FETCH hd.khuyenMai
                LEFT JOIN FETCH hd.suKien
                WHERE hd.maHD = :id
                """, HoaDon.class)
                .setParameter("id", id)
                .getResultList();
            return rs.isEmpty() ? Optional.empty() : Optional.of(rs.get(0));
        }
    }

    @Override
    public List<HoaDon> findAllNgayHomNay() {
        try (EntityManager em = openEntityManager()) {
            return em.createQuery("""
                FROM HoaDon hd
                LEFT JOIN FETCH hd.khachHang kh
                LEFT JOIN FETCH kh.hangKhachHang
                LEFT JOIN FETCH hd.nhanVien
                LEFT JOIN FETCH hd.ban b
                LEFT JOIN FETCH b.khuVuc
                LEFT JOIN FETCH b.loaiBan
                LEFT JOIN FETCH hd.khuyenMai
                LEFT JOIN FETCH hd.suKien
                WHERE (
                    (hd.kieuDatBan = true  AND CAST(hd.tgCheckIn AS date) = CURRENT_DATE)
                    OR
                    (hd.kieuDatBan = false AND CAST(hd.tgLapHD  AS date) = CURRENT_DATE)
                )
                """, HoaDon.class)
                .getResultList();
        }
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
                "FROM HoaDon WHERE kieuDatBan = false AND trangThai = 0 AND ban.maBan LIKE 'W%' ORDER BY maHD DESC");
    }

    @Override
    public Optional<String> findLastMaHDByPrefix(String prefix) {
        try (EntityManager em = openEntityManager()) {
            String result = em
                    .createQuery("SELECT maHD FROM HoaDon WHERE maHD LIKE :prefix ORDER BY maHD DESC", String.class)
                    .setParameter("prefix", prefix + "%")
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            return Optional.ofNullable(result);
        }
    }

    @Override
    public List<HoaDon> findByNgay(LocalDate ngay) {
        return findByHql(
                "FROM HoaDon WHERE CAST(tgLapHD AS date) = :ngay OR CAST(tgCheckIn AS date) = :ngay",
                "ngay", ngay);
    }

    /**
     * Cập nhật 4 cột thanh toán bằng native SQL trong một transaction độc lập.
     * Tránh toàn bộ vấn đề detached/transient entity mà em.merge() gây ra
     * khi entity được load từ một EntityManager đã đóng.
     */
    @Override
    public void updateForCheckout(String maHD, String maKM,
                                  boolean kieuThanhToan, LocalDateTime tgCheckout) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.createNativeQuery(
                    "UPDATE HoaDon SET trangThai = 2, tgCheckout = :tgCheckout, " +
                    "kieuThanhToan = :kieuThanhToan, maKM = :maKM WHERE maHD = :maHD")
                .setParameter("tgCheckout",     tgCheckout)
                .setParameter("kieuThanhToan",  kieuThanhToan ? 1 : 0)
                .setParameter("maKM",           maKM)          // null được chấp nhận
                .setParameter("maHD",           maHD)
                .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("updateForCheckout() thất bại cho maHD=" + maHD, e);
        }
    }
}
