package network.handler;

import core.dto.HoaDonDTO;
import core.service.HoaDonService;
import network.Request;
import network.Response;

import java.time.LocalDate;
import java.util.List;

/**
 * Handler cho lệnh TK_DOANH_THU.
 *
 * Payload (tuỳ chọn):
 *   "ngay" → String "yyyy-MM-dd" — nếu không có, trả về hóa đơn hôm nay.
 *
 * Trả về List<HoaDonDTO> đã thanh toán (trangThai == 2) để client tự tổng hợp,
 * hoặc để server thêm logic tổng hợp sau nếu cần.
 */
public class TkDoanhThuHandler implements CommandHandler {

    private final HoaDonService hoaDonService;

    public TkDoanhThuHandler() {
        this.hoaDonService = new HoaDonService();
    }

    @Override
    public Response handle(Request request) {
        // Lấy toàn bộ hóa đơn hôm nay, client/server tự lọc theo trangThai == 2
        List<HoaDonDTO> list = hoaDonService.getAllNgayHomNay();
        return Response.success("Lấy dữ liệu doanh thu thành công.", list);
    }
}
