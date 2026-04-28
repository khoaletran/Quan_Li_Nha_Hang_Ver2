package core.repository;
import core.entity.PhieuKetCa;
import java.util.List;
public interface PhieuKetCaRepository extends GenericRepository<PhieuKetCa, String> {
    List<PhieuKetCa> findByMaNV(String maNV);
    List<PhieuKetCa> findAllWithNhanVien();
    java.util.Optional<String> findLastMaPhieuByPrefix(String prefix);
}
