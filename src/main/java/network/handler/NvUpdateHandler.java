package network.handler;

import core.dto.NhanVienDTO;
import core.service.NhanVienService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh NV_UPDATE.
 * Payload: "dto" → NhanVienDTO
 */
public class NvUpdateHandler implements CommandHandler {

    private final NhanVienService nhanVienService;

    public NvUpdateHandler() {
        this.nhanVienService = new NhanVienService();
    }

    @Override
    public Response handle(Request request) {
        Object raw = request.getParam("dto");
        if (!(raw instanceof NhanVienDTO dto)) {
            return Response.error("Payload thiếu hoặc sai kiểu NhanVienDTO.");
        }
        try {
            nhanVienService.update(dto);
            return Response.success("Cập nhật nhân viên thành công.");
        } catch (IllegalArgumentException e) {
            return Response.error("Validation lỗi: " + e.getMessage());
        } catch (Exception e) {
            return Response.error("Lỗi server khi cập nhật nhân viên: " + e.getMessage());
        }
    }
}
