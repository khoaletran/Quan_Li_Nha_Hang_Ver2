package core.service;

import core.dto.KhachHangDTO;
import core.entity.HangKhachHang;
import core.entity.KhachHang;
import core.repository.KhachHangRepository;
import infrastructure.persistence.impl.KhachHangRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class KhachHangService {

    private final KhachHangRepository khachHangRepo;

    public KhachHangService() {
        this.khachHangRepo = new KhachHangRepositoryImpl();
    }

    public List<KhachHangDTO> getAll() {
        return khachHangRepo.findAll().stream().map(this::toDto).toList();
    }

    public Optional<KhachHangDTO> getById(String maKH) {
        return khachHangRepo.findById(maKH).map(this::toDto);
    }

    public Optional<KhachHangDTO> getBySdt(String sdt) {
        return khachHangRepo.findBySdt(sdt).map(this::toDto);
    }

    public void congDiemTichLuy(String maKH, double tongTienTruoc) {
        if (maKH == null || maKH.isBlank()) return;
        khachHangRepo.findById(maKH).ifPresent(kh -> {
            int diem = (int) (tongTienTruoc * 0.01 / 100);
            kh.setDiemTichLuy(kh.getDiemTichLuy() + diem);
            khachHangRepo.update(kh);
        });
    }

    public void save(KhachHangDTO dto) {
        if (dto.getMaKH() == null || !dto.getMaKH().matches("^KH\\d{4}$"))
            throw new IllegalArgumentException("Mã khách hàng sai định dạng.");
        if (dto.getTenKH() == null || dto.getTenKH().isBlank())
            throw new IllegalArgumentException("Tên khách hàng không được trống.");
        if (dto.getSdt() == null || !dto.getSdt().matches("^0[3-9]\\d{8}$"))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ.");

        KhachHang entity = toEntity(dto);
        khachHangRepo.save(entity);
    }

    public void update(KhachHangDTO dto) {
        KhachHang entity = toEntity(dto);
        khachHangRepo.update(entity);
    }

    public void delete(String maKH) {
        khachHangRepo.delete(maKH);
    }

    // ── Mapper helpers ─────────────────────────────────────────────────────

    private KhachHangDTO toDto(KhachHang kh) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKH(kh.getMaKH());
        dto.setTenKH(kh.getTenKH());
        dto.setSdt(kh.getSdt());
        dto.setGioiTinh(kh.isGioiTinh());
        dto.setDiemTichLuy(kh.getDiemTichLuy());
        HangKhachHang hkh = kh.getHangKhachHang();
        if (hkh != null) {
            dto.setMaHang(hkh.getMaHang());
            dto.setGiamGiaHang(hkh.getGiamGia());
            dto.setDiemHang(hkh.getDiemHang());
        }
        return dto;
    }

    private KhachHang toEntity(KhachHangDTO dto) {
        KhachHang kh = new KhachHang();
        kh.setMaKH(dto.getMaKH());
        kh.setTenKH(dto.getTenKH());
        kh.setSdt(dto.getSdt());
        kh.setGioiTinh(dto.isGioiTinh());
        kh.setDiemTichLuy(dto.getDiemTichLuy());
        if (dto.getMaHang() != null) {
            HangKhachHang hkh = new HangKhachHang();
            hkh.setMaHang(dto.getMaHang());
            kh.setHangKhachHang(hkh);
        }
        return kh;
    }
}
