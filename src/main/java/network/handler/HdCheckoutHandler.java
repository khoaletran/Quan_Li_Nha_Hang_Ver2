package network.handler;

import core.service.HoaDonService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh HD_CHECKOUT.
 *
 * Payload:
 *   "maHD"          → String  (bắt buộc)
 *   "maKM"          → String  (tuỳ chọn — mã khuyến mãi)
 *   "kieuThanhToan" → Boolean (true = chuyển khoản, false = tiền mặt)
 */
public class HdCheckoutHandler implements CommandHandler {

    private final HoaDonService hoaDonService;

    public HdCheckoutHandler() {
        this.hoaDonService = new HoaDonService();
    }

    @Override
    public Response handle(Request request) {
        String maHD = request.getStringParam("maHD");
        if (maHD == null || maHD.isBlank()) {
            return Response.error("Thiếu tham số maHD.");
        }

        String maKM = request.getStringParam("maKM"); // có thể null
        Object kieuRaw = request.getParam("kieuThanhToan");
        boolean kieuThanhToan = Boolean.TRUE.equals(kieuRaw);

        try {
            hoaDonService.checkout(maHD, maKM, kieuThanhToan);
            return Response.success("Thanh toán hóa đơn " + maHD + " thành công.");
        } catch (IllegalStateException e) {
            return Response.error(e.getMessage());
        } catch (Exception e) {
            return Response.error("Lỗi server khi thanh toán: " + e.getMessage());
        }
    }
}
