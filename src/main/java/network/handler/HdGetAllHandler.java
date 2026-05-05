package network.handler;

import core.dto.HoaDonDTO;
import core.service.HoaDonService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh HD_GET_ALL.
 * Trả về List<HoaDonDTO>.
 */
public class HdGetAllHandler implements CommandHandler {

    private final HoaDonService hoaDonService;

    public HdGetAllHandler() {
        this.hoaDonService = new HoaDonService();
    }

    @Override
    public Response handle(Request request) {
        List<HoaDonDTO> list = hoaDonService.getAll();
        return Response.success("Lấy danh sách hóa đơn thành công.", list);
    }
}
