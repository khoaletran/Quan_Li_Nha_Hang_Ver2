package network.handler;

import core.dto.MonDTO;
import core.service.MonService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh MON_INSERT.
 * Payload: "dto" → MonDTO
 */
public class MonInsertHandler implements CommandHandler {

    private final MonService monService;

    public MonInsertHandler() {
        this.monService = new MonService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof MonDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu MonDTO.");
        }
        try {
            monService.save(dto);
            return Response.success("Thêm món thành công.");
        } catch (IllegalArgumentException e) {
            return Response.error("Validation lỗi: " + e.getMessage());
        } catch (Exception e) {
            return Response.error("Lỗi server khi thêm món: " + e.getMessage());
        }
    }
}
