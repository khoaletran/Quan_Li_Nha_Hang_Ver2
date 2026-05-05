package network.handler;

import core.dto.MonDTO;
import core.service.MonService;
import network.Request;
import network.Response;

import java.util.Optional;

/**
 * Handler cho lệnh MON_GET_BY_ID.
 * Payload: "maMon" → String
 */
public class MonGetByIdHandler implements CommandHandler {

    private final MonService monService;

    public MonGetByIdHandler() {
        this.monService = new MonService();
    }

    @Override
    public Response handle(Request request) {
        String maMon = request.getStringParam("maMon");
        if (maMon == null || maMon.isBlank()) {
            return Response.error("Thiếu tham số maMon.");
        }
        Optional<MonDTO> result = monService.getById(maMon);
        return result
                .map(dto -> Response.success("Tìm thấy món.", dto))
                .orElse(Response.notFound("Không tìm thấy món: " + maMon));
    }
}
