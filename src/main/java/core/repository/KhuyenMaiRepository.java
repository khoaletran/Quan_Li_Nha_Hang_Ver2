package core.repository;

import core.entity.KhuyenMai;
import java.util.Optional;

public interface KhuyenMaiRepository extends GenericRepository<KhuyenMai, String> {
    Optional<KhuyenMai> findByMaKM(String maKM);
    boolean decrementSoLuong(String maKM);
    boolean incrementSoLuong(String maKM);
}
