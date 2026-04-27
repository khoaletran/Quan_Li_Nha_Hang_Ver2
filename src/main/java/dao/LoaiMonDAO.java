package dao;

import entity.LoaiMon;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class LoaiMonDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static LoaiMon mapRow(Tuple t) {
        return new LoaiMon(t.get("maLoaiMon", String.class), t.get("tenLoaiMon", String.class), t.get("moTa", String.class));
    }

    public static List<LoaiMon> getAll() {
        List<LoaiMon> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery("SELECT maLoaiMon, tenLoaiMon, moTa FROM LoaiMon ORDER BY maLoaiMon", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("LoaiMonDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static LoaiMon getByID(String maLoaiMon) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maLoaiMon, tenLoaiMon, moTa FROM LoaiMon WHERE maLoaiMon=:ma", Tuple.class)
                .setParameter("ma", maLoaiMon).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("LoaiMonDAO.getByID(): " + e.getMessage()); }
        return null;
    }

    public static String getMaLoaiMonByTen(String tenLoaiMon) {
        try (EntityManager em = em()) {
            List<?> rows = em.createNativeQuery("SELECT maLoaiMon FROM LoaiMon WHERE tenLoaiMon=:ten").setParameter("ten", tenLoaiMon).getResultList();
            if (!rows.isEmpty()) return (String) rows.get(0);
        } catch (Exception e) { System.err.println("LoaiMonDAO.getMaLoaiMonByTen(): " + e.getMessage()); }
        return null;
    }

    public boolean insert(LoaiMon loaiMon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO LoaiMon(maLoaiMon, tenLoaiMon, moTa) VALUES (:ma, :ten, :mo)")
                .setParameter("ma", loaiMon.getMaLoaiMon()).setParameter("ten", loaiMon.getTenLoaiMon()).setParameter("mo", loaiMon.getMoTa()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("LoaiMonDAO.insert(): " + e.getMessage()); return false; }
    }

    public boolean update(LoaiMon loaiMon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE LoaiMon SET tenLoaiMon=:ten, moTa=:mo WHERE maLoaiMon=:ma")
                .setParameter("ten", loaiMon.getTenLoaiMon()).setParameter("mo", loaiMon.getMoTa()).setParameter("ma", loaiMon.getMaLoaiMon()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("LoaiMonDAO.update(): " + e.getMessage()); return false; }
    }

    public boolean delete(String maLoaiMon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM LoaiMon WHERE maLoaiMon=:ma").setParameter("ma", maLoaiMon).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("LoaiMonDAO.delete(): " + e.getMessage()); return false; }
    }
}
