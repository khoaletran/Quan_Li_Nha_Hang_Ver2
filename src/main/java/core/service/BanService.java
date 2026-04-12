package core.service;

import core.dto.BanDTO;
import core.entity.Ban;
import core.repository.BanRepository;
import infrastructure.persistence.impl.BanRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class BanService {

    private final BanRepository banRepo;

    public BanService() {
        this.banRepo = new BanRepositoryImpl();
    }

    public List<BanDTO> getAll() {
        return banRepo.findAll().stream().map(this::toDto).toList();
    }

    public Optional<BanDTO> getById(String maBan) {
        return banRepo.findById(maBan).map(this::toDto);
    }

    public List<BanDTO> getByKhuVuc(String maKhuVuc) {
        return banRepo.findByKhuVuc(maKhuVuc).stream().map(this::toDto).toList();
    }

    public List<BanDTO> getByTrangThai(boolean trangThai) {
        return banRepo.findByTrangThai(trangThai).stream().map(this::toDto).toList();
    }

    /** Open or close a table (trangThai = false = available). */
    public void updateTrangThai(String maBan, boolean trangThai) {
        banRepo.updateTrangThai(maBan, trangThai);
    }

    public void save(BanDTO dto) {
        if (dto.getMaBan() == null || !dto.getMaBan().matches("^[BW][OIV]\\d{4}$"))
            throw new IllegalArgumentException("Mã bàn không hợp lệ.");
        banRepo.save(toEntity(dto));
    }

    public void update(BanDTO dto) {
        banRepo.update(toEntity(dto));
    }

    public void delete(String maBan) {
        banRepo.delete(maBan);
    }

    // ── Mappers ───────────────────────────────────────────────────────────

    private BanDTO toDto(Ban b) {
        BanDTO dto = new BanDTO();
        dto.setMaBan(b.getMaBan());
        dto.setTrangThai(b.isTrangThai());
        if (b.getLoaiBan() != null) {
            dto.setMaLoaiBan(b.getLoaiBan().getMaLoaiBan());
            dto.setTenLoaiBan(b.getLoaiBan().getTenLoaiBan());
            dto.setSoLuongGhe(b.getLoaiBan().getSoLuong());
        }
        if (b.getKhuVuc() != null) {
            dto.setMaKhuVuc(b.getKhuVuc().getMaKhuVuc());
            dto.setTenKhuVuc(b.getKhuVuc().getTenKhuVuc());
        }
        return dto;
    }

    private Ban toEntity(BanDTO dto) {
        Ban b = new Ban();
        b.setMaBan(dto.getMaBan());
        b.setTrangThai(dto.isTrangThai());
        return b;
    }
}
