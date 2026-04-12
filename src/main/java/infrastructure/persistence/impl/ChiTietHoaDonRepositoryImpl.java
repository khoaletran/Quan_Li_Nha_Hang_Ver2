package infrastructure.persistence.impl;

import core.entity.ChiTietHoaDon;
import core.entity.ChiTietHoaDonId;
import core.repository.ChiTietHoaDonRepository;
import infrastructure.persistence.AbstractRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        Transaction tx = null;
        try (Session session = openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM ChiTietHoaDon WHERE id.maHD = :maHD")
                   .setParameter("maHD", maHD)
                   .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("deleteByMaHD failed", e);
        }
    }
}
