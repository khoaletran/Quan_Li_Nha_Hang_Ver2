package dao;

import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {

    private static EntityManager em() {
        return JpaConfig.getEntityManagerFactory().createEntityManager();
    }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private static Ban mapRow(Tuple t) {
        LoaiBan loaiBan = new LoaiBan(t.get("maLoaiBan", String.class), ((Number) t.get("soLuong")).intValue(), t.get("tenLoaiBan", String.class));
        KhuVuc khuVuc = new KhuVuc(t.get("maKhuVuc", String.class), t.get("tenKhuVuc", String.class));
        return new Ban(t.get("maBan", String.class), khuVuc, loaiBan, toBool(t.get("trangThai")));
    }

    private static final String SELECT_JOIN =
        "SELECT b.maBan, b.trangThai, lb.maLoaiBan, lb.soLuong, lb.tenLoaiBan, kv.maKhuVuc, kv.tenKhuVuc " +
        "FROM Ban b JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc";

    public static List<Ban> getAll() {
        List<Ban> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT_JOIN + " ORDER BY b.maBan", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(Ban ban, boolean trangThai) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO Ban(maBan, trangThai, maLoaiBan, maKhuVuc) VALUES (:ma, :tt, :lb, :kv)")
                .setParameter("ma", ban.getMaBan()).setParameter("tt", trangThai)
                .setParameter("lb", ban.getLoaiBan().getMaLoaiBan()).setParameter("kv", ban.getKhuVuc().getMaKhuVuc()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace(); return false;
        }
    }

    public static boolean update(Ban ban, boolean trangThai) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE Ban SET trangThai=:tt, maLoaiBan=:lb, maKhuVuc=:kv WHERE maBan=:ma")
                .setParameter("tt", trangThai).setParameter("lb", ban.getLoaiBan().getMaLoaiBan())
                .setParameter("kv", ban.getKhuVuc().getMaKhuVuc()).setParameter("ma", ban.getMaBan()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace(); return false;
        }
    }

    public static boolean delete(String maBan) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM Ban WHERE maBan=:ma").setParameter("ma", maBan).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace(); return false;
        }
    }

    public static Ban getByID(String maBan) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT_JOIN + " WHERE b.maBan=:ma", Tuple.class).setParameter("ma", maBan).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static List<Ban> getAllTrong() {
        List<Ban> list = new ArrayList<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT_JOIN + " WHERE b.trangThai = 0", Tuple.class).getResultList();
            for (Tuple t : rows) list.add(mapRow(t));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String taoMaBanChoTheoKhuVuc(KhuVuc khuVuc) {
        String prefix = switch (khuVuc.getMaKhuVuc()) {
            case "KV0001" -> "WO"; case "KV0002" -> "WI"; case "KV0003" -> "WV"; default -> "WX";
        };
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maBan FROM Ban WHERE maBan LIKE :p ORDER BY maBan DESC LIMIT 1")
                .setParameter("p", prefix + "%").getResultList();
            int nextId = 1;
            if (!r.isEmpty()) nextId = Integer.parseInt(((String) r.get(0)).substring(2)) + 1;
            return prefix + String.format("%04d", nextId);
        } catch (Exception e) { e.printStackTrace(); return prefix + "0001"; }
    }

    public static String getMaBanCuoiTheoKhuVuc(String khuVuc) {
        String prefix = switch (khuVuc) {
            case "Indoor" -> "BI"; case "Outdoor" -> "BO"; case "VIP" -> "BV"; default -> "B";
        };
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maBan FROM Ban WHERE maBan LIKE :p ORDER BY maBan DESC LIMIT 1")
                .setParameter("p", prefix + "%").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static boolean conBanTrongTheoKhuVuc(String maKV, int soLuong, LocalDateTime selected) {
        String sql = "SELECT 1 FROM Ban b JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan " +
                     "WHERE b.maBan LIKE 'B%' AND b.maKhuVuc=:kv AND lb.soLuong >= :sl " +
                     "AND NOT EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.maBan = b.maBan AND hd.trangThai IN (0,1) " +
                     "AND hd.tgCheckin IS NOT NULL AND hd.tgCheckin <= :sel AND (hd.tgCheckout IS NULL OR hd.tgCheckout > :sel)) LIMIT 1";
        try (EntityManager em = em()) {
            return !em.createNativeQuery(sql).setParameter("kv", maKV).setParameter("sl", soLuong)
                .setParameter("sel", Timestamp.valueOf(selected)).getResultList().isEmpty();
        } catch (Exception e) { System.err.println("conBanTrongTheoKhuVuc: " + e.getMessage()); return false; }
    }

    public static boolean conBanTrongTheoLoaiVaKV(String maKV, String maLB, int soLuongKhach, LocalDateTime selected) {
        String sql = "SELECT 1 FROM Ban b JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan " +
                     "WHERE b.maBan LIKE 'B%' AND b.maKhuVuc=:kv AND b.maLoaiBan=:lb AND lb.soLuong >= :sl " +
                     "AND NOT EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.maBan = b.maBan AND hd.trangThai IN (0,1) " +
                     "AND hd.tgCheckin IS NOT NULL AND hd.tgCheckin <= :sel AND (hd.tgCheckout IS NULL OR hd.tgCheckout > :sel)) LIMIT 1";
        try (EntityManager em = em()) {
            return !em.createNativeQuery(sql).setParameter("kv", maKV).setParameter("lb", maLB)
                .setParameter("sl", soLuongKhach).setParameter("sel", Timestamp.valueOf(selected)).getResultList().isEmpty();
        } catch (Exception e) { System.err.println("conBanTrongTheoLoaiVaKV: " + e.getMessage()); return false; }
    }

    public static Ban getMotBanTrongTheoLoaiVaKV(String maKV, String maLB, int soLuongKhach, LocalDateTime selected) {
        String sql = SELECT_JOIN +
                     " WHERE b.maBan LIKE 'B%' AND b.maKhuVuc=:kv AND b.maLoaiBan=:lb AND lb.soLuong >= :sl " +
                     "AND NOT EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.maBan = b.maBan AND hd.trangThai IN (0,1) " +
                     "AND hd.tgCheckin IS NOT NULL AND hd.tgCheckin <= :sel AND (hd.tgCheckout IS NULL OR hd.tgCheckout > :sel)) ORDER BY b.maBan LIMIT 1";
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(sql, Tuple.class).setParameter("kv", maKV).setParameter("lb", maLB)
                .setParameter("sl", soLuongKhach).setParameter("sel", Timestamp.valueOf(selected)).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("getMotBanTrongTheoLoaiVaKV: " + e.getMessage()); }
        return null;
    }

    public static int getMaxSucChuaTheoKhuVuc(String maKV) {
        String sql = "SELECT IFNULL(MAX(lb.soLuong), 0) AS maxSL FROM Ban b JOIN LoaiBan lb ON b.maLoaiBan = lb.maLoaiBan " +
                     "WHERE b.maBan LIKE 'B%' AND b.maKhuVuc=:kv";
        try (EntityManager em = em()) {
            Object result = em.createNativeQuery(sql).setParameter("kv", maKV).getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) { System.err.println("getMaxSucChuaTheoKhuVuc: " + e.getMessage()); return 0; }
    }
}
