package network.handler;

import core.dto.KhachHangDTO;
import core.service.KhachHangService;
import network.Request;
import network.Response;

import java.util.Optional;

/**
 * Handler cho lệnh KH_GET_BY_ID.
 * Payload: "maKH" → String (mã KH hoặc SĐT — xem "sdt" key cũng được hỗ trợ)
 *
 * Ưu tiên "maKH"; nếu không có, thử "sdt".
 */
public class KhGetByIdHandler implements CommandHandler {

    private final KhachHangService khachHangService;

    public KhGetByIdHandler() {
        this.khachHangService = new KhachHangService();
    }

    @Override
    public Response handle(Request request) {
        String maKH = request.getStringParam("maKH");
        String sdt   = request.getStringParam("sdt");

        Optional<KhachHangDTO> result;

        if (maKH != null && !maKH.isBlank()) {
            result = khachHangService.getById(maKH);
        } else if (sdt != null && !sdt.isBlank()) {
            result = khachHangService.getBySdt(sdt);
        } else {
            return Response.error("Thiếu tham số maKH hoặc sdt.");
        }

        return result
                .map(dto -> Response.success("Tìm thấy khách hàng.", dto))
                .orElse(Response.notFound("Không tìm thấy khách hàng."));
    }
}
