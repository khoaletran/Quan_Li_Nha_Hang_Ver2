package dao;

import entity.KhuyenMai;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private static KhuyenMai mapRow(Tuple t) {
        LocalDate ph = t.get("ngayPhatHanh") instanceof Date d ? d.toLocalDate() : (LocalDate) t.get("ngayPhatHanh");
        LocalDate kt = t.get("ngayKetThuc") instanceof Date d ? d.toLocalDate() : (LocalDate) t.get("ngayKetThuc");
        return new KhuyenMai(t.get("maKM", String.class), t.get("tenKM", String.class),
            ((Number) t.get("soLuong")).intValue(), ph, kt,
            t.get("maThayThe", String.class), ((Number) t.get("phanTramGiamGia")).intValue(), toBool(t.get("uuDai")));
    }

    private static final String SELECT = "SELECT maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai FROM KhuyenMai";

    public static List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery(SELECT + " ORDER BY maKM", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("KhuyenMaiDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static boolean insert(KhuyenMai km) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO KhuyenMai(maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai) VALUES (:ma, :ten, :sl, :ph, :kt, :mtt, :ptgg, :ud)")
                .setParameter("ma", km.getMaKM()).setParameter("ten", km.getTenKM()).setParameter("sl", km.getSoLuong())
                .setParameter("ph", km.getNgayPhatHanh()).setParameter("kt", km.getNgayKetThuc())
                .setParameter("mtt", km.getMaThayThe()).setParameter("ptgg", km.getPhanTRamGiamGia()).setParameter("ud", km.isUuDai()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("KhuyenMaiDAO.insert(): " + e.getMessage()); return false; }
    }

    public static boolean update(KhuyenMai km) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE KhuyenMai SET tenKM=:ten, soLuong=:sl, ngayPhatHanh=:ph, ngayKetThuc=:kt, maThayThe=:mtt, phanTramGiamGia=:ptgg, uuDai=:ud WHERE maKM=:ma")
                .setParameter("ten", km.getTenKM()).setParameter("sl", km.getSoLuong())
                .setParameter("ph", km.getNgayPhatHanh()).setParameter("kt", km.getNgayKetThuc())
                .setParameter("mtt", km.getMaThayThe()).setParameter("ptgg", km.getPhanTRamGiamGia())
                .setParameter("ud", km.isUuDai()).setParameter("ma", km.getMaKM()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("KhuyenMaiDAO.update(): " + e.getMessage()); return false; }
    }

    public static boolean delete(String maKM) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM KhuyenMai WHERE maKM=:ma").setParameter("ma", maKM).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("KhuyenMaiDAO.delete(): " + e.getMessage()); return false; }
    }

    public static KhuyenMai getByID(String maKM) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maKM=:ma", Tuple.class).setParameter("ma", maKM).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("KhuyenMaiDAO.getByID(): " + e.getMessage()); }
        return null;
    }

    public static String maKMCuoi() {
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maKM FROM KhuyenMai ORDER BY maKM DESC LIMIT 1").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) { System.err.println("KhuyenMaiDAO.maKMCuoi(): " + e.getMessage()); }
        return null;
    }

    public static String taoMaKMTiepTheo() {
        String maCuoi = maKMCuoi();
        int soMoi = 1;
        if (maCuoi != null && maCuoi.startsWith("KM")) {
            try { soMoi = Integer.parseInt(maCuoi.substring(2)) + 1; } catch (NumberFormatException ignored) {}
        }
        return String.format("KM%04d", soMoi);
    }

    public static KhuyenMai getByCode(String code) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE maKM=:ma OR maThayThe=:ma ORDER BY maKM LIMIT 1", Tuple.class)
                .setParameter("ma", code).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("KhuyenMaiDAO.getByCode(): " + e.getMessage()); }
        return null;
    }

    public static boolean giamSoLuongAtomic(String maKM) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE KhuyenMai SET soLuong = soLuong - 1 WHERE maKM=:ma AND soLuong > 0").setParameter("ma", maKM).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("KhuyenMaiDAO.giamSoLuongAtomic(): " + e.getMessage()); return false; }
    }

    public static boolean tangSoLuongAtomic(String maKM) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE KhuyenMai SET soLuong = soLuong + 1 WHERE maKM=:ma").setParameter("ma", maKM).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("KhuyenMaiDAO.tangSoLuongAtomic(): " + e.getMessage()); return false; }
    }

    public static boolean insertVoucherHuyDatBan(String maHDHuy, int phanTramGiamGia, LocalDate ngayHetHan) {
        String maKM = taoMaKMTiepTheo();
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO KhuyenMai(maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai) VALUES (:ma, :ten, 1, :ph, :kt, NULL, :ptgg, 1)")
                .setParameter("ma", maKM).setParameter("ten", maHDHuy)
                .setParameter("ph", LocalDate.now()).setParameter("kt", ngayHetHan)
                .setParameter("ptgg", phanTramGiamGia).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("insertVoucherHuyDatBan(): " + e.getMessage()); return false; }
    }
}
