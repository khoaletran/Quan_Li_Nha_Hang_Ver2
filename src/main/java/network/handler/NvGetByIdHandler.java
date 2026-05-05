package network.handler;

import core.dto.NhanVienDTO;
import core.service.NhanVienService;
import network.Request;
import network.Response;

import java.util.Optional;

/**
 * Handler cho lệnh NV_GET_BY_ID.
 * Payload: "maNV" → String
 */
public class NvGetByIdHandler implements CommandHandler {

    private final NhanVienService nhanVienService;

    public NvGetByIdHandler() {
        this.nhanVienService = new NhanVienService();
    }

    @Override
    public Response handle(Request request) {
        String maNV = request.getStringParam("maNV");
        if (maNV == null || maNV.isBlank()) {
            return Response.error("Thiếu tham số maNV.");
        }
        Optional<NhanVienDTO> result = nhanVienService.getById(maNV);
        return result
                .map(dto -> Response.success("Tìm thấy nhân viên.", dto))
                .orElse(Response.notFound("Không tìm thấy nhân viên: " + maNV));
    }
}
