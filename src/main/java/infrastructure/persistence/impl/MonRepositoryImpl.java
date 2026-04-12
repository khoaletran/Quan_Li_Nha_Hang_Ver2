package infrastructure.persistence.impl;

import core.entity.Mon;
import core.repository.MonRepository;
import infrastructure.persistence.AbstractRepository;

import java.util.List;

public class MonRepositoryImpl extends AbstractRepository<Mon, String>
        implements MonRepository {

    public MonRepositoryImpl() { super(Mon.class); }

    @Override
    public List<Mon> findByLoaiMon(String maLoaiMon) {
        return findByHql("FROM Mon WHERE loaiMon.maLoaiMon = :lm", "lm", maLoaiMon);
    }

    @Override
    public List<Mon> findByTenMon(String keyword) {
        return findByHql("FROM Mon WHERE tenMon LIKE :kw", "kw", "%" + keyword + "%");
    }
}
