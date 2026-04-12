package core.service;

import core.dto.KhuyenMaiDTO;
import core.entity.KhuyenMai;
import core.repository.KhuyenMaiRepository;
import infrastructure.mapper.GenericMapper;
import infrastructure.persistence.impl.KhuyenMaiRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiService {

    private final KhuyenMaiRepository kmRepo;

    public KhuyenMaiService() {
        this.kmRepo = new KhuyenMaiRepositoryImpl();
    }

    public List<KhuyenMaiDTO> getAll() {
        return kmRepo.findAll().stream().map(this::toDto).toList();
    }

    public Optional<KhuyenMaiDTO> getById(String maKM) {
        return kmRepo.findById(maKM).map(this::toDto);
    }

    /**
     * Validate voucher: soLuong > 0, not expired, not before phát hành.
     * Logic migrated from CheckoutController.isKmConHieuLuc().
     */
    public boolean isKmConHieuLuc(String maKM) {
        if (maKM == null || maKM.isBlank()) return false;
        return kmRepo.findById(maKM).map(km -> {
            if (km.getSoLuong() <= 0) return false;
            LocalDate today = LocalDate.now();
            if (km.getNgayPhatHanh() != null && today.isBefore(km.getNgayPhatHanh())) return false;
            if (km.getNgayKetThuc()  != null && today.isAfter(km.getNgayKetThuc()))   return false;
            return true;
        }).orElse(false);
    }

    /**
     * Atomically decrement soLuong (ensure only 1 slot is consumed).
     */
    public boolean decrementSoLuong(String maKM) {
        return kmRepo.decrementSoLuong(maKM);
    }

    public boolean incrementSoLuong(String maKM) {
        return kmRepo.incrementSoLuong(maKM);
    }

    public void save(KhuyenMaiDTO dto) {
        validateNew(dto);
        kmRepo.save(GenericMapper.map(dto, KhuyenMai.class));
    }

    public void update(KhuyenMaiDTO dto) {
        kmRepo.update(GenericMapper.map(dto, KhuyenMai.class));
    }

    public void delete(String maKM) {
        kmRepo.delete(maKM);
    }

    // ── Validation (new inserts only) ─────────────────────────────────────

    private void validateNew(KhuyenMaiDTO dto) {
        if (dto.getMaKM() == null || !dto.getMaKM().matches("^KM\\d{4}$"))
            throw new IllegalArgumentException("Mã khuyến mãi sai định dạng.");
        if (dto.getNgayPhatHanh() == null)
            throw new IllegalArgumentException("Ngày phát hành không được rỗng.");
        if (dto.getNgayKetThuc() == null || !dto.getNgayKetThuc().isAfter(dto.getNgayPhatHanh()))
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày phát hành.");
        if (dto.getSoLuong() < 0)
            throw new IllegalArgumentException("Số lượng không được âm.");
    }

    // ── Mapper helper ─────────────────────────────────────────────────────

    private KhuyenMaiDTO toDto(KhuyenMai km) {
        return KhuyenMaiDTO.builder()
                .maKM(km.getMaKM())
                .tenKM(km.getTenKM())
                .soLuong(km.getSoLuong())
                .ngayPhatHanh(km.getNgayPhatHanh())
                .ngayKetThuc(km.getNgayKetThuc())
                .maThayThe(km.getMaThayThe())
                .phanTramGiamGia(km.getPhanTramGiamGia())
                .uuDai(km.isUuDai())
                .build();
    }
}
