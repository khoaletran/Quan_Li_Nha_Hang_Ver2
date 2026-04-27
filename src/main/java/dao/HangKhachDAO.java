package dao;

import entity.HangKhachHang;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class HangKhachDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static HangKhachHang mapRow(Tuple t) {
        return new HangKhachHang(t.get("maHang", String.class), t.get("moTa", String.class),
            ((Number) t.get("giamGia")).intValue(), ((Number) t.get("diemHang")).intValue());
    }

    public static List<HangKhachHang> getAll() {
        List<HangKhachHang> ds = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery("SELECT maHang, moTa, giamGia, diemHang FROM HangKhachHang", Tuple.class).getResultList();
            for (Tuple t : rows) ds.add(mapRow(t));
        } catch (Exception e) { System.err.println("Lỗi khi lấy danh sách hạng khách hàng: " + e.getMessage()); }
        return ds;
    }

    public boolean insert(HangKhachHang hkh) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO HangKhachHang(maHang, diemHang, giamGia, moTa) VALUES (:ma, :diem, :giam, :mo)")
                .setParameter("ma", hkh.getMaHang()).setParameter("diem", hkh.getDiemHang())
                .setParameter("giam", hkh.getGiamGia()).setParameter("mo", hkh.getMoTa()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi khi thêm hạng khách hàng: " + e.getMessage()); return false; }
    }

    public boolean update(HangKhachHang hkh) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE HangKhachHang SET diemHang=:diem, giamGia=:giam, moTa=:mo WHERE maHang=:ma")
                .setParameter("diem", hkh.getDiemHang()).setParameter("giam", hkh.getGiamGia())
                .setParameter("mo", hkh.getMoTa()).setParameter("ma", hkh.getMaHang()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi khi cập nhật hạng khách hàng: " + e.getMessage()); return false; }
    }

    public boolean delete(String maHang) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM HangKhachHang WHERE maHang=:ma").setParameter("ma", maHang).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); System.err.println("Lỗi khi xóa hạng khách hàng: " + e.getMessage()); return false; }
    }

    public static HangKhachHang getByID(String maHang) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maHang, moTa, giamGia, diemHang FROM HangKhachHang WHERE maHang=:ma", Tuple.class)
                .setParameter("ma", maHang).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("Lỗi khi lấy hạng khách hàng theo mã: " + e.getMessage()); }
        return null;
    }

    public static HangKhachHang getHangTheoDiem(int diem) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery("SELECT maHang, moTa, giamGia, diemHang FROM HangKhachHang WHERE diemHang <= :diem ORDER BY diemHang DESC LIMIT 1", Tuple.class)
                .setParameter("diem", diem).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { System.err.println("Lỗi khi lấy hạng theo điểm: " + e.getMessage()); }
        return null;
    }
}
