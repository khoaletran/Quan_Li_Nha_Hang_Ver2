package network.handler;

import core.service.MonService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh MON_DELETE.
 * Payload: "maMon" → String
 */
public class MonDeleteHandler implements CommandHandler {

    private final MonService monService;

    public MonDeleteHandler() {
        this.monService = new MonService();
    }

    @Override
    public Response handle(Request request) {
        String maMon = request.getStringParam("maMon");
        if (maMon == null || maMon.isBlank()) {
            return Response.error("Thiếu tham số maMon.");
        }
        try {
            monService.delete(maMon);
            return Response.success("Xóa món thành công.");
        } catch (Exception e) {
            return Response.error("Lỗi server khi xóa món: " + e.getMessage());
        }
    }
}
