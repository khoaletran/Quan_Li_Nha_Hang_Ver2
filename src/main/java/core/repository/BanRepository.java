package core.repository;

import core.entity.Ban;
import java.util.List;

public interface BanRepository extends GenericRepository<Ban, String> {
    List<Ban> findByKhuVuc(String maKhuVuc);
    List<Ban> findByTrangThai(boolean trangThai);
    void updateTrangThai(String maBan, boolean trangThai);
}
