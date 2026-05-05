package network.handler;

import core.dto.HoaDonDTO;
import core.service.HoaDonService;
import network.Request;
import network.Response;

import java.util.Optional;

/**
 * Handler cho lệnh HD_GET_BY_ID.
 * Payload: "maHD" → String
 */
public class HdGetByIdHandler implements CommandHandler {

    private final HoaDonService hoaDonService;

    public HdGetByIdHandler() {
        this.hoaDonService = new HoaDonService();
    }

    @Override
    public Response handle(Request request) {
        String maHD = request.getStringParam("maHD");
        if (maHD == null || maHD.isBlank()) {
            return Response.error("Thiếu tham số maHD.");
        }
        Optional<HoaDonDTO> result = hoaDonService.getById(maHD);
        return result
                .map(dto -> Response.success("Tìm thấy hóa đơn.", dto))
                .orElse(Response.notFound("Không tìm thấy hóa đơn: " + maHD));
    }
}
