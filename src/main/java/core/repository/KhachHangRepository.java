package core.repository;

import core.entity.KhachHang;
import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends GenericRepository<KhachHang, String> {
    Optional<KhachHang> findBySdt(String sdt);
    List<KhachHang> findByHangKhachHang(String maHang);
}
