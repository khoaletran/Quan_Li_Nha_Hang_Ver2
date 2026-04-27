package dao;

import entity.Mon;
import entity.LoaiMon;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class MonDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static Mon mapRow(Tuple t, List<LoaiMon> cache) {
        Mon m = new Mon();
        m.setMaMon(t.get("maMon", String.class));
        m.setTenMon(t.get("tenMon", String.class));
        m.setMoTa(t.get("moTa", String.class));
        m.setHinhAnh(t.get("hinhAnh", String.class));
        m.setGiaGoc(t.get("giaGoc") != null ? ((Number) t.get("giaGoc")).doubleValue() : 0);
        m.setSoLuong(t.get("soLuong") != null ? ((Number) t.get("soLuong")).intValue() : 0);
        String maLoai = t.get("loaiMon", String.class);
        if (maLoai != null && cache != null) {
            m.setLoaiMon(cache.stream().filter(x -> x.getMaLoaiMon().equals(maLoai)).findFirst().orElse(null));
        } else if (maLoai != null) {
            m.setLoaiMon(LoaiMonDAO.getByID(maLoai));
        }
        return m;
    }

    private static final String SELECT = "SELECT maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon FROM Mon";

    public static List<Mon> getAll() {
        List<Mon> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " ORDER BY maMon", Tuple.class).getResultList();
            List<LoaiMon> cache = LoaiMonDAO.getAll();
            for (Tuple t : rows) ds.add(mapRow(t, cache));
        } catch (Exception e) { System.err.println("MonDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static Mon findByID(String maMon) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maMon=:ma", Tuple.class).setParameter("ma", maMon).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0), null);
        } catch (Exception e) { System.err.println("MonDAO.findByID(): " + e.getMessage()); }
        return null;
    }

    public static boolean insert(Mon mon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO Mon(maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) VALUES (:ma, :ten, :mo, :ha, :gg, :sl, :lm)")
                .setParameter("ma", mon.getMaMon()).setParameter("ten", mon.getTenMon()).setParameter("mo", mon.getMoTa())
                .setParameter("ha", mon.getHinhAnh()).setParameter("gg", mon.getGiaGoc()).setParameter("sl", mon.getSoLuong())
                .setParameter("lm", mon.getLoaiMon() != null ? mon.getLoaiMon().getMaLoaiMon() : null).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("MonDAO.insert(): " + e.getMessage()); return false; }
    }

    public static boolean update(Mon mon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE Mon SET tenMon=:ten, moTa=:mo, hinhAnh=:ha, giaGoc=:gg, soLuong=:sl, loaiMon=:lm WHERE maMon=:ma")
                .setParameter("ten", mon.getTenMon()).setParameter("mo", mon.getMoTa()).setParameter("ha", mon.getHinhAnh())
                .setParameter("gg", mon.getGiaGoc()).setParameter("sl", mon.getSoLuong())
                .setParameter("lm", mon.getLoaiMon() != null ? mon.getLoaiMon().getMaLoaiMon() : null)
                .setParameter("ma", mon.getMaMon()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("MonDAO.update(): " + e.getMessage()); return false; }
    }

    public static boolean delete(String maMon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM Mon WHERE maMon=:ma").setParameter("ma", maMon).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("MonDAO.delete(): " + e.getMessage()); return false; }
    }

    public static String getLatestMaMon() {
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maMon FROM Mon ORDER BY maMon DESC LIMIT 1").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) { System.err.println("MonDAO.getLatestMaMon(): " + e.getMessage()); }
        return null;
    }
}
