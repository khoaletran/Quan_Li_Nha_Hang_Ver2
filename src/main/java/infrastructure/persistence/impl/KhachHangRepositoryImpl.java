package infrastructure.persistence.impl;

import core.entity.KhachHang;
import core.repository.KhachHangRepository;
import infrastructure.persistence.AbstractRepository;

import java.util.List;
import java.util.Optional;

public class KhachHangRepositoryImpl extends AbstractRepository<KhachHang, String>
        implements KhachHangRepository {

    public KhachHangRepositoryImpl() { super(KhachHang.class); }

    @Override
    public Optional<KhachHang> findBySdt(String sdt) {
        return findFirstByHql("FROM KhachHang WHERE sdt = :sdt", "sdt", sdt);
    }

    @Override
    public List<KhachHang> findByHangKhachHang(String maHang) {
        return findByHql("FROM KhachHang WHERE hangKhachHang.maHang = :mh", "mh", maHang);
    }
}
