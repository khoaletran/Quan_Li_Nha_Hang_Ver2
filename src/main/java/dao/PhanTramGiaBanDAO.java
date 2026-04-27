package dao;

import entity.Mon;
import entity.LoaiMon;
import entity.PhanTramGiaBan;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhanTramGiaBanDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static PhanTramGiaBan mapRow(Tuple t) {
        LoaiMon loaiMon = t.get("maLoaiMon") != null ? LoaiMonDAO.getByID(t.get("maLoaiMon", String.class)) : null;
        Mon mon = t.get("maMon") != null ? MonDAO.findByID(t.get("maMon", String.class)) : null;
        LocalDateTime ngayApDung = t.get("ngayApDung") instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) t.get("ngayApDung");
        return new PhanTramGiaBan(t.get("maPTGB", String.class), ((Number) t.get("phanTramLoi")).intValue(), ngayApDung, loaiMon, mon);
    }

    private static final String SELECT = "SELECT maPTGB, maLoaiMon, maMon, phanTramLoi, ngayApDung FROM PhanTramGiaBan";

    public static List<PhanTramGiaBan> getAll() {
        List<PhanTramGiaBan> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery(SELECT + " ORDER BY maPTGB DESC", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("PTGB.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static PhanTramGiaBan getByID(String maPTGB) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maPTGB=:ma", Tuple.class).setParameter("ma", maPTGB).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("PTGB.getByID(): " + e.getMessage()); }
        return null;
    }

    public static PhanTramGiaBan getLatestForMon(String maMon) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maMon=:ma ORDER BY ngayApDung DESC, maPTGB DESC LIMIT 1", Tuple.class)
                .setParameter("ma", maMon).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("PTGB.getLatestForMon(): " + e.getMessage()); }
        return null;
    }

    public static PhanTramGiaBan getLatestForLoaiMon(String maLoaiMon) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maLoaiMon=:ma AND maMon IS NULL ORDER BY ngayApDung DESC, maPTGB DESC LIMIT 1", Tuple.class)
                .setParameter("ma", maLoaiMon).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("PTGB.getLatestForLoaiMon(): " + e.getMessage()); }
        return null;
    }

    public static PhanTramGiaBan getLatest() {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " ORDER BY maPTGB DESC LIMIT 1", Tuple.class).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("PTGB.getLatest(): " + e.getMessage()); }
        return null;
    }

    public static boolean insert(PhanTramGiaBan pt) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO PhanTramGiaBan(maPTGB, maLoaiMon, maMon, phanTramLoi, ngayApDung) VALUES (:ma, :lm, :mon, :ptl, :nad)")
                .setParameter("ma", pt.getMaPTGB())
                .setParameter("lm", pt.getLoaiMon() != null ? pt.getLoaiMon().getMaLoaiMon() : null)
                .setParameter("mon", pt.getMon() != null ? pt.getMon().getMaMon() : null)
                .setParameter("ptl", pt.getPhanTramLoi())
                .setParameter("nad", pt.getNgayApDung() != null ? Timestamp.valueOf(pt.getNgayApDung()) : null).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("PTGB.insert(): " + e.getMessage()); return false; }
    }

    public boolean update(PhanTramGiaBan pt) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE PhanTramGiaBan SET phanTramLoi=:ptl, ngayApDung=:nad WHERE maPTGB=:ma")
                .setParameter("ptl", pt.getPhanTramLoi())
                .setParameter("nad", pt.getNgayApDung() != null ? Timestamp.valueOf(pt.getNgayApDung()) : null)
                .setParameter("ma", pt.getMaPTGB()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("PTGB.update(): " + e.getMessage()); return false; }
    }

    public boolean delete(String maPTGB) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM PhanTramGiaBan WHERE maPTGB=:ma").setParameter("ma", maPTGB).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("PTGB.delete(): " + e.getMessage()); return false; }
    }

    public static PhanTramGiaBan getEffectiveForMonAtDate(String maMon, LocalDateTime ngayHD) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maMon=:ma AND ngayApDung <= :ngay ORDER BY ngayApDung DESC LIMIT 1", Tuple.class)
                .setParameter("ma", maMon).setParameter("ngay", Timestamp.valueOf(ngayHD)).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("PTGB.getEffectiveForMonAtDate(): " + e.getMessage()); }
        return null;
    }

    public static PhanTramGiaBan getEffectiveForLoaiMonAtDate(String maLoaiMon, LocalDateTime ngayHD) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maLoaiMon=:ma AND ngayApDung <= :ngay AND maMon IS NULL ORDER BY ngayApDung DESC LIMIT 1", Tuple.class)
                .setParameter("ma", maLoaiMon).setParameter("ngay", Timestamp.valueOf(ngayHD)).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("PTGB.getEffectiveForLoaiMonAtDate(): " + e.getMessage()); }
        return null;
    }

    public static boolean existsTodayForMon(String maMon) {
        try (EntityManager em = em()) {
            return !em.createNativeQuery("SELECT 1 FROM PhanTramGiaBan WHERE maMon=:ma AND DATE(ngayApDung) = CURDATE()")
                .setParameter("ma", maMon).getResultList().isEmpty();
        } catch (Exception e) { System.err.println("existsTodayForMon(): " + e.getMessage()); return true; }
    }

    public static boolean existsTodayForLoaiMon(String maLoaiMon) {
        try (EntityManager em = em()) {
            return !em.createNativeQuery("SELECT 1 FROM PhanTramGiaBan WHERE maLoaiMon=:ma AND maMon IS NULL AND DATE(ngayApDung) = CURDATE()")
                .setParameter("ma", maLoaiMon).getResultList().isEmpty();
        } catch (Exception e) { System.err.println("existsTodayForLoaiMon(): " + e.getMessage()); return true; }
    }
}
