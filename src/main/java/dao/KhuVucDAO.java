package dao;

import entity.KhuVuc;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class KhuVucDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static KhuVuc mapRow(Tuple t) {
        return new KhuVuc(t.get("maKhuVuc", String.class), t.get("tenKhuVuc", String.class));
    }

    public static List<KhuVuc> getAll() {
        List<KhuVuc> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery("SELECT maKhuVuc, tenKhuVuc FROM KhuVuc ORDER BY maKhuVuc", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("Lỗi KhuVucDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static KhuVuc getById(String maKhuVuc) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maKhuVuc, tenKhuVuc FROM KhuVuc WHERE maKhuVuc=:id", Tuple.class)
                .setParameter("id", maKhuVuc).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("Lỗi KhuVucDAO.getById(): " + e.getMessage()); }
        return null;
    }

    public static KhuVuc getByName(String tenKhuVuc) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maKhuVuc, tenKhuVuc FROM KhuVuc WHERE tenKhuVuc=:ten", Tuple.class)
                .setParameter("ten", tenKhuVuc).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("Lỗi KhuVucDAO.getByName(): " + e.getMessage()); }
        return null;
    }

    public boolean insert(KhuVuc kv) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO KhuVuc(maKhuVuc, tenKhuVuc) VALUES (:ma, :ten)")
                .setParameter("ma", kv.getMaKhuVuc()).setParameter("ten", kv.getTenKhuVuc()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi KhuVucDAO.insert(): " + e.getMessage()); return false; }
    }

    public boolean update(KhuVuc kv) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE KhuVuc SET tenKhuVuc=:ten WHERE maKhuVuc=:ma")
                .setParameter("ten", kv.getTenKhuVuc()).setParameter("ma", kv.getMaKhuVuc()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi KhuVucDAO.update(): " + e.getMessage()); return false; }
    }

    public boolean delete(String maKhuVuc) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM KhuVuc WHERE maKhuVuc=:ma").setParameter("ma", maKhuVuc).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi KhuVucDAO.delete(): " + e.getMessage()); return false; }
    }
}
