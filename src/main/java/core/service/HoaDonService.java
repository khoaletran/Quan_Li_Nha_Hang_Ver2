package core.service;

import core.dto.ChiTietHoaDonDTO;
import core.dto.HoaDonDTO;
import core.entity.*;
import core.repository.*;
import infrastructure.persistence.impl.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * HoaDonService — the heart of all invoice/billing business logic.
 *
 * Business logic migrated from:
 *  - HoaDon entity (getTongTienTruoc, getThue, getCoc, getTongTienSau, etc.)
 *  - CheckoutController (updateThanhTien, xuLyThanhToan, congDiemTichLuy)
 */
public class HoaDonService {

    private final HoaDonRepository         hoaDonRepo;
    private final ChiTietHoaDonRepository  cthdRepo;
    private final KhuyenMaiService         kmService;
    private final KhachHangService         khService;
    private final BanService               banService;
    private final MonService               monService;
    private final CocRepository            cocRepo;

    public HoaDonService() {
        this.hoaDonRepo = new HoaDonRepositoryImpl();
        this.cthdRepo   = new ChiTietHoaDonRepositoryImpl();
        this.kmService  = new KhuyenMaiService();
        this.khService  = new KhachHangService();
        this.banService = new BanService();
        this.monService = new MonService();
        this.cocRepo    = new CocRepositoryImpl();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  QUERIES
    // ══════════════════════════════════════════════════════════════════════

    public List<HoaDonDTO> getAllNgayHomNay() {
        return hoaDonRepo.findAllNgayHomNay().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<HoaDonDTO> getAll() {
        return hoaDonRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<HoaDonDTO> getById(String maHD) {
        return hoaDonRepo.findById(maHD).map(this::toDto);
    }

    public List<HoaDonDTO> getByMaNV(String maNV) {
        return hoaDonRepo.findByMaNV(maNV).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<HoaDonDTO> getByTrangThai(int trangThai) {
        return hoaDonRepo.findByTrangThai(trangThai).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<HoaDonDTO> getWaitlist() {
        return hoaDonRepo.findWaitlist().stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Get full chi tiết for an invoice as DTO list. */
    public List<ChiTietHoaDonDTO> getChiTiet(String maHD) {
        HoaDon hd = hoaDonRepo.findById(maHD).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy hóa đơn: " + maHD));

        return cthdRepo.findByMaHD(maHD).stream().map(ct -> {
            ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
            dto.setMaHD(maHD);
            Mon m = ct.getMon();
            if (m != null) {
                dto.setMaMon(m.getMaMon());
                dto.setTenMon(m.getTenMon());
                dto.setHinhAnh(m.getHinhAnh());
                int pt = monService.getPhanTramGiaBanTaiNgayLapHD(m, hd.getTgLapHD());
                dto.setPhanTramLoiTaiLucLapHD(pt);
                dto.setGiaBanTaiLucLapHD(m.getGiaGoc() * (1 + pt / 100.0));
            }
            dto.setSoLuong(ct.getSoLuong());
            dto.setThanhTien(ct.getThanhTien());
            return dto;
        }).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  BILLING CALCULATIONS (moved from HoaDon entity)
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Tổng tiền trước giảm giá = Σ(thanhTien) + giaSuKien.
     * Migrated from HoaDon.getTongTienTruoc().
     */
    public double tinhTongTienTruoc(String maHD) {
        HoaDon hd = hoaDonRepo.findById(maHD).orElse(null);
        if (hd == null) return 0;

        double tongTien = cthdRepo.findByMaHD(maHD).stream()
                .mapToDouble(ChiTietHoaDon::getThanhTien)
                .sum();

        // Add event fee
        if (hd.getSuKien() != null) {
            tongTien += hd.getSuKien().getGia();
        }
        return tongTien;
    }

    /**
     * Discount from voucher code.
     * Migrated from HoaDon.getTienMaKM().
     */
    public double tinhTienMaKM(String maHD) {
        HoaDon hd = hoaDonRepo.findById(maHD).orElse(null);
        if (hd == null || hd.getKhuyenMai() == null) return 0;

        KhuyenMai km = hd.getKhuyenMai();
        double tongTruoc = tinhTongTienTruoc(maHD);

        if (!km.isUuDai()) {
            // Percentage discount
            return (km.getPhanTramGiamGia() / 100.0) * tongTruoc;
        } else {
            // Fixed discount amount
            return km.getPhanTramGiamGia();
        }
    }

    /**
     * Discount from loyalty tier.
     * Migrated from HoaDon.getTienHangKM().
     */
    public double tinhTienHangKM(String maHD) {
        HoaDon hd = hoaDonRepo.findById(maHD).orElse(null);
        if (hd == null || hd.getKhachHang() == null) return 0;

        KhachHang kh = hd.getKhachHang();
        if (kh.getHangKhachHang() == null) return 0;

        double tongTruoc = tinhTongTienTruoc(maHD);
        return (kh.getHangKhachHang().getGiamGia() / 100.0) * tongTruoc;
    }

    /**
     * Total discount = voucher discount + tier discount.
     * Migrated from HoaDon.getTongTienKhuyenMai().
     */
    public double tinhTongKhuyenMai(String maHD) {
        return tinhTienMaKM(maHD) + tinhTienHangKM(maHD);
    }

    /**
     * Tax = 10% of tongTienTruoc.
     * Migrated from HoaDon.getThue().
     */
    public double tinhThue(String maHD) {
        return tinhTongTienTruoc(maHD) * 0.10;
    }

    /**
     * Deposit amount.
     * Migrated from HoaDon.getCoc().
     */
    public double tinhCoc(String maHD) {
        HoaDon hd = hoaDonRepo.findById(maHD).orElse(null);
        if (hd == null || hd.getBan() == null || !hd.isKieuDatBan()) return 0;

        Ban ban = hd.getBan();
        if (ban.getKhuVuc() == null || ban.getLoaiBan() == null) return 0;

        String maKV = ban.getKhuVuc().getMaKhuVuc();
        String maLB = ban.getLoaiBan().getMaLoaiBan();

        Optional<Coc> cocOpt = cocRepo.findByKhuVucAndLoaiBan(maKV, maLB);
        if (cocOpt.isEmpty()) return 0;

        Coc coc = cocOpt.get();
        double tongTruoc = tinhTongTienTruoc(maHD);

        if (coc.isLoaiCoc()) {
            return tongTruoc * coc.getPhanTramCoc() / 100.0;
        } else {
            if (tongTruoc >= coc.getSoTienCoc() * 10) {
                return tongTruoc * 0.4;
            }
            return coc.getSoTienCoc();
        }
    }

    /**
     * Final amount after discount, VAT, and deposit.
     * Migrated from HoaDon.getTongTienSau().
     */
    public double tinhTongTienSau(String maHD) {
        HoaDon hd = hoaDonRepo.findById(maHD).orElse(null);
        if (hd == null) return 0;

        double truoc = tinhTongTienTruoc(maHD);
        double km    = tinhTongKhuyenMai(maHD);
        double thue  = tinhThue(maHD);
        double coc   = hd.isKieuDatBan() ? tinhCoc(maHD) : 0;

        return truoc - km + thue - coc;
    }

    /** Populate all computed billing fields into a HoaDonDTO. */
    public HoaDonDTO enrichWithBilling(HoaDonDTO dto) {
        String maHD = dto.getMaHD();
        dto.setTongTienTruoc(tinhTongTienTruoc(maHD));
        dto.setTienMaKM(tinhTienMaKM(maHD));
        dto.setTienHangKM(tinhTienHangKM(maHD));
        dto.setTongTienKhuyenMai(tinhTongKhuyenMai(maHD));
        dto.setThue(tinhThue(maHD));
        dto.setCoc(tinhCoc(maHD));
        dto.setTongTienSau(tinhTongTienSau(maHD));
        return dto;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CRUD + CHECKOUT FLOW
    // ══════════════════════════════════════════════════════════════════════

    public void checkIn(HoaDonDTO dto) {
        validate(dto);
        hoaDonRepo.save(toEntity(dto));
        // Mark table as occupied
        banService.updateTrangThai(dto.getMaBan(), true);
    }

    /**
     * Full checkout flow:
     *  1. Validate & reserve voucher slot
     *  2. Update invoice status → 2 (completed), set tgCheckout
     *  3. Credit loyalty points to customer
     *  4. Release table
     *
     * Migrated from CheckoutController.xuLyThanhToan().
     */
    public void checkout(String maHD, String maKM, boolean kieuThanhToan) {
        HoaDon hd = hoaDonRepo.findById(maHD)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn: " + maHD));

        // 1) Validate + reserve voucher
        if (maKM != null && !maKM.isBlank()) {
            if (!kmService.isKmConHieuLuc(maKM))
                throw new IllegalStateException("Voucher không còn hiệu lực hoặc hết số lượng.");
            boolean reserved = kmService.decrementSoLuong(maKM);
            if (!reserved)
                throw new IllegalStateException("Voucher vừa hết số lượng. Vui lòng kiểm tra lại.");
            // Link voucher to invoice
            KhuyenMai km = new KhuyenMai();
            km.setMaKM(maKM);
            hd.setKhuyenMai(km);
        }

        // 2) Mark invoice done
        hd.setTrangThai(2);
        hd.setTgCheckout(LocalDateTime.now());
        hd.setKieuThanhToan(kieuThanhToan);
        try {
            hoaDonRepo.update(hd);
        } catch (Exception e) {
            // Roll back voucher if update fails
            if (maKM != null && !maKM.isBlank()) kmService.incrementSoLuong(maKM);
            throw e;
        }

        // 3) Credit loyalty points
        if (hd.getKhachHang() != null) {
            double tongTruoc = tinhTongTienTruoc(maHD);
            khService.congDiemTichLuy(hd.getKhachHang().getMaKH(), tongTruoc);
        }

        // 4) Release table
        if (hd.getBan() != null) {
            banService.updateTrangThai(hd.getBan().getMaBan(), false);
        }
    }

    public void update(HoaDonDTO dto) {
        hoaDonRepo.update(toEntity(dto));
    }

    public void delete(String maHD) {
        hoaDonRepo.delete(maHD);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MA HÓA ĐƠN GENERATION
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Generate next maHD.
     * Format: HD + [0=sáng|1=tối] + DDMMYY + 4-digit seq.
     * Migrated from old controller/DAO logic.
     */
    public String generateMaHD(boolean isEveningShift) {
        String ca  = isEveningShift ? "1" : "0";
        String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        String prefix = "HD" + ca + ngay;

        return hoaDonRepo.findLastMaHDByPrefix(prefix).map(last -> {
            int seq = Integer.parseInt(last.substring(last.length() - 4));
            return prefix + String.format("%04d", seq + 1);
        }).orElse(prefix + "0001");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MAPPER helpers
    // ══════════════════════════════════════════════════════════════════════

    private HoaDonDTO toDto(HoaDon hd) {
        HoaDonDTO dto = new HoaDonDTO();
        dto.setMaHD(hd.getMaHD());
        dto.setTgLapHD(hd.getTgLapHD());
        dto.setTgCheckIn(hd.getTgCheckIn());
        dto.setTgCheckout(hd.getTgCheckout());
        dto.setKieuThanhToan(hd.isKieuThanhToan());
        dto.setKieuDatBan(hd.isKieuDatBan());
        dto.setTrangThai(hd.getTrangThai());
        dto.setSoLuong(hd.getSoLuong());
        dto.setMoTa(hd.getMoTa());

        KhachHang kh = hd.getKhachHang();
        if (kh != null) {
            dto.setMaKH(kh.getMaKH());
            dto.setTenKH(kh.getTenKH());
            dto.setSdtKH(kh.getSdt());
            dto.setGioiTinhKH(kh.isGioiTinh());
            dto.setDiemTichLuy(kh.getDiemTichLuy());
            HangKhachHang hkh = kh.getHangKhachHang();
            if (hkh != null) {
                dto.setMaHang(hkh.getMaHang());
                dto.setGiamGiaHang(hkh.getGiamGia());
            }
        }

        NhanVien nv = hd.getNhanVien();
        if (nv != null) {
            dto.setMaNV(nv.getMaNV());
            dto.setTenNV(nv.getTenNV());
        }

        Ban ban = hd.getBan();
        if (ban != null) {
            dto.setMaBan(ban.getMaBan());
            if (ban.getKhuVuc() != null) {
                dto.setMaKhuVuc(ban.getKhuVuc().getMaKhuVuc());
                dto.setTenKhuVuc(ban.getKhuVuc().getTenKhuVuc());
            }
            if (ban.getLoaiBan() != null) {
                dto.setMaLoaiBan(ban.getLoaiBan().getMaLoaiBan());
                dto.setTenLoaiBan(ban.getLoaiBan().getTenLoaiBan());
            }
        }

        KhuyenMai km = hd.getKhuyenMai();
        if (km != null) {
            dto.setMaKM(km.getMaKM());
            dto.setTenKM(km.getTenKM());
            dto.setPhanTramGiamGia(km.getPhanTramGiamGia());
            dto.setUuDai(km.isUuDai());
        }

        SuKien sk = hd.getSuKien();
        if (sk != null) {
            dto.setMaSK(sk.getMaSK());
            dto.setTenSK(sk.getTenSK());
            dto.setGiaSuKien(sk.getGia());
        }

        return dto;
    }

    private HoaDon toEntity(HoaDonDTO dto) {
        HoaDon hd = new HoaDon();
        hd.setMaHD(dto.getMaHD());
        hd.setTgLapHD(dto.getTgLapHD());
        hd.setTgCheckIn(dto.getTgCheckIn());
        hd.setTgCheckout(dto.getTgCheckout());
        hd.setKieuThanhToan(dto.isKieuThanhToan());
        hd.setKieuDatBan(dto.isKieuDatBan());
        hd.setTrangThai(dto.getTrangThai());
        hd.setSoLuong(dto.getSoLuong());
        hd.setMoTa(dto.getMoTa());

        if (dto.getMaKH() != null) {
            KhachHang kh = new KhachHang();
            kh.setMaKH(dto.getMaKH());
            hd.setKhachHang(kh);
        }
        if (dto.getMaNV() != null) {
            NhanVien nv = new NhanVien();
            nv.setMaNV(dto.getMaNV());
            hd.setNhanVien(nv);
        }
        if (dto.getMaBan() != null) {
            Ban ban = new Ban();
            ban.setMaBan(dto.getMaBan());
            hd.setBan(ban);
        }
        if (dto.getMaKM() != null) {
            KhuyenMai km = new KhuyenMai();
            km.setMaKM(dto.getMaKM());
            hd.setKhuyenMai(km);
        }
        if (dto.getMaSK() != null) {
            SuKien sk = new SuKien();
            sk.setMaSK(dto.getMaSK());
            hd.setSuKien(sk);
        }
        return hd;
    }

    private void validate(HoaDonDTO dto) {
        if (dto.getMaHD() == null || !dto.getMaHD().matches("^HD[01]\\d{6}\\d{4}$"))
            throw new IllegalArgumentException("Mã hóa đơn sai định dạng.");
        if (dto.getSoLuong() <= 0)
            throw new IllegalArgumentException("Số lượng khách phải > 0.");
    }
}
