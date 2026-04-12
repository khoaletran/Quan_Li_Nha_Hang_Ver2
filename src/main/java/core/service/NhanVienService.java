package core.service;

import core.dto.NhanVienDTO;
import core.entity.NhanVien;
import core.repository.NhanVienRepository;
import infrastructure.persistence.impl.NhanVienRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class NhanVienService {

    private final NhanVienRepository nvRepo;

    public NhanVienService() {
        this.nvRepo = new NhanVienRepositoryImpl();
    }

    public List<NhanVienDTO> getAll() {
        return nvRepo.findAll().stream().map(this::toDto).toList();
    }

    public Optional<NhanVienDTO> getById(String maNV) {
        return nvRepo.findById(maNV).map(this::toDto);
    }

    /**
     * Authenticate employee — returns DTO if credentials match.
     * Business logic migrated from LoginController.
     */
    public Optional<NhanVienDTO> authenticate(String maNV, String matKhau) {
        return nvRepo.findByMatKhau(maNV, matKhau).map(this::toDto);
    }

    public void save(NhanVienDTO dto) {
        validate(dto, true);
        nvRepo.save(toEntity(dto));
    }

    public void update(NhanVienDTO dto) {
        validate(dto, false);
        nvRepo.update(toEntity(dto));
    }

    public void delete(String maNV) {
        nvRepo.delete(maNV);
    }

    private void validate(NhanVienDTO dto, boolean isNew) {
        if (dto.getMaNV() == null || !dto.getMaNV().matches("^NV\\d{4}$"))
            throw new IllegalArgumentException("Mã nhân viên sai định dạng.");
        if (dto.getTenNV() == null || dto.getTenNV().isBlank())
            throw new IllegalArgumentException("Tên nhân viên không được trống.");
        if (dto.getSdt() != null && !dto.getSdt().matches("^0[3-9]\\d{8}$"))
            throw new IllegalArgumentException("SĐT không hợp lệ.");
        if (dto.getNgayVaoLam() != null && dto.getNgayVaoLam().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày vào làm phải ≤ ngày hiện tại.");
        if (isNew && dto.getMatKhau() != null
                && !dto.getMatKhau().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"))
            throw new IllegalArgumentException("Mật khẩu không đủ mạnh.");
    }

    private NhanVienDTO toDto(NhanVien nv) {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNV(nv.getMaNV());
        dto.setTenNV(nv.getTenNV());
        dto.setSdt(nv.getSdt());
        dto.setGioiTinh(nv.isGioiTinh());
        dto.setQuanLi(nv.isQuanLi());
        dto.setNgayVaoLam(nv.getNgayVaoLam());
        dto.setTrangThai(nv.isTrangThai());
        dto.setMatKhau(""); // Never return raw password
        return dto;
    }

    private NhanVien toEntity(NhanVienDTO dto) {
        NhanVien nv = new NhanVien();
        nv.setMaNV(dto.getMaNV());
        nv.setTenNV(dto.getTenNV());
        nv.setSdt(dto.getSdt());
        nv.setGioiTinh(dto.isGioiTinh());
        nv.setQuanLi(dto.isQuanLi());
        nv.setNgayVaoLam(dto.getNgayVaoLam());
        nv.setTrangThai(dto.isTrangThai());
        nv.setMatKhau(dto.getMatKhau());
        return nv;
    }
}
