package network.handler;

import core.dto.KhuyenMaiDTO;
import core.service.KhuyenMaiService;
import network.Request;
import network.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler cho lệnh KM_GET_ACTIVE.
 * Trả về List<KhuyenMaiDTO> chỉ gồm những khuyến mãi còn hiệu lực.
 */
public class KmGetActiveHandler implements CommandHandler {

    private final KhuyenMaiService khuyenMaiService;

    public KmGetActiveHandler() {
        this.khuyenMaiService = new KhuyenMaiService();
    }

    @Override
    public Response handle(Request request) {
        // Lấy toàn bộ rồi lọc những KM còn hiệu lực
        List<KhuyenMaiDTO> active = khuyenMaiService.getAll().stream()
                .filter(dto -> khuyenMaiService.isKmConHieuLuc(dto.getMaKM()))
                .collect(Collectors.toList());
        return Response.success("Lấy danh sách khuyến mãi còn hiệu lực thành công.", active);
    }
}
