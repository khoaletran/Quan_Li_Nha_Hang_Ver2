package infrastructure.persistence.impl;

import core.entity.Ban;
import core.repository.BanRepository;
import infrastructure.persistence.AbstractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class BanRepositoryImpl extends AbstractRepository<Ban, String>
        implements BanRepository {

    public BanRepositoryImpl() { super(Ban.class); }

    @Override
    public List<Ban> findByKhuVuc(String maKhuVuc) {
        return findByHql("FROM Ban WHERE khuVuc.maKhuVuc = :kv", "kv", maKhuVuc);
    }

    @Override
    public List<Ban> findByTrangThai(boolean trangThai) {
        return findByHql("FROM Ban WHERE trangThai = :tt", "tt", trangThai);
    }

    @Override
    public void updateTrangThai(String maBan, boolean trangThai) {
        EntityTransaction tx = null;
        try (EntityManager em = openEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.createQuery("UPDATE Ban SET trangThai = :tt WHERE maBan = :mb")
                   .setParameter("tt", trangThai)
                   .setParameter("mb", maBan)
                   .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("updateTrangThai failed", e);
        }
    }
}
