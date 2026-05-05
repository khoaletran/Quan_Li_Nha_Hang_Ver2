package network.handler;

import core.service.NhanVienService;
import network.Request;
import network.Response;

/**
 * Handler cho lệnh NV_DELETE.
 * Payload: "maNV" → String
 */
public class NvDeleteHandler implements CommandHandler {

    private final NhanVienService nhanVienService;

    public NvDeleteHandler() {
        this.nhanVienService = new NhanVienService();
    }

    @Override
    public Response handle(Request request) {
        String maNV = request.getStringParam("maNV");
        if (maNV == null || maNV.isBlank()) {
            return Response.error("Thiếu tham số maNV.");
        }
        try {
            nhanVienService.delete(maNV);
            return Response.success("Xóa nhân viên thành công.");
        } catch (Exception e) {
            return Response.error("Lỗi server khi xóa nhân viên: " + e.getMessage());
        }
    }
}
