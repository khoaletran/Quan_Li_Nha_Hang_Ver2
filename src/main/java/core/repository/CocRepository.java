package core.repository;

import core.entity.Coc;
import java.util.Optional;

public interface CocRepository extends GenericRepository<Coc, String> {
    Optional<Coc> findByKhuVucAndLoaiBan(String maKhuVuc, String maLoaiBan);
}
