package infrastructure.persistence.impl;

import core.entity.Coc;
import core.repository.CocRepository;
import infrastructure.persistence.AbstractRepository;

import java.util.Optional;

public class CocRepositoryImpl extends AbstractRepository<Coc, String>
        implements CocRepository {

    public CocRepositoryImpl() { super(Coc.class); }

    @Override
    public Optional<Coc> findByKhuVucAndLoaiBan(String maKhuVuc, String maLoaiBan) {
        return findFirstByHql(
            "FROM Coc WHERE khuVuc.maKhuVuc = :kv AND loaiBan.maLoaiBan = :lb",
            "kv", maKhuVuc, "lb", maLoaiBan
        );
    }
}
