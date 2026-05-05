package network.handler;

import core.dto.KhachHangDTO;
import core.service.KhachHangService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh KH_UPDATE.
 * Payload: "dto" → KhachHangDTO
 */
public class KhUpdateHandler implements CommandHandler {

    private final KhachHangService khachHangService;

    public KhUpdateHandler() {
        this.khachHangService = new KhachHangService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof KhachHangDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu KhachHangDTO.");
        }
        try {
            khachHangService.update(dto);
            return Response.success("Cập nhật khách hàng thành công.");
        } catch (Exception e) {
            return Response.error("Lỗi server khi cập nhật khách hàng: " + e.getMessage());
        }
    }
}
