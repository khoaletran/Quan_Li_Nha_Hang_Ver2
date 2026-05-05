package network.handler;

import core.dto.KhuyenMaiDTO;
import core.service.KhuyenMaiService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh KM_GET_ALL.
 * Trả về List<KhuyenMaiDTO>.
 */
public class KmGetAllHandler implements CommandHandler {

    private final KhuyenMaiService khuyenMaiService;

    public KmGetAllHandler() {
        this.khuyenMaiService = new KhuyenMaiService();
    }

    @Override
    public Response handle(Request request) {
        List<KhuyenMaiDTO> list = khuyenMaiService.getAll();
        return Response.success("Lấy danh sách khuyến mãi thành công.", list);
    }
}
