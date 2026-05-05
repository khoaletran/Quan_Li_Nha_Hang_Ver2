package network.handler;

import core.dto.MonDTO;
import core.service.MonService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh MON_GET_ALL.
 * Trả về List<MonDTO> (giá bán đã được tính sẵn bởi MonService).
 */
public class MonGetAllHandler implements CommandHandler {

    private final MonService monService;

    public MonGetAllHandler() {
        this.monService = new MonService();
    }

    @Override
    public Response handle(Request request) {
        List<MonDTO> list = monService.getAll();
        return Response.success("Lấy danh sách món thành công.", list);
    }
}
