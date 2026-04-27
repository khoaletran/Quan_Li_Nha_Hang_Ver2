package dao;

import entity.SuKien;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SuKienDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static SuKien mapRow(Tuple t) {
        return new SuKien(t.get("maSK", String.class), t.get("tenSK", String.class), t.get("moTa", String.class), ((Number) t.get("gia")).doubleValue());
    }

    public static List<SuKien> getAll() {
        List<SuKien> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery("SELECT maSK, tenSK, moTa, gia FROM SuKien ORDER BY maSK", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("SuKienDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public boolean insert(SuKien sk) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO SuKien(maSK, tenSK, moTa, gia) VALUES (:ma, :ten, :mo, :gia)")
                .setParameter("ma", sk.getMaSK()).setParameter("ten", sk.getTenSK()).setParameter("mo", sk.getMota()).setParameter("gia", sk.getGia()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("SuKienDAO.insert(): " + e.getMessage()); return false; }
    }

    public boolean update(SuKien sk) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE SuKien SET tenSK=:ten, moTa=:mo, gia=:gia WHERE maSK=:ma")
                .setParameter("ten", sk.getTenSK()).setParameter("mo", sk.getMota()).setParameter("gia", sk.getGia()).setParameter("ma", sk.getMaSK()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("SuKienDAO.update(): " + e.getMessage()); return false; }
    }

    public boolean delete(String maSK) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM SuKien WHERE maSK=:ma").setParameter("ma", maSK).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("SuKienDAO.delete(): " + e.getMessage()); return false; }
    }

    public static SuKien getByID(String maSK) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maSK, tenSK, moTa, gia FROM SuKien WHERE maSK=:ma", Tuple.class)
                .setParameter("ma", maSK).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("SuKienDAO.getByID(): " + e.getMessage()); }
        return null;
    }
}
