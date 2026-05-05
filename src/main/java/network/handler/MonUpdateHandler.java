package network.handler;

import core.dto.MonDTO;
import core.service.MonService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh MON_UPDATE.
 * Payload: "dto" → MonDTO
 */
public class MonUpdateHandler implements CommandHandler {

    private final MonService monService;

    public MonUpdateHandler() {
        this.monService = new MonService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof MonDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu MonDTO.");
        }
        try {
            monService.update(dto);
            return Response.success("Cập nhật món thành công.");
        } catch (Exception e) {
            return Response.error("Lỗi server khi cập nhật món: " + e.getMessage());
        }
    }
}
