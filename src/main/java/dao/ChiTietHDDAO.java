package dao;

import entity.*;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class ChiTietHDDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static ChiTietHoaDon mapCTHDFull(Tuple t) {
        Mon mon = new Mon();
        mon.setMaMon(t.get("maMon", String.class));
        mon.setTenMon(t.get("tenMon", String.class));
        mon.setGiaGoc(t.get("giaGoc") != null ? ((Number) t.get("giaGoc")).doubleValue() : 0);
        mon.setHinhAnh(t.get("hinhAnh", String.class));
        LoaiMon loai = new LoaiMon();
        loai.setMaLoaiMon(t.get("maLoaiMon", String.class));
        loai.setTenLoaiMon(t.get("tenLoaiMon", String.class));
        mon.setLoaiMon(loai);
        HoaDon hd = new HoaDon();
        hd.setMaHD(t.get("maHD", String.class));
        Object tgLap = t.get("tgLapHD");
        hd.setTgLapHD(tgLap instanceof Timestamp ts ? ts.toLocalDateTime() : (tgLap instanceof LocalDateTime ldt ? ldt : null));
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setHoaDon(hd); ct.setMon(mon);
        ct.setSoLuong(t.get("soLuong") != null ? ((Number) t.get("soLuong")).intValue() : 0);
        int ptgb = t.get("phanTramLoi") != null ? ((Number) t.get("phanTramLoi")).intValue() : 0;
        ct.setThanhTien(mon.getGiaGoc() * (1 + ptgb / 100.0) * ct.getSoLuong());
        return ct;
    }

    private static final String SQL_BY_HD = """
        SELECT cthd.maHD, cthd.maMon, cthd.soLuong, hd.tgLapHD,
            m.tenMon, m.giaGoc, m.hinhAnh, lm.maLoaiMon, lm.tenLoaiMon,
            COALESCE(ptMon.phanTramLoi, ptLoai.phanTramLoi, 0) AS phanTramLoi
        FROM ChiTietHoaDon cthd
        JOIN Mon m ON cthd.maMon = m.maMon
        JOIN LoaiMon lm ON m.loaiMon = lm.maLoaiMon
        JOIN HoaDon hd ON hd.maHD = cthd.maHD
        LEFT JOIN (SELECT p1.maMon, p1.phanTramLoi, p1.ngayApDung FROM PhanTramGiaBan p1) ptMon
            ON ptMon.maMon = m.maMon AND ptMon.ngayApDung = (
                SELECT MAX(p2.ngayApDung) FROM PhanTramGiaBan p2 WHERE p2.maMon = m.maMon AND p2.ngayApDung <= hd.tgLapHD)
        LEFT JOIN (SELECT p3.maLoaiMon, p3.phanTramLoi, p3.ngayApDung FROM PhanTramGiaBan p3 WHERE p3.maMon IS NULL) ptLoai
            ON ptLoai.maLoaiMon = lm.maLoaiMon AND ptLoai.ngayApDung = (
                SELECT MAX(p4.ngayApDung) FROM PhanTramGiaBan p4
                WHERE p4.maLoaiMon = lm.maLoaiMon AND p4.maMon IS NULL AND p4.ngayApDung <= hd.tgLapHD)
    """;

    public static List<ChiTietHoaDon> getAllByMaHD(String maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SQL_BY_HD + " WHERE cthd.maHD = :maHD", Tuple.class).setParameter("maHD", maHD).getResultList();
            for (Tuple t : rows) list.add(mapCTHDFull(t));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static List<ChiTietHoaDon> getByMaHD(String maHD) { return getAllByMaHD(maHD); }

    public boolean insert(ChiTietHoaDon ct) {
        if (ct == null || ct.getHoaDon() == null || ct.getHoaDon().getMaHD() == null) {
            System.err.println("Lỗi thêm CTHD: HoaDon hoặc maHD bị null");
            return false;
        }
        String maHD = ct.getHoaDon().getMaHD();

        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction();
            tx.begin();
            em.createNativeQuery("INSERT INTO ChiTietHoaDon(maHD, maMon, soLuong) VALUES (:hd, :mon, :sl)")
                .setParameter("hd", maHD)
                .setParameter("mon", ct.getMon().getMaMon())
                .setParameter("sl", ct.getSoLuong())
                .executeUpdate();
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("Lỗi thêm CTHD: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String maHD, String maMon) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM ChiTietHoaDon WHERE maHD=:hd AND maMon=:mon")
                .setParameter("hd", maHD).setParameter("mon", maMon).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi xóa CTHD: " + e.getMessage()); return false; }
    }

    public boolean update(ChiTietHoaDon ct) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE ChiTietHoaDon SET soLuong=:sl WHERE maHD=:hd AND maMon=:mon")
                .setParameter("sl", ct.getSoLuong()).setParameter("hd", ct.getHoaDon().getMaHD())
                .setParameter("mon", ct.getMon().getMaMon()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi cập nhật CTHD: " + e.getMessage()); return false; }
    }

    public static List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery(SQL_BY_HD, Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapCTHDFull(t));
        } catch (Exception e) { System.err.println("Lỗi getAll CTHD: " + e.getMessage()); }
        return ds;
    }

    public static List<ChiTietHoaDon> getAllInDay() {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            String sql = SQL_BY_HD + " WHERE hd.tgCheckin >= CURDATE() AND hd.tgCheckin < DATE_ADD(CURDATE(), INTERVAL 1 DAY)";
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery(sql, Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapCTHDFull(t));
        } catch (Exception e) { System.err.println("Lỗi getAllInDay CTHD: " + e.getMessage()); }
        return ds;
    }

    public static List<ChiTietHoaDon> getAllCTHDTheoThangNam(int nam, int thang) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT m.maMon, m.tenMon, m.hinhAnh, m.soLuong AS soLuongTon, SUM(cthd.soLuong) AS tongSoLuong " +
                     "FROM ChiTietHoaDon cthd JOIN HoaDon hd ON cthd.maHD = hd.maHD JOIN Mon m ON cthd.maMon = m.maMon " +
                     "WHERE YEAR(hd.tgLapHD) = :nam";
        if (thang != 0) sql += " AND MONTH(hd.tgLapHD) = :thang";
        sql += " GROUP BY m.maMon, m.tenMon, m.hinhAnh, m.soLuong ORDER BY tongSoLuong DESC";
        try (EntityManager em = em()) {
            var q = em.createNativeQuery(sql, Tuple.class).setParameter("nam", nam);
            if (thang != 0) q.setParameter("thang", thang);
            for (Tuple t : (List<Tuple>) q.getResultList()) {
                Mon m = new Mon();
                m.setMaMon(t.get("maMon", String.class));
                m.setTenMon(t.get("tenMon", String.class));
                m.setHinhAnh(t.get("hinhAnh", String.class));
                m.setSoLuong(((Number) t.get("soLuongTon")).intValue());
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMon(m); ct.setSoLuong(((Number) t.get("tongSoLuong")).intValue());
                ds.add(ct);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public static Map<String, Integer> getSoLuongTheoThangNam(int nam, int thang) {
        Map<String, Integer> map = new HashMap<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(
                "SELECT m.maMon, SUM(cthd.soLuong) AS tongSoLuong FROM ChiTietHoaDon cthd JOIN HoaDon hd ON cthd.maHD = hd.maHD JOIN Mon m ON cthd.maMon = m.maMon WHERE YEAR(hd.tgLapHD) = :nam AND MONTH(hd.tgLapHD) = :thang GROUP BY m.maMon",
                Tuple.class).setParameter("nam", nam).setParameter("thang", thang).getResultList();
            for (Tuple t : rows) map.put(t.get("maMon", String.class), ((Number) t.get("tongSoLuong")).intValue());
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}
