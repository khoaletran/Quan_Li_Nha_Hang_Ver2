package network.handler;

import core.service.BanService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh BAN_UPDATE_TRANG_THAI.
 *
 * Payload:
 *   "maBan"     → String  (bắt buộc)
 *   "trangThai" → Boolean (true = đang sử dụng, false = trống)
 */
public class BanUpdateTrangThaiHandler implements CommandHandler {

    private final BanService banService;

    public BanUpdateTrangThaiHandler() {
        this.banService = new BanService();
    }

    @Override
    public Response handle(Request request) {
        String maBan = request.getStringParam("maBan");
        if (maBan == null || maBan.isBlank()) {
            return Response.error("Thiếu tham số maBan.");
        }
        Object trangThaiRaw = request.getParam("trangThai");
        boolean trangThai = Boolean.TRUE.equals(trangThaiRaw);

        try {
            banService.updateTrangThai(maBan, trangThai);
            return Response.success("Cập nhật trạng thái bàn thành công.");
        } catch (Exception e) {
            return Response.error("Lỗi server khi cập nhật trạng thái bàn: " + e.getMessage());
        }
    }
}
