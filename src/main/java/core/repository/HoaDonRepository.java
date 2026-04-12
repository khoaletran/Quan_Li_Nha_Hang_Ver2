package core.repository;

import core.entity.HoaDon;
import java.time.LocalDate;
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
}
