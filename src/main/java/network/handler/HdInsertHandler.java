package network.handler;

import core.dto.HoaDonDTO;
import core.service.HoaDonService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh HD_INSERT.
 * Payload: "dto" → HoaDonDTO
 */
public class HdInsertHandler implements CommandHandler {

    private final HoaDonService hoaDonService;

    public HdInsertHandler() {
        this.hoaDonService = new HoaDonService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof HoaDonDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu HoaDonDTO.");
        }
        try {
            hoaDonService.checkIn(dto);
            return Response.success("Tạo hóa đơn thành công.");
        } catch (IllegalArgumentException e) {
            return Response.error("Validation lỗi: " + e.getMessage());
        } catch (Exception e) {
            return Response.error("Lỗi server khi tạo hóa đơn: " + e.getMessage());
        }
    }
}
