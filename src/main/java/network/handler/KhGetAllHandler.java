package network.handler;

import core.dto.KhachHangDTO;
import core.service.KhachHangService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh KH_GET_ALL.
 * Trả về List<KhachHangDTO>.
 */
public class KhGetAllHandler implements CommandHandler {

    private final KhachHangService khachHangService;

    public KhGetAllHandler() {
        this.khachHangService = new KhachHangService();
    }

    @Override
    public Response handle(Request request) {
        List<KhachHangDTO> list = khachHangService.getAll();
        return Response.success("Lấy danh sách khách hàng thành công.", list);
    }
}
