package ui.controllers;

import core.dto.NhanVienDTO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * MainController_QL — điều hướng màn hình quản lý.
 * Sử dụng NhanVienDTO thay cho entity.NhanVien.
 *
 * Lưu ý: setCenterContent(String, Mon) và openQLMenuWithMon(Mon) tạm thời
 * giữ lại để không phá vỡ DashboardController vẫn đang dùng entity.Mon.
 * Chúng sẽ được migrate sang MonDTO trong phase sau.
 */
public class MainController_QL {

    @FXML private StackPane mainContent;
    @FXML private SidebarController_QL sidebar_QLController;

    private NhanVienDTO nhanVien;
    @FXML private ui.controllers.TopBarController topBarQlController;

    public SidebarController_QL getsidebar_QLController() {
        return sidebar_QLController;
    }

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) topBarQlController
                    .getRoot().getScene().getWindow();
            topBarQlController.bindStage(stage);
            topBarQlController.setTitle("CrabKing Restaurant");
            topBarQlController.configureActionButton("Đăng Xuất",
                    () -> ui.DangXuat.showDialog(stage));
        });

        if (sidebar_QLController != null) {
            sidebar_QLController.setMainController(this);
        }

        mainContent.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                addKeyboardShortcuts(newScene);
            }
        });
    }

    private void addKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT1 -> sidebar_QLController.selectTab(1);
                case DIGIT2 -> sidebar_QLController.selectTab(2);
                case DIGIT3 -> sidebar_QLController.selectTab(3);
                case DIGIT4 -> sidebar_QLController.selectTab(4);
                case DIGIT5 -> sidebar_QLController.selectTab(5);
                case DIGIT6 -> sidebar_QLController.selectTab(6);
                case DIGIT7 -> sidebar_QLController.selectTab(7);
                case DIGIT8 -> sidebar_QLController.selectTab(8);
            }
        });
    }

    public void setNhanVien(NhanVienDTO nhanVien) {
        this.nhanVien = nhanVien;
        setCenterContent("/FXML/DashBoard.fxml");
    }

    public NhanVienDTO getNhanVien() {
        return nhanVien;
    }

    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController dashboardController) {
                dashboardController.setMainController(this);
                dashboardController.setNhanVien(nhanVien);
            } else if (controller instanceof ThongKeController thongKeController) {
                thongKeController.setMainController(this);
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            mainContent.getChildren().setAll(node);
            fadeIn.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mở màn hình QLMenu với một món được tìm kiếm theo tên.
     * Tạm thời dùng String tenMon để tương thích — không cần entity.Mon.
     */
    public void openQLMenuWithKeyword(String tenMon) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/FXML/QLMenu.fxml")
            );
            Parent root = loader.load();

            QLMenuController controller = loader.getController();
            controller.setMainController(this);

            if (tenMon != null && !tenMon.isBlank()) {
                controller.setSearchKeyword(tenMon);
            }

            sidebar_QLController.selectTab(2);

            mainContent.getChildren().setAll(root);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Giữ lại tương thích với DashboardController đang truyền entity.Mon.
     * Sẽ được xoá sau khi DashboardController được migrate sang MonDTO.
     *
     * @deprecated Dùng openQLMenuWithKeyword(String tenMon) thay thế.
     */
    @Deprecated
    public void openQLMenuWithMon(entity.Mon mon) {
        openQLMenuWithKeyword(mon != null ? mon.getTenMon() : null);
    }

    /**
     * Giữ lại tương thích với các nơi gọi setCenterContent(fxml, Mon).
     *
     * @deprecated Dùng openQLMenuWithKeyword(String) thay thế.
     */
    @Deprecated
    public void setCenterContent(String fxmlPath, entity.Mon mon) {
        sidebar_QLController.selectTab(3);
        openQLMenuWithKeyword(mon != null ? mon.getTenMon() : null);
    }
}
