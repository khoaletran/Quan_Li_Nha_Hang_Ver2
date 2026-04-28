package infrastructure.persistence.impl;

import core.entity.PhieuKetCa;
import core.repository.PhieuKetCaRepository;
import infrastructure.persistence.AbstractRepository;

import java.util.List;

public class PhieuKetCaRepositoryImpl extends AbstractRepository<PhieuKetCa, String>
        implements PhieuKetCaRepository {

    public PhieuKetCaRepositoryImpl() { super(PhieuKetCa.class); }

    @Override
    public List<PhieuKetCa> findByMaNV(String maNV) {
        return findByHql("FROM PhieuKetCa WHERE nhanVien.maNV = :maNV", "maNV", maNV);
    }

    @Override
    public List<PhieuKetCa> findAllWithNhanVien() {
        return findByHql("SELECT p FROM PhieuKetCa p JOIN FETCH p.nhanVien ORDER BY p.maPhieu DESC");
    }

    @Override
    public java.util.Optional<String> findLastMaPhieuByPrefix(String prefix) {
        String sql = "SELECT maPhieu FROM PhieuKetCa WHERE maPhieu LIKE :p ORDER BY maPhieu DESC LIMIT 1";
        try (jakarta.persistence.EntityManager em = infrastructure.db.JpaConfig.getEntityManagerFactory().createEntityManager()) {
            List<?> r = em.createNativeQuery(sql)
                    .setParameter("p", prefix + "%")
                    .getResultList();
            return r.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of((String) r.get(0));
        }
    }
}
