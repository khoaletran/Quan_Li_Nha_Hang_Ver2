package dao;

import entity.Coc;
import entity.KhuVuc;
import entity.LoaiBan;
import infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;

public class CocDAO {

    private static EntityManager em() { return JpaConfig.getEntityManagerFactory().createEntityManager(); }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() != 0;
    }

    private Coc mapRow(Tuple t) {
        Coc c = new Coc();
        c.setMaCoc(t.get("maCoc", String.class));
        c.setLoaiCoc(toBool(t.get("loaiCoc")));
        if (c.isLoaiCoc()) {
            c.setPhanTramCoc(t.get("phanTramCoc") != null ? ((Number) t.get("phanTramCoc")).intValue() : 0);
            c.setSoTienCoc(0);
        } else {
            c.setSoTienCoc(t.get("soTienCoc") != null ? ((Number) t.get("soTienCoc")).doubleValue() : 0);
            c.setPhanTramCoc(0);
        }
        String maKV = t.get("maKhuVuc", String.class);
        if (maKV != null) c.setKhuVuc(new KhuVuc(maKV, t.get("tenKhuVuc", String.class)));
        String maLB = t.get("maLoaiBan", String.class);
        if (maLB != null) c.setLoaiBan(new LoaiBan(maLB, t.get("soLuong") != null ? ((Number) t.get("soLuong")).intValue() : 0, t.get("tenLoaiBan", String.class)));
        return c;
    }

    private static final String SELECT =
        "SELECT c.maCoc, c.loaiCoc, c.phanTramCoc, c.soTienCoc, " +
        "c.maKhuVuc, kv.tenKhuVuc, c.maLoaiBan, lb.soLuong, lb.tenLoaiBan " +
        "FROM Coc c LEFT JOIN KhuVuc kv ON c.maKhuVuc = kv.maKhuVuc LEFT JOIN LoaiBan lb ON c.maLoaiBan = lb.maLoaiBan";

    public List<Coc> getAll() {
        List<Coc> list = new ArrayList<>();
        try (EntityManager em = em()) {
            @SuppressWarnings("unchecked")
            List<Tuple> rows = em.createNativeQuery(SELECT + " ORDER BY c.maCoc", Tuple.class).getResultList();
            for (Tuple t : rows) list.add(mapRow(t));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Coc getLatest() {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " ORDER BY c.maCoc DESC LIMIT 1", Tuple.class).getResultList();
            if (!rows.isEmpty()) return mapRow(rows.get(0));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Coc coc) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            em.createNativeQuery("INSERT INTO Coc(maCoc, loaiCoc, phanTramCoc, soTienCoc, maKhuVuc, maLoaiBan) VALUES (:ma, :loai, :ptc, :stc, :kv, :lb)")
                .setParameter("ma", coc.getMaCoc()).setParameter("loai", coc.isLoaiCoc())
                .setParameter("ptc", coc.isLoaiCoc() ? coc.getPhanTramCoc() : 0)
                .setParameter("stc", coc.isLoaiCoc() ? 0 : coc.getSoTienCoc())
                .setParameter("kv", coc.getKhuVuc() != null ? coc.getKhuVuc().getMaKhuVuc() : null)
                .setParameter("lb", coc.getLoaiBan() != null ? coc.getLoaiBan().getMaLoaiBan() : null).executeUpdate();
            tx.commit(); return true;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); e.printStackTrace(); return false; }
    }

    public boolean update(Coc coc) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("UPDATE Coc SET loaiCoc=:loai, phanTramCoc=:ptc, soTienCoc=:stc, maKhuVuc=:kv, maLoaiBan=:lb WHERE maCoc=:ma")
                .setParameter("loai", coc.isLoaiCoc())
                .setParameter("ptc", coc.isLoaiCoc() ? coc.getPhanTramCoc() : 0)
                .setParameter("stc", coc.isLoaiCoc() ? 0 : coc.getSoTienCoc())
                .setParameter("kv", coc.getKhuVuc() != null ? coc.getKhuVuc().getMaKhuVuc() : null)
                .setParameter("lb", coc.getLoaiBan() != null ? coc.getLoaiBan().getMaLoaiBan() : null)
                .setParameter("ma", coc.getMaCoc()).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); e.printStackTrace(); return false; }
    }

    public static Coc getByKhuVucVaLoaiBan(String maKV, String maLB) {
        try (EntityManager em = em()) {
            List<Tuple> rows = em.createNativeQuery(SELECT + " WHERE c.maKhuVuc=:kv AND c.maLoaiBan=:lb", Tuple.class)
                .setParameter("kv", maKV).setParameter("lb", maLB).getResultList();
            if (!rows.isEmpty()) return new CocDAO().mapRow(rows.get(0));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean delete(String maCoc) {
        EntityTransaction tx = null;
        try (EntityManager em = em()) {
            tx = em.getTransaction(); tx.begin();
            int r = em.createNativeQuery("DELETE FROM Coc WHERE maCoc=:ma").setParameter("ma", maCoc).executeUpdate();
            tx.commit(); return r > 0;
        } catch (Exception e) { if (tx != null && tx.isActive()) tx.rollback(); e.printStackTrace(); return false; }
    }
}
