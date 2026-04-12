package core.repository;

import core.entity.Mon;
import java.util.List;

/** Repository for Mon. */
public interface MonRepository extends GenericRepository<Mon, String> {
    List<Mon> findByLoaiMon(String maLoaiMon);
    List<Mon> findByTenMon(String keyword);
}
