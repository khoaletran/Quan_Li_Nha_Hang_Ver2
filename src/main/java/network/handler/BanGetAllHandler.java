package network.handler;

import core.dto.BanDTO;
import core.service.BanService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh BAN_GET_ALL.
 * Trả về List<BanDTO>.
 */
public class BanGetAllHandler implements CommandHandler {

    private final BanService banService;

    public BanGetAllHandler() {
        this.banService = new BanService();
    }

    @Override
    public Response handle(Request request) {
        List<BanDTO> list = banService.getAll();
        return Response.success("Lấy danh sách bàn thành công.", list);
    }
}
