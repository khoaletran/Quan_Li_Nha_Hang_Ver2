package core.repository;

import core.entity.HoaDon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Specialized queries for HoaDon beyond generic CRUD. */
public interface HoaDonRepository extends GenericRepository<HoaDon, String> {
    List<HoaDon> findAllNgayHomNay();
    List<HoaDon> findByMaNV(String maNV);
    List<HoaDon> findByTrangThai(int trangThai);
    List<HoaDon> findByMaBan(String maBan);
    List<HoaDon> findWaitlist();
    Optional<String> findLastMaHDByPrefix(String prefix);
    List<HoaDon> findByNgay(LocalDate ngay);

    /**
     * Cập nhật trạng thái thanh toán của hóa đơn bằng native SQL.
     * Dùng thay cho update(entity) để tránh lỗi detached/transient entity
     * khi merge entity đã bị tách khỏi EntityManager.
     */
    void updateForCheckout(String maHD, String maKM, boolean kieuThanhToan, LocalDateTime tgCheckout);
}
