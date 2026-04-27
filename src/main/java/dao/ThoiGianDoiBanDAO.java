package dao;

import entity.ThoiGianDoiBan;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class ThoiGianDoiBanDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private static ThoiGianDoiBan mapRow(Tuple t) {
        return new ThoiGianDoiBan(t.get("maTGDB", String.class), toBool(t.get("loaiDatBan")), ((Number) t.get("thoiGian")).intValue());
    }

    public static List<ThoiGianDoiBan> getAll() {
        List<ThoiGianDoiBan> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery("SELECT maTGDB, loaiDatBan, thoiGian FROM ThoiGianDoiBan ORDER BY maTGDB", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("ThoiGianDoiBanDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public ThoiGianDoiBan getLatest() {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maTGDB, loaiDatBan, thoiGian FROM ThoiGianDoiBan ORDER BY maTGDB DESC LIMIT 1", Tuple.class).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("ThoiGianDoiBanDAO.getLatest(): " + e.getMessage()); }
        return null;
    }

    public static ThoiGianDoiBan getLatestByLoai(boolean loaiDatBan) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maTGDB, loaiDatBan, thoiGian FROM ThoiGianDoiBan WHERE loaiDatBan=:loai ORDER BY maTGDB DESC LIMIT 1", Tuple.class)
                .setParameter("loai", loaiDatBan).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("ThoiGianDoiBanDAO.getLatestByLoai(): " + e.getMessage()); }
        return null;
    }

    public boolean insert(ThoiGianDoiBan tgdb) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO ThoiGianDoiBan(maTGDB, loaiDatBan, thoiGian) VALUES (:ma, :loai, :tg)")
                .setParameter("ma", tgdb.getMaTGDB()).setParameter("loai", tgdb.isLoaiDatBan()).setParameter("tg", tgdb.getThoiGian()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("ThoiGianDoiBanDAO.insert(): " + e.getMessage()); return false; }
    }

    public boolean update(ThoiGianDoiBan tgdb) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE ThoiGianDoiBan SET loaiDatBan=:loai, thoiGian=:tg WHERE maTGDB=:ma")
                .setParameter("loai", tgdb.isLoaiDatBan()).setParameter("tg", tgdb.getThoiGian()).setParameter("ma", tgdb.getMaTGDB()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("ThoiGianDoiBanDAO.update(): " + e.getMessage()); return false; }
    }

    public boolean delete(String maTGDB) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM ThoiGianDoiBan WHERE maTGDB=:ma").setParameter("ma", maTGDB).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("ThoiGianDoiBanDAO.delete(): " + e.getMessage()); return false; }
    }
}
