package core.repository;

import core.entity.NhanVien;
import java.util.Optional;

public interface NhanVienRepository extends GenericRepository<NhanVien, String> {
    Optional<NhanVien> findByMatKhau(String maNV, String matKhau);
    Optional<NhanVien> findBySdt(String sdt);
}
