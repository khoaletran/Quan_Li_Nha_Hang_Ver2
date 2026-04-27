package dao;

import entity.NhanVien;
import entity.PhieuKetCa;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuKetCaDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private static LocalDateTime toDateTime(Object val) {
        if (val instanceof Timestamp ts) return ts.toLocalDateTime();
        if (val instanceof LocalDateTime ldt) return ldt;
        return null;
    }

    private static PhieuKetCa mapRow(Tuple t) {
        PhieuKetCa p = new PhieuKetCa();
        p.setMaPhieu(t.get("maPhieu", String.class));
        p.setCa(toBool(t.get("ca")));
        p.setSoHoaDon(t.get("soHoaDon") != null ? ((Number) t.get("soHoaDon")).intValue() : 0);
        p.setTienMat(t.get("tienMat") != null ? ((Number) t.get("tienMat")).doubleValue() : 0);
        p.setTienCK(t.get("tienCK") != null ? ((Number) t.get("tienCK")).doubleValue() : 0);
        p.setTienChenhLech(t.get("tienChenhLech") != null ? ((Number) t.get("tienChenhLech")).doubleValue() : 0);
        p.setNgayKetCaFromDB(toDateTime(t.get("ngayKetCa")));
        p.setMoTa(t.get("moTa", String.class));
        p.setTgLogIn(toDateTime(t.get("tgLogIn")));
        NhanVien nv = NhanVienDAO.getByID(t.get("maNV", String.class));
        p.setNhanVien(nv);
        return p;
    }

    public static List<PhieuKetCa> getAll() {
        List<PhieuKetCa> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maPhieu, maNV, ca, soHoaDon, tienMat, tienCK, tienChenhLech, ngayKetCa, moTa, tgLogIn FROM PhieuKetCa ORDER BY maPhieu DESC", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("PhieuKetCaDAO.getAll(): " + e.getMessage()); }
        return ds;
    }

    public static List<PhieuKetCa> getAllForTraCuu() {
        List<PhieuKetCa> ds = new ArrayList<>();
        String sql = "SELECT p.maPhieu, p.maNV, p.ca, p.soHoaDon, p.tienMat, p.tienCK, p.tienChenhLech, p.ngayKetCa, p.moTa, p.tgLogIn, n.tenNV, n.sdt " +
                     "FROM PhieuKetCa p JOIN NhanVien n ON p.maNV = n.maNV ORDER BY p.maPhieu DESC";
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(sql, Tuple.class).getResultList();
            for (Tuple t : rows) {
                PhieuKetCa p = new PhieuKetCa();
                p.setMaPhieu(t.get("maPhieu", String.class));
                p.setCa(toBool(t.get("ca")));
                p.setSoHoaDon(t.get("soHoaDon") != null ? ((Number) t.get("soHoaDon")).intValue() : 0);
                p.setTienMat(t.get("tienMat") != null ? ((Number) t.get("tienMat")).doubleValue() : 0);
                p.setTienCK(t.get("tienCK") != null ? ((Number) t.get("tienCK")).doubleValue() : 0);
                p.setTienChenhLech(t.get("tienChenhLech") != null ? ((Number) t.get("tienChenhLech")).doubleValue() : 0);
                p.setNgayKetCaFromDB(toDateTime(t.get("ngayKetCa")));
                p.setMoTa(t.get("moTa", String.class));
                p.setTgLogIn(toDateTime(t.get("tgLogIn")));
                NhanVien nv = new NhanVien();
                nv.setMaNV(t.get("maNV", String.class));
                nv.setTenNV(t.get("tenNV", String.class));
                nv.setSdt(t.get("sdt", String.class));
                p.setNhanVien(nv);
                ds.add(p);
            }
        } catch (Exception e) { System.err.println("PhieuKetCaDAO.getAllForTraCuu(): " + e.getMessage()); }
        return ds;
    }

    public boolean insert(PhieuKetCa phieu) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO PhieuKetCa(maPhieu, maNV, ca, soHoaDon, tienMat, tienCK, tienChenhLech, ngayKetCa, moTa, tgLogIn) VALUES (:ma, :nv, :ca, :sohd, :tm, :tck, :tcl, :ngay, :mo, :login)")
                .setParameter("ma", phieu.getMaPhieu()).setParameter("nv", phieu.getNhanVien().getMaNV())
                .setParameter("ca", phieu.isCa()).setParameter("sohd", phieu.getSoHoaDon())
                .setParameter("tm", phieu.getTienMat()).setParameter("tck", phieu.getTienCK())
                .setParameter("tcl", phieu.getTienChenhLech())
                .setParameter("ngay", phieu.getNgayKetCa() != null ? Timestamp.valueOf(phieu.getNgayKetCa()) : null)
                .setParameter("mo", phieu.getMoTa())
                .setParameter("login", phieu.getTgLogIn() != null ? Timestamp.valueOf(phieu.getTgLogIn()) : null).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("PhieuKetCaDAO.insert(): " + e.getMessage()); return false; }
    }

    public String getMaxMaPhieu() {
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maPhieu FROM PhieuKetCa ORDER BY maPhieu DESC LIMIT 1").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) { System.err.println("PhieuKetCaDAO.getMaxMaPhieu(): " + e.getMessage()); }
        return null;
    }

    public String generateNewMaPhieu() {
        String max = getMaxMaPhieu();
        int next = 1;
        if (max != null && max.startsWith("MP")) next = Integer.parseInt(max.substring(2)) + 1;
        return String.format("MP%04d", next);
    }

    public static String getMaPhieuKCCuoiTheoNgay(String ca, String ngay) {
        String prefix = "MP" + ca + ngay;
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maPhieu FROM PhieuKetCa WHERE maPhieu LIKE :p ORDER BY maPhieu DESC LIMIT 1")
                .setParameter("p", prefix + "%").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) { System.err.println("Lỗi getMaPhieuKCCuoiTheoNgay: " + e.getMessage()); }
        return null;
    }
}
