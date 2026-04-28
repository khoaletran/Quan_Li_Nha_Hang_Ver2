package ui.controllers;

import core.dto.PhieuKetCaDTO;
import core.service.PhieuKetCaService;
import entity.NhanVien;
import entity.PhieuKetCa;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TraCuuKetCaController implements Initializable {

    @FXML private VBox vbox_center_scroll;
    @FXML private TextField txtMaPhieu;
    @FXML private TextField txtTenNV;
    @FXML private TextField txtCaLam;
    @FXML private TextField txtSoDon;
    @FXML private TextField txtTgVaoCa;
    @FXML private TextField txtTgKetCa;
    @FXML private TextField txtTienMat;
    @FXML private TextField txtChuyenKhoan;
    @FXML private TextField txtTongTien;
    @FXML private TextField txtTienChenhLech,txtSDTPK;
    @FXML private TextArea taMoTa;
    @FXML private TextField txtTimKiem;
    @FXML private TextField txtSDT;
    @FXML private DatePicker dpThoiGian;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Button btnTimKiem;
    @FXML private Button btnXoaTrang;


    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat VND_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));



    private final PhieuKetCaService phieuKetCaService = new PhieuKetCaService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFilter();
        loadDanhSachPhieuKetCa();
    }

    private void initFilter() {
        cboTrangThai.getItems().addAll("Ca sáng", "Ca tối");

        btnTimKiem.setOnAction(e -> timKiem());
        btnXoaTrang.setOnAction(e -> xoaTrang());
    }

    private void loadDanhSachPhieuKetCa() {
        vbox_center_scroll.getChildren().clear();

        List<PhieuKetCaDTO> list = phieuKetCaService.getAll();

        for (PhieuKetCaDTO p : list) {
            VBox orderCard = createOrderCard(p);
            vbox_center_scroll.getChildren().add(orderCard);
        }
    }

    private VBox createOrderCard(PhieuKetCaDTO p) {

        VBox card = new VBox(10);
        card.getStyleClass().add("kc-card");

        // ===== Ảnh =====
        ImageView img = new ImageView(
                new Image(getClass().getResource("/IMG/avatar.png").toExternalForm())
        );
        img.setFitWidth(56);
        img.setFitHeight(56);
        img.getStyleClass().add("kc-card-image");

        // ===== Thông tin bên trái =====
        VBox infoBox = new VBox(4);

        String caLam = p.isCa() ? "Tối" : "Sáng";

        Label lblTime = new Label(
                p.getNgayKetCa() != null
                        ? "Thời gian: " + p.getTgLogIn().format(DT_FORMAT)
                        + " - " + p.getNgayKetCa().format(DT_FORMAT)
                        : "Chưa kết ca"
        );
        lblTime.getStyleClass().add("kc-card-time");

        Label lblNhanVien = new Label("Nhân viên: " + p.getTenNV());
        lblNhanVien.getStyleClass().add("kc-card-staff");

        Label lblCaLam = new Label("Ca làm: " + caLam);
        lblCaLam.getStyleClass().add("kc-card-shift");

        infoBox.getChildren().addAll(lblTime, lblNhanVien, lblCaLam);

        Label lblTongTien = new Label(
                String.format("%,.0f đ", p.getTienMat() + p.getTienCK() + p.getTienChenhLech())
        );
        lblTongTien.getStyleClass().add("kc-card-total");

        VBox totalBox = new VBox(lblTongTien);
        totalBox.setAlignment(Pos.CENTER);
        totalBox.setMinWidth(140);

        // ===== Layout ngang =====
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(img, infoBox, totalBox);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        card.getChildren().add(row);
        card.setOnMouseClicked(e -> hienThiPhieuKetCa(p));

        return card;
    }


    private void hienThiPhieuKetCa(PhieuKetCaDTO p) {
        txtMaPhieu.setText(String.valueOf(p.getMaPhieu()));
        txtTenNV.setText(p.getTenNV());
        txtCaLam.setText(p.isCa() ? "Ca tối" : "Ca sáng");
        txtSoDon.setText(String.valueOf(p.getSoHoaDon()));

        txtTgVaoCa.setText(
                p.getTgLogIn() != null ? p.getTgLogIn().format(DT_FORMAT) : ""
        );

        txtTgKetCa.setText(
                p.getNgayKetCa() != null ? p.getNgayKetCa().format(DT_FORMAT) : ""
        );

        txtTienMat.setText(VND_FORMAT.format(p.getTienMat()));
        txtChuyenKhoan.setText(VND_FORMAT.format(p.getTienCK()));

        double tongTien = p.getTienMat() + p.getTienCK() + p.getTienChenhLech();
        txtTongTien.setText(VND_FORMAT.format(tongTien));

        txtTienChenhLech.setText(VND_FORMAT.format(p.getTienChenhLech()));
        // Note: sdt might need to be added to DTO if required, currently using a placeholder or keeping empty
        // txtSDTPK.setText(...); 

        taMoTa.setText(p.getMoTa());
    }

    private void timKiem() {

        String ten = txtTimKiem.getText().trim().toLowerCase();
        String sdt = txtSDT.getText().trim();
        String ca = cboTrangThai.getValue();
        var ngay = dpThoiGian.getValue();

        List<PhieuKetCaDTO> all = phieuKetCaService.getAll();

        List<PhieuKetCaDTO> ketQua = all.stream()
                .filter(p -> {
                    // ===== Họ tên =====
                    if (!ten.isEmpty()) {
                        String tNV = p.getTenNV().toLowerCase();
                        if (!tNV.contains(ten)) return false;
                    }

                    // ===== SĐT (not in DTO, skip or filter manually if needed) =====
                    // ...

                    // ===== Thời gian (ngày kết ca) =====
                    if (ngay != null) {
                        if (p.getNgayKetCa() == null ||
                                !p.getNgayKetCa().toLocalDate().equals(ngay)) {
                            return false;
                        }
                    }

                    // ===== Ca làm =====
                    if (ca != null) {
                        boolean isCaToi = ca.equals("Ca tối");
                        if (p.isCa() != isCaToi) return false;
                    }

                    return true;
                })
                .toList();

        hienThiDanhSach(ketQua);
    }

    private void hienThiDanhSach(List<PhieuKetCaDTO> list) {
        vbox_center_scroll.getChildren().clear();

        for (PhieuKetCaDTO p : list) {
            vbox_center_scroll.getChildren().add(createOrderCard(p));
        }
    }

    private void xoaTrang() {
        txtTimKiem.clear();
        txtSDT.clear();
        dpThoiGian.setValue(null);
        cboTrangThai.setValue(null);

        loadDanhSachPhieuKetCa();
    }



}
