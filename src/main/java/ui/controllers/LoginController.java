package ui.controllers;

import core.dto.NhanVienDTO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.ClientConnection;
import network.CommandType;
import network.Request;
import network.Response;
import ui.AlertCus;
import ui.AppConstants;

import java.time.LocalDateTime;

/**
 * LoginController — đăng nhập qua socket.
 *
 * Tất cả xác thực được xử lý bởi server (LoginHandler → NhanVienService).
 * Controller này chỉ đọc input, gửi Request, nhận NhanVienDTO, và mở màn hình.
 * KHÔNG có truy cập trực tiếp vào DAO hay Entity.
 */
public class LoginController {

    @FXML private VBox loginPane;
    @FXML private VBox forgotPane;
    @FXML private VBox resetPane;
    @FXML private Button closeBtn;
    @FXML private Button minimizeBtn;
    @FXML private Button loginBtn;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField newPassField;
    @FXML private PasswordField confirmPassField;

    /** DTO của nhân viên đang thực hiện đổi mật khẩu (dùng trong resetDone). */
    private NhanVienDTO nhanVienDTO;

    // ── Getter cho các controller khác cần đọc field ──────────────────────

    public TextField getUsernameField() {
        return usernameField;
    }

    // ── Pane switching ─────────────────────────────────────────────────────

    private void switchPane(VBox hide, VBox show) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), hide);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            hide.setVisible(false);
            hide.setManaged(false);
            show.setVisible(true);
            show.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), show);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // ── Window controls ────────────────────────────────────────────────────

    @FXML
    private void close() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimize() {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    // ── Navigation ─────────────────────────────────────────────────────────

    @FXML
    private void showForgot() { switchPane(loginPane, forgotPane); }

    @FXML
    private void showLogin() {
        if (forgotPane.isVisible()) switchPane(forgotPane, loginPane);
        else switchPane(resetPane, loginPane);
    }

    @FXML
    private void showReset() { switchPane(forgotPane, resetPane); }

    // ── API cho DashboardController (đổi mật khẩu) ────────────────────────

    /**
     * Nhận NhanVienDTO từ màn hình dashboard khi người dùng muốn đổi mật khẩu.
     * Thay thế setNhanVien(NhanVien) đã bị loại bỏ.
     */
    public void setNhanVienDTO(NhanVienDTO dto) {
        this.nhanVienDTO = dto;
        if (dto != null) System.out.println("setNhanVienDTO: " + dto.getMaNV());
    }

    /** Tương thích ngược — gọi từ DashboardController.showChangePassword() */
    public void setNhanVien(NhanVienDTO dto) {
        setNhanVienDTO(dto);
    }

    // ── Hiển thị pane đổi mật khẩu (gọi từ DashboardController) ──────────

    public void showResetPane() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        forgotPane.setVisible(false);
        forgotPane.setManaged(false);
        resetPane.setVisible(true);
        resetPane.setManaged(true);
    }

    // ── LOGIN FLOW (qua socket) ────────────────────────────────────────────

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isBlank()) {
            AlertCus.show("Thông báo", "Vui lòng nhập mã nhân viên.");
            return;
        }
        if (password.isBlank()) {
            AlertCus.show("Thông báo", "Vui lòng nhập mật khẩu.");
            return;
        }

        // ── Bypass cục bộ cho tài khoản ADMIN (không cần server) ──────────
        if (username.equals(AppConstants.ADMIN) && password.equals(AppConstants.ADPASS)) {
            NhanVienDTO adminDto = NhanVienDTO.builder()
                    .maNV("NV0000")
                    .tenNV("ADMIN")
                    .sdt("099999999")
                    .gioiTinh(false)
                    .quanLi(true)
                    .trangThai(true)
                    .matKhau("")
                    .build();
            openMainQL(adminDto);
            return;
        }

        // ── Gửi request đến server ─────────────────────────────────────────
        ClientConnection conn = ClientConnection.getInstance();
        if (!conn.isConnected()) {
            AlertCus.show("Lỗi kết nối", "Không thể kết nối đến máy chủ. Vui lòng kiểm tra server.");
            return;
        }

        Request loginRequest = Request.of(CommandType.LOGIN)
                .param("maNV", username)
                .param("matKhau", password)
                .build();

        Response response = conn.send(loginRequest);

        if (response.isSuccess()) {
            NhanVienDTO dto = (NhanVienDTO) response.getData();
            if (!dto.isTrangThai()) {
                AlertCus.show("Thông báo", "Tài khoản không còn quyền truy cập!");
                return;
            }
            if (dto.isQuanLi()) {
                openMainQL(dto);
            } else {
                openMainNV(dto);
            }
        } else {
            AlertCus.show("Thông báo", response.getMessage() != null
                    ? response.getMessage()
                    : "Sai tên đăng nhập hoặc mật khẩu.");
        }
    }

    // ── Mở màn hình QL ────────────────────────────────────────────────────

    private void openMainQL(NhanVienDTO dto) {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            ui.MainQL mainQL = new ui.MainQL();
            mainQL.setNhanVienDangNhap(dto);
            mainQL.show(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Mở màn hình NV ────────────────────────────────────────────────────

    private void openMainNV(NhanVienDTO dto) {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            ui.MainNV main = new ui.MainNV();
            main.setNhanVienDangNhap(dto);
            main.setThoiGianVaoCa(LocalDateTime.now());
            main.show(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── ĐỔI MẬT KHẨU ──────────────────────────────────────────────────────

    @FXML
    private void resetDone() {
        String newPassword    = newPassField.getText().trim();
        String confirmPassword = confirmPassField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            AlertCus.show("Lỗi", "Vui lòng nhập đầy đủ mật khẩu!");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            AlertCus.show("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }
        if (nhanVienDTO == null || nhanVienDTO.getMaNV() == null) {
            AlertCus.show("Lỗi", "Không tìm thấy thông tin nhân viên!");
            return;
        }

        // Gửi yêu cầu cập nhật mật khẩu qua socket
        NhanVienDTO updateDto = NhanVienDTO.builder()
                .maNV(nhanVienDTO.getMaNV())
                .tenNV(nhanVienDTO.getTenNV())
                .sdt(nhanVienDTO.getSdt())
                .gioiTinh(nhanVienDTO.isGioiTinh())
                .quanLi(nhanVienDTO.isQuanLi())
                .ngayVaoLam(nhanVienDTO.getNgayVaoLam())
                .trangThai(nhanVienDTO.isTrangThai())
                .matKhau(newPassword)
                .build();

        ClientConnection conn = ClientConnection.getInstance();
        Response response = conn.isConnected()
                ? conn.send(Request.of(CommandType.NV_UPDATE).param("dto", updateDto).build())
                : Response.error("Không kết nối được server.");

        if (response != null && response.isSuccess()) {
            AlertCus.show("Thành công", "Đổi mật khẩu thành công!");
            newPassField.clear();
            confirmPassField.clear();
            switchPane(resetPane, loginPane);
        } else {
            AlertCus.show("Lỗi", "Đổi mật khẩu thất bại: "
                    + (response != null ? response.getMessage() : "Lỗi kết nối."));
        }
    }

    // ── Alert helper ───────────────────────────────────────────────────────

    @SuppressWarnings("unused")
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
