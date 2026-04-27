package infrastructure.persistence.impl;

import core.entity.ChiTietHoaDon;
import core.entity.ChiTietHoaDonId;
import core.repository.ChiTietHoaDonRepository;
import infrastructure.persistence.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ChiTietHoaDonRepositoryImpl extends AbstractRepository<ChiTietHoaDon, ChiTietHoaDonId>
        implements ChiTietHoaDonRepository {

    public ChiTietHoaDonRepositoryImpl() {
        super(ChiTietHoaDon.class);
    }

    @Override
    public List<ChiTietHoaDon> findByMaHD(String maHD) {
        return findByHql("FROM ChiTietHoaDon WHERE id.maHD = :maHD", "maHD", maHD);
    }

    @Override
    public void deleteByMaHD(String maHD) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.createQuery("DELETE FROM ChiTietHoaDon WHERE id.maHD = :maHD")
                   .setParameter("maHD", maHD)
                   .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("deleteByMaHD failed", e);
        }
    }
}
