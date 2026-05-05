package network.handler;

import core.dto.NhanVienDTO;
import core.service.NhanVienService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh NV_INSERT.
 * Payload: "dto" → NhanVienDTO
 */
public class NvInsertHandler implements CommandHandler {

    private final NhanVienService nhanVienService;

    public NvInsertHandler() {
        this.nhanVienService = new NhanVienService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof NhanVienDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu NhanVienDTO.");
        }
        try {
            nhanVienService.save(dto);
            return Response.success("Thêm nhân viên thành công.");
        } catch (IllegalArgumentException e) {
            return Response.error("Validation lỗi: " + e.getMessage());
        } catch (Exception e) {
            return Response.error("Lỗi server khi thêm nhân viên: " + e.getMessage());
        }
    }
}
