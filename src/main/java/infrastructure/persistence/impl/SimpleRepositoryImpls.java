package infrastructure.persistence.impl;

import core.entity.*;
import core.repository.*;
import infrastructure.persistence.AbstractRepository;

// ── Simple pass-through implementations ──────────────────────────────────────

/** Generic implementations for entities needing only default CRUD. */

class HangKhachHangRepositoryImpl extends AbstractRepository<HangKhachHang, String>
        implements HangKhachHangRepository {
    public HangKhachHangRepositoryImpl() { super(HangKhachHang.class); }
}

class LoaiMonRepositoryImpl extends AbstractRepository<LoaiMon, String>
        implements LoaiMonRepository {
    public LoaiMonRepositoryImpl() { super(LoaiMon.class); }
}

class LoaiBanRepositoryImpl extends AbstractRepository<LoaiBan, String>
        implements LoaiBanRepository {
    public LoaiBanRepositoryImpl() { super(LoaiBan.class); }
}

class KhuVucRepositoryImpl extends AbstractRepository<KhuVuc, String>
        implements KhuVucRepository {
    public KhuVucRepositoryImpl() { super(KhuVuc.class); }
}

class SuKienRepositoryImpl extends AbstractRepository<SuKien, String>
        implements SuKienRepository {
    public SuKienRepositoryImpl() { super(SuKien.class); }
}

class ThoiGianDoiBanRepositoryImpl extends AbstractRepository<ThoiGianDoiBan, String>
        implements ThoiGianDoiBanRepository {
    public ThoiGianDoiBanRepositoryImpl() { super(ThoiGianDoiBan.class); }
}
