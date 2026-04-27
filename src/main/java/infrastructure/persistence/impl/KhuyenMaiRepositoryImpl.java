package infrastructure.persistence.impl;

import core.entity.KhuyenMai;
import core.repository.KhuyenMaiRepository;
import infrastructure.persistence.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Optional;

public class KhuyenMaiRepositoryImpl extends AbstractRepository<KhuyenMai, String>
        implements KhuyenMaiRepository {

    public KhuyenMaiRepositoryImpl() {
        super(KhuyenMai.class);
    }

    @Override
    public Optional<KhuyenMai> findByMaKM(String maKM) {
        return findById(maKM);
    }

    @Override
    public boolean decrementSoLuong(String maKM) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em
                .createQuery(
                    "UPDATE KhuyenMai SET soLuong = soLuong - 1 WHERE maKM = :maKM AND soLuong > 0"
                )
                .setParameter("maKM", maKM)
                .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean incrementSoLuong(String maKM) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em
                .createQuery(
                    "UPDATE KhuyenMai SET soLuong = soLuong + 1 WHERE maKM = :maKM"
                )
                .setParameter("maKM", maKM)
                .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            return false;
        }
    }
}
