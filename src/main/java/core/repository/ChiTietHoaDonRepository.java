package core.repository;

import core.entity.ChiTietHoaDon;
import core.entity.ChiTietHoaDonId;
import java.util.List;

/** Repository for ChiTietHoaDon. */
public interface ChiTietHoaDonRepository extends GenericRepository<ChiTietHoaDon, ChiTietHoaDonId> {
    List<ChiTietHoaDon> findByMaHD(String maHD);
    void deleteByMaHD(String maHD);
}
