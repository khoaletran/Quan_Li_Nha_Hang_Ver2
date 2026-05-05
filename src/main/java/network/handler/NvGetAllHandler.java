package network.handler;

import core.dto.NhanVienDTO;
import core.service.NhanVienService;
import network.Request;
import network.Response;

import java.util.List;

/**
 * Handler cho lệnh NV_GET_ALL.
 * Trả về danh sách tất cả nhân viên dưới dạng List<NhanVienDTO>.
 */
public class NvGetAllHandler implements CommandHandler {

    private final NhanVienService nhanVienService;

    public NvGetAllHandler() {
        this.nhanVienService = new NhanVienService();
    }

    @Override
    public Response handle(Request request) {
        List<NhanVienDTO> list = nhanVienService.getAll();
        return Response.success("Lấy danh sách nhân viên thành công.", list);
    }
}
