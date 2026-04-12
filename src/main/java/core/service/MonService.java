package core.service;

import core.dto.MonDTO;
import core.dto.PhanTramGiaBanDTO;
import core.entity.Mon;
import core.entity.PhanTramGiaBan;
import core.repository.MonRepository;
import core.repository.PhanTramGiaBanRepository;
import infrastructure.mapper.GenericMapper;
import infrastructure.persistence.impl.MonRepositoryImpl;
import infrastructure.persistence.impl.PhanTramGiaBanRepositoryImpl;

import java.time.LocalDateTime;
import java.util.*;

/**
 * MonService — all pricing logic from old Mon entity is here.
 * This service is the ONLY place where giaBan, phanTramLoi calculations occur.
 */
public class MonService {

    private final MonRepository monRepo;
    private final PhanTramGiaBanRepository ptgbRepo;

    // ── Thread-safe in-memory cache (cleared when PTGB is updated) ─────────
    private final Map<String, Integer> cachePtgbMon     = new HashMap<>();
    private final Map<String, Integer> cachePtgbLoaiMon = new HashMap<>();

    public MonService() {
        this.monRepo  = new MonRepositoryImpl();
        this.ptgbRepo = new PhanTramGiaBanRepositoryImpl();
    }

    // ── Constructor for testing / injection ───────────────────────────────
    public MonService(MonRepository monRepo, PhanTramGiaBanRepository ptgbRepo) {
        this.monRepo  = monRepo;
        this.ptgbRepo = ptgbRepo;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  QUERY
    // ══════════════════════════════════════════════════════════════════════

    public List<MonDTO> getAll() {
        List<Mon> mons = monRepo.findAll();
        List<MonDTO> result = new ArrayList<>();
        for (Mon m : mons) {
            MonDTO dto = toDto(m);
            int pt = getPhanTramGiaBanHienTai(m);
            dto.setPhanTramLoi(pt);
            dto.setGiaBan(m.getGiaGoc() * (1 + pt / 100.0));
            result.add(dto);
        }
        return result;
    }

    public Optional<MonDTO> getById(String maMon) {
        return monRepo.findById(maMon).map(m -> {
            MonDTO dto = toDto(m);
            int pt = getPhanTramGiaBanHienTai(m);
            dto.setPhanTramLoi(pt);
            dto.setGiaBan(m.getGiaGoc() * (1 + pt / 100.0));
            return dto;
        });
    }

    public List<MonDTO> getByLoaiMon(String maLoaiMon) {
        return monRepo.findByLoaiMon(maLoaiMon).stream()
                .map(m -> {
                    MonDTO dto = toDto(m);
                    int pt = getPhanTramGiaBanHienTai(m);
                    dto.setPhanTramLoi(pt);
                    dto.setGiaBan(m.getGiaGoc() * (1 + pt / 100.0));
                    return dto;
                }).toList();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  BUSINESS LOGIC — migrated from old Mon entity
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Get the currently-effective profit percentage for a Mon.
     * Priority: specific-Mon rule > LoaiMon rule > 0.
     * Results are cached per maMon.
     */
    public int getPhanTramGiaBanHienTai(Mon mon) {
        String maMon = mon.getMaMon();

        if (cachePtgbMon.containsKey(maMon))
            return cachePtgbMon.get(maMon);

        Optional<PhanTramGiaBan> ptMon = ptgbRepo.findLatestForMon(maMon);
        if (ptMon.isPresent()) {
            int pt = ptMon.get().getPhanTramLoi();
            cachePtgbMon.put(maMon, pt);
            return pt;
        }

        String maLoai = mon.getLoaiMon() != null ? mon.getLoaiMon().getMaLoaiMon() : null;
        if (maLoai != null) {
            if (cachePtgbLoaiMon.containsKey(maLoai))
                return cachePtgbLoaiMon.get(maLoai);

            Optional<PhanTramGiaBan> ptLoai = ptgbRepo.findLatestForLoaiMon(maLoai);
            if (ptLoai.isPresent()) {
                int pt = ptLoai.get().getPhanTramLoi();
                cachePtgbLoaiMon.put(maLoai, pt);
                return pt;
            }
        }

        return 0;
    }

    /**
     * Get the profit percentage effective AT the time of a given invoice (tgLapHD).
     */
    public int getPhanTramGiaBanTaiNgayLapHD(Mon mon, LocalDateTime tgLapHD) {
        if (tgLapHD == null) return getPhanTramGiaBanHienTai(mon);

        String maMon  = mon.getMaMon();
        String maLoai = mon.getLoaiMon() != null ? mon.getLoaiMon().getMaLoaiMon() : null;

        Optional<PhanTramGiaBan> ptMon = ptgbRepo.findEffectiveForMonAtDate(maMon, tgLapHD);
        if (ptMon.isPresent()) return ptMon.get().getPhanTramLoi();

        if (maLoai != null) {
            Optional<PhanTramGiaBan> ptLoai = ptgbRepo.findEffectiveForLoaiMonAtDate(maLoai, tgLapHD);
            if (ptLoai.isPresent()) return ptLoai.get().getPhanTramLoi();
        }

        return 0;
    }

    /**
     * Compute giaBan at current time.
     */
    public double getGiaBan(Mon mon) {
        return mon.getGiaGoc() * (1 + getPhanTramGiaBanHienTai(mon) / 100.0);
    }

    /**
     * Compute giaBan at the time the invoice was created.
     */
    public double getGiaBanTaiLucLapHD(Mon mon, LocalDateTime tgLapHD) {
        return mon.getGiaGoc() * (1 + getPhanTramGiaBanTaiNgayLapHD(mon, tgLapHD) / 100.0);
    }

    // ── Cache management ──────────────────────────────────────────────────

    public void clearCacheForMon(String maMon) {
        cachePtgbMon.remove(maMon);
    }

    public void updateCacheForMon(String maMon, int pt) {
        cachePtgbMon.put(maMon, pt);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CRUD
    // ══════════════════════════════════════════════════════════════════════

    public void save(MonDTO dto) {
        // Validation (moved from entity setters)
        if (dto.getMaMon() == null || !dto.getMaMon().matches("^MM\\d{4}$"))
            throw new IllegalArgumentException("Mã món sai định dạng (MM + 4 số).");
        if (dto.getTenMon() == null || dto.getTenMon().isBlank())
            throw new IllegalArgumentException("Tên món không được để trống.");
        if (dto.getGiaGoc() < 0)
            throw new IllegalArgumentException("Giá gốc không được âm.");

        Mon entity = GenericMapper.map(dto, Mon.class);
        monRepo.save(entity);
        clearCacheForMon(dto.getMaMon());
    }

    public void update(MonDTO dto) {
        Mon entity = GenericMapper.map(dto, Mon.class);
        monRepo.update(entity);
        clearCacheForMon(dto.getMaMon());
    }

    public void delete(String maMon) {
        monRepo.delete(maMon);
        clearCacheForMon(maMon);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MAPPER helper
    // ══════════════════════════════════════════════════════════════════════

    private MonDTO toDto(Mon m) {
        MonDTO dto = new MonDTO();
        dto.setMaMon(m.getMaMon());
        dto.setTenMon(m.getTenMon());
        dto.setMoTa(m.getMoTa());
        dto.setHinhAnh(m.getHinhAnh());
        dto.setGiaGoc(m.getGiaGoc());
        dto.setSoLuong(m.getSoLuong());
        if (m.getLoaiMon() != null) {
            dto.setMaLoaiMon(m.getLoaiMon().getMaLoaiMon());
            dto.setTenLoaiMon(m.getLoaiMon().getTenLoaiMon());
        }
        return dto;
    }
}
