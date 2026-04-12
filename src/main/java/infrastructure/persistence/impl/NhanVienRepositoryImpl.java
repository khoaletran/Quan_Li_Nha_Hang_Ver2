package infrastructure.persistence.impl;

import core.entity.NhanVien;
import core.repository.NhanVienRepository;
import infrastructure.persistence.AbstractRepository;

import java.util.Optional;

public class NhanVienRepositoryImpl extends AbstractRepository<NhanVien, String>
        implements NhanVienRepository {

    public NhanVienRepositoryImpl() { super(NhanVien.class); }

    @Override
    public Optional<NhanVien> findByMatKhau(String maNV, String matKhau) {
        return findFirstByHql(
            "FROM NhanVien WHERE maNV = :maNV AND matKhau = :mk AND trangThai = true",
            "maNV", maNV, "mk", matKhau
        );
    }

    @Override
    public Optional<NhanVien> findBySdt(String sdt) {
        return findFirstByHql("FROM NhanVien WHERE sdt = :sdt", "sdt", sdt);
    }
}
