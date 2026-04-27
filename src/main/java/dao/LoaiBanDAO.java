package dao;

import entity.LoaiBan;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class LoaiBanDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static LoaiBan mapRow(Tuple t) {
        return new LoaiBan(t.get("maLoaiBan", String.class), ((Number) t.get("soLuong")).intValue(), t.get("tenLoaiBan", String.class));
    }

    public static List<LoaiBan> getAll() {
        List<LoaiBan> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery("SELECT maLoaiBan, soLuong, tenLoaiBan FROM LoaiBan ORDER BY maLoaiBan", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("LoaiBanDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static LoaiBan getById(String maLoaiBan) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maLoaiBan, soLuong, tenLoaiBan FROM LoaiBan WHERE maLoaiBan=:ma", Tuple.class)
                .setParameter("ma", maLoaiBan).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("LoaiBanDAO.getById(): " + e.getMessage()); }
        return null;
    }

    public static LoaiBan getByName(String tenLoaiBan) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maLoaiBan, soLuong, tenLoaiBan FROM LoaiBan WHERE tenLoaiBan=:ten", Tuple.class)
                .setParameter("ten", tenLoaiBan).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("LoaiBanDAO.getByName(): " + e.getMessage()); }
        return null;
    }

    public boolean insert(LoaiBan lb) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO LoaiBan(maLoaiBan, tenLoaiBan, soLuong) VALUES (:ma, :ten, :sl)")
                .setParameter("ma", lb.getMaLoaiBan()).setParameter("ten", lb.getTenLoaiBan()).setParameter("sl", lb.getSoLuong()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("LoaiBanDAO.insert(): " + e.getMessage()); return false; }
    }

    public boolean update(LoaiBan lb) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE LoaiBan SET tenLoaiBan=:ten, soLuong=:sl WHERE maLoaiBan=:ma")
                .setParameter("ten", lb.getTenLoaiBan()).setParameter("sl", lb.getSoLuong()).setParameter("ma", lb.getMaLoaiBan()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("LoaiBanDAO.update(): " + e.getMessage()); return false; }
    }

    public boolean delete(String maLoaiBan) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM LoaiBan WHERE maLoaiBan=:ma").setParameter("ma", maLoaiBan).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("LoaiBanDAO.delete(): " + e.getMessage()); return false; }
    }
}
