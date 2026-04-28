package dao;

import entity.NhanVien;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    private static EntityManager em() {
        return JpaConfig.getEntityManagerFactory().createEntityManager();
    }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private static NhanVien mapRow(Tuple t) {
        NhanVien nv = new NhanVien();
        nv.setMaNV(t.get("maNV", String.class));
        nv.setTenNV(t.get("tenNV", String.class));
        nv.setSdt(t.get("sdt", String.class));
        nv.setGioiTinh(toBool(t.get("gioiTinh")));
        nv.setQuanLi(toBool(t.get("quanLi")));
        Object ngay = t.get("ngayVaoLam");
        if (ngay instanceof java.sql.Date d) nv.setNgayVaoLam(d.toLocalDate());
        else if (ngay instanceof LocalDate ld) nv.setNgayVaoLam(ld);
        nv.setTrangThai(toBool(t.get("trangThai")));
        nv.setMatKhau(t.get("matKhau", String.class));
        return nv;
    }

    public static List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau FROM NhanVien ORDER BY maNV", Tuple.class).getResultList();
            for (Tuple t : rows) list.add(mapRow(t));
        } catch (Exception e) {
            System.err.println("NhanVienDAO.getAll(): " + e.getMessage());
        }
        return list;
    }

    public static boolean insert(NhanVien nv) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO NhanVien(maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau) VALUES (:maNV, :tenNV, :sdt, :gt, :ql, :ngay, :tt, :mk)")
                .setParameter("maNV", nv.getMaNV()).setParameter("tenNV", nv.getTenNV()).setParameter("sdt", nv.getSdt())
                .setParameter("gt", nv.isGioiTinh()).setParameter("ql", nv.isQuanLi()).setParameter("ngay", nv.getNgayVaoLam())
                .setParameter("tt", nv.isTrangThai()).setParameter("mk", nv.getMatKhau()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("NhanVienDAO.insert(): " + e.getMessage()); return false;
        }
    }

    public static boolean update(NhanVien nv) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE NhanVien SET tenNV=:tenNV, sdt=:sdt, gioiTinh=:gt, quanLi=:ql, ngayVaoLam=:ngay, trangThai=:tt, matKhau=:mk WHERE maNV=:maNV")
                .setParameter("tenNV", nv.getTenNV()).setParameter("sdt", nv.getSdt()).setParameter("gt", nv.isGioiTinh())
                .setParameter("ql", nv.isQuanLi()).setParameter("ngay", nv.getNgayVaoLam()).setParameter("tt", nv.isTrangThai())
                .setParameter("mk", nv.getMatKhau()).setParameter("maNV", nv.getMaNV()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("NhanVienDAO.update(): " + e.getMessage()); return false;
        }
    }

    public static boolean delete(String maNV) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM NhanVien WHERE maNV=:maNV").setParameter("maNV", maNV).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("NhanVienDAO.delete(): " + e.getMessage()); return false;
        }
    }

    public static NhanVien getByID(String maNV) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau FROM NhanVien WHERE maNV=:maNV", Tuple.class)
                .setParameter("maNV", maNV).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) {
            System.err.println("NhanVienDAO.getByID(): " + e.getMessage());
        }
        return null;
    }

    public static String maNVCuoi() {
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maNV FROM NhanVien ORDER BY maNV DESC LIMIT 1").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) {
            System.err.println("NhanVienDAO.maNVCuoi(): " + e.getMessage());
        }
        return "NV0000";
    }

    public static boolean updateMatKhau(String maNV, String matKhauMoi) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE NhanVien SET matKhau=:mk WHERE maNV=:maNV")
                .setParameter("mk", matKhauMoi).setParameter("maNV", maNV).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("NhanVienDAO.updateMatKhau(): " + e.getMessage()); return false;
        }
    }
}
