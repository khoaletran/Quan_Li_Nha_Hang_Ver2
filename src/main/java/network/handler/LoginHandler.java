package network.handler;

import core.dto.NhanVienDTO;
import core.service.NhanVienService;
import network.Request;
import network.Response;

import java.util.Optional;

/**
 * Handler cho lệnh LOGIN.
 *
 * <p><b>Request payload:</b>
 * <pre>
 *   "maNV"    → String  (bắt buộc)
 *   "matKhau" → String  (bắt buộc)
 * </pre>
 *
 * <p><b>Response khi thành công:</b>
 * <pre>
 *   status  = SUCCESS
 *   message = "Đăng nhập thành công."
 *   data    = NhanVienDTO (không chứa matKhau)
 * </pre>
 *
 * <p><b>Response khi thất bại:</b>
 * <pre>
 *   status  = UNAUTHORIZED
 *   message = "Sai mã nhân viên hoặc mật khẩu."
 * </pre>
 *
 * <p>Logic nghiệp vụ được delegate hoàn toàn sang {@link NhanVienService#authenticate}.
 */
public class LoginHandler implements CommandHandler {

    private final NhanVienService nhanVienService;

    public LoginHandler() {
        this.nhanVienService = new NhanVienService();
    }

    /** Constructor injection – dùng khi test hoặc DI thủ công. */
    public LoginHandler(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @Override
    public Response handle(Request request) {
        // ── 1. Đọc tham số từ payload ────────────────────────────────────────
        String maNV    = request.getStringParam("maNV");
        String matKhau = request.getStringParam("matKhau");

        // ── 2. Kiểm tra input tối thiểu ──────────────────────────────────────
        if (maNV == null || maNV.isBlank()) {
            return Response.error("Mã nhân viên không được để trống.");
        }
        if (matKhau == null || matKhau.isBlank()) {
            return Response.error("Mật khẩu không được để trống.");
        }

        // ── 3. Uỷ quyền xác thực cho service ────────────────────────────────
        Optional<NhanVienDTO> result = nhanVienService.authenticate(maNV, matKhau);

        // ── 4. Trả về response ───────────────────────────────────────────────
        if (result.isPresent()) {
            return Response.success("Đăng nhập thành công.", result.get());
        } else {
            return Response.unauthorized("Sai mã nhân viên hoặc mật khẩu.");
        }
    }
}
