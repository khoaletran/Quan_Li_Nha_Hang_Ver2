package dao;

import entity.HangKhachHang;
import entity.KhachHang;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private static final String SELECT_JOIN =
        "SELECT kh.maKH, kh.maHang, kh.tenKH, kh.sdt, kh.gioiTinh, kh.diemTichLuy, " +
        "hkh.diemHang, hkh.giamGia, hkh.moTa " +
        "FROM KhachHang kh JOIN HangKhachHang hkh ON kh.maHang = hkh.maHang";

    private static KhachHang mapRow(Tuple t) {
        HangKhachHang hang = new HangKhachHang(
            t.get("maHang", String.class), t.get("moTa", String.class),
            ((Number) t.get("giamGia")).intValue(), ((Number) t.get("diemHang")).intValue());
        return new KhachHang(t.get("maKH", String.class), ((Number) t.get("diemTichLuy")).intValue(),
            toBool(t.get("gioiTinh")), t.get("sdt", String.class), t.get("tenKH", String.class), hang);
    }

    public static List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery(SELECT_JOIN, Tuple.class).getResultList();
            for (Tuple t : rows) list.add(mapRow(t));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insert(KhachHang kh) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO KhachHang(maKH, maHang, tenKH, sdt, gioiTinh, diemTichLuy) VALUES (:ma, :hang, :ten, :sdt, :gt, :diem)")
                .setParameter("ma", kh.getMaKhachHang()).setParameter("hang", kh.getHangKhachHang() != null ? kh.getHangKhachHang().getMaHang() : null)
                .setParameter("ten", kh.getTenKhachHang()).setParameter("sdt", kh.getSdt())
                .setParameter("gt", kh.isGioiTinh()).setParameter("diem", kh.getDiemTichLuy()).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); e.printStackTrace(); return false; }
    }

    public static boolean update(KhachHang kh) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE KhachHang SET tenKH=:ten, sdt=:sdt, gioiTinh=:gt, diemTichLuy=:diem WHERE maKH=:ma")
                .setParameter("ten", kh.getTenKhachHang()).setParameter("sdt", kh.getSdt())
                .setParameter("gt", kh.isGioiTinh()).setParameter("diem", kh.getDiemTichLuy())
                .setParameter("ma", kh.getMaKhachHang()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); e.printStackTrace(); return false; }
    }

    public static boolean delete(String maKH) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM KhachHang WHERE maKH=:ma").setParameter("ma", maKH).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); e.printStackTrace(); return false; }
    }

    public KhachHang findBySDT(String sdt) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT_JOIN + " WHERE kh.sdt=:sdt", Tuple.class).setParameter("sdt", sdt).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static String getMaKHCuoi() {
        try (EntityManager em = em()) {
            List<?> r = em.createNativeQuery("SELECT maKH FROM KhachHang ORDER BY maKH DESC LIMIT 1").getResultList();
            if (!r.isEmpty()) return (String) r.get(0);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public KhachHang getById(String maKH) { return getByID(maKH); }

    public static KhachHang getByID(String maKH) {
        String sql = "SELECT kh.maKH, kh.maHang, kh.tenKH, kh.sdt, kh.gioiTinh, kh.diemTichLuy, " +
                     "hkh.diemHang, hkh.giamGia, hkh.moTa " +
                     "FROM KhachHang kh LEFT JOIN HangKhachHang hkh ON kh.maHang = hkh.maHang WHERE kh.maKH=:ma";
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(sql, Tuple.class).setParameter("ma", maKH).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static String tuSinhMaKhachHang() {
        String lastMa = getMaKHCuoi();
        int so = (lastMa != null) ? Integer.parseInt(lastMa.substring(2)) + 1 : 1;
        return String.format("KH%04d", so);
    }

    public KhachHang taoKhachHangMoi(KhachHang kh) { return insert(kh) ? kh : null; }

    public static String maKHCuoi() { return getMaKHCuoi(); }
}
