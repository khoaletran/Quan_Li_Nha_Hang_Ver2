package network.handler;

import core.dto.KhachHangDTO;
import core.service.KhachHangService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh KH_INSERT.
 * Payload: "dto" → KhachHangDTO
 */
public class KhInsertHandler implements CommandHandler {

    private final KhachHangService khachHangService;

    public KhInsertHandler() {
        this.khachHangService = new KhachHangService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof KhachHangDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu KhachHangDTO.");
        }
        try {
            khachHangService.save(dto);
            return Response.success("Thêm khách hàng thành công.");
        } catch (IllegalArgumentException e) {
            return Response.error("Validation lỗi: " + e.getMessage());
        } catch (Exception e) {
            return Response.error("Lỗi server khi thêm khách hàng: " + e.getMessage());
        }
    }
}
