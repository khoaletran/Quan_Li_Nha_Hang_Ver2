package ui;

import core.dto.NhanVienDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.MainController_QL;
import ui.controllers.SidebarController_QL;

import static ui.AppConstants.APP_LOGO;

/**
 * MainQL — khởi tạo cửa sổ chính cho Quản Lý.
 * Sử dụng NhanVienDTO thay cho entity.NhanVien.
 */
public class MainQL {

    private NhanVienDTO nvDangNhap;

    public void setNhanVienDangNhap(NhanVienDTO nv) {
        this.nvDangNhap = nv;
    }

    public void show(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Login.class.getResource("/FXML/MainQuanLi.fxml"));
        BorderPane root = loader.load();

        MainController_QL mainController = loader.getController();
        mainController.setNhanVien(nvDangNhap);

        SidebarController_QL sidebarController = mainController.getsidebar_QLController();
        sidebarController.setThongTinNhanVien(nvDangNhap);

        Scene scene = new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        stage.setTitle(AppConstants.APP_TITLE + " - Quản Lí");
        stage.getIcons().add(APP_LOGO);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
