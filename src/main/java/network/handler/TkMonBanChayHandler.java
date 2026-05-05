package network.handler;

import core.dto.MonDTO;
import core.service.MonService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh TK_MON_BAN_CHAY.
 * Trả về toàn bộ List<MonDTO> — client/server có thể sort theo soLuong để lấy top-N.
 */
public class TkMonBanChayHandler implements CommandHandler {

    private final MonService monService;

    public TkMonBanChayHandler() {
        this.monService = new MonService();
    }

    @Override
    public Response handle(Request request) {
        List<MonDTO> list = monService.getAll();
        return Response.success("Lấy dữ liệu món bán chạy thành công.", list);
    }
}
