package core.repository;
import core.entity.PhieuKetCa;
import java.util.List;
public interface PhieuKetCaRepository extends GenericRepository<PhieuKetCa, String> {
    List<PhieuKetCa> findByMaNV(String maNV);
}
