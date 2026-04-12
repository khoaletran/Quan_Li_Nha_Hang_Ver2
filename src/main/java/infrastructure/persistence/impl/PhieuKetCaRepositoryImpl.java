package infrastructure.persistence.impl;

import core.entity.PhieuKetCa;
import core.repository.PhieuKetCaRepository;
import infrastructure.persistence.AbstractRepository;

import java.util.List;

public class PhieuKetCaRepositoryImpl extends AbstractRepository<PhieuKetCa, String>
        implements PhieuKetCaRepository {

    public PhieuKetCaRepositoryImpl() { super(PhieuKetCa.class); }

    @Override
    public List<PhieuKetCa> findByMaNV(String maNV) {
        return findByHql("FROM PhieuKetCa WHERE nhanVien.maNV = :maNV", "maNV", maNV);
    }
}
