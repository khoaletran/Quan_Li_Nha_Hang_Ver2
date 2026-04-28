package core.service;

import core.dto.PhieuKetCaDTO;
import core.entity.NhanVien;
import core.entity.PhieuKetCa;
import core.repository.PhieuKetCaRepository;
import infrastructure.persistence.impl.PhieuKetCaRepositoryImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PhieuKetCaService — manages shift-end reports.
 * Refactored from direct DAO usage in controllers.
 */
public class PhieuKetCaService {

    private final PhieuKetCaRepository phieuKetCaRepo;

    public PhieuKetCaService() {
        this.phieuKetCaRepo = new PhieuKetCaRepositoryImpl();
    }

    public List<PhieuKetCaDTO> getAll() {
        return phieuKetCaRepo.findAllWithNhanVien().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public boolean insert(PhieuKetCaDTO dto) {
        try {
            phieuKetCaRepo.save(toEntity(dto));
            return true;
        } catch (Exception e) {
            System.err.println("PhieuKetCaService.insert failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate next maPhieu based on shift and date.
     * Format: MP + [0=morning|1=evening] + DDMMYY + 4-digit seq.
     */
    public String generateMaPhieu(LocalDateTime thoiGianVaoCa) {
        String ca = (thoiGianVaoCa.getHour() < 12) ? "0" : "1";
        String ngay = thoiGianVaoCa.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String prefix = "MP" + ca + ngay;

        return phieuKetCaRepo.findLastMaPhieuByPrefix(prefix).map(last -> {
            String seqStr = last.substring(last.length() - 4);
            int seq = Integer.parseInt(seqStr);
            return prefix + String.format("%04d", seq + 1);
        }).orElse(prefix + "0001");
    }

    private PhieuKetCaDTO toDto(PhieuKetCa p) {
        PhieuKetCaDTO dto = new PhieuKetCaDTO();
        dto.setMaPhieu(p.getMaPhieu());
        dto.setCa(p.isCa());
        dto.setSoHoaDon(p.getSoHoaDon());
        dto.setTienMat(p.getTienMat());
        dto.setTienCK(p.getTienCK());
        dto.setTienChenhLech(p.getTienChenhLech());
        dto.setNgayKetCa(p.getNgayKetCa());
        dto.setTgLogIn(p.getTgLogIn());
        dto.setMoTa(p.getMoTa());
        if (p.getNhanVien() != null) {
            dto.setMaNV(p.getNhanVien().getMaNV());
            dto.setTenNV(p.getNhanVien().getTenNV());
        }
        return dto;
    }

    private PhieuKetCa toEntity(PhieuKetCaDTO dto) {
        PhieuKetCa p = new PhieuKetCa();
        p.setMaPhieu(dto.getMaPhieu());
        p.setCa(dto.isCa());
        p.setSoHoaDon(dto.getSoHoaDon());
        p.setTienMat(dto.getTienMat());
        p.setTienCK(dto.getTienCK());
        p.setTienChenhLech(dto.getTienChenhLech());
        p.setNgayKetCa(dto.getNgayKetCa());
        p.setTgLogIn(dto.getTgLogIn());
        p.setMoTa(dto.getMoTa());
        if (dto.getMaNV() != null) {
            NhanVien nv = new NhanVien();
            nv.setMaNV(dto.getMaNV());
            p.setNhanVien(nv);
        }
        return p;
    }
}
