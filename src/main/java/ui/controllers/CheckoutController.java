package ui.controllers;

import core.dto.ChiTietHoaDonDTO;
import core.dto.HoaDonDTO;
import core.service.HoaDonService;
import core.service.KhuyenMaiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import ui.AlertCus;
import ui.HoaDonIn;
import ui.QRThanhToan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * CheckoutController — FULLY REFACTORED (reference implementation).
 *
 * Rules enforced:
 *  ✓ No Entity imports
 *  ✓ No Repository imports
 *  ✓ No DAO imports
 *  ✓ Only Service calls
 *  ✓ Only DTOs in controller state
 *  ✓ No business logic
 */
public class CheckoutController {

    // ── FXML bindings ─────────────────────────────────────────────────────
    @FXML private ToggleGroup paymentGroup;
    @FXML private RadioButton rdoChuyenKhoan, rdoTienMat;
    @FXML private TextField txtMaGG, txtTienKhachDua, searchField;
    @FXML private VBox vboxHoaDon, vboxMenu, vboxTienMat;
    @FXML private Button btnSearch, btnCamera,
                         btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6,
                         btnThanhToan;
    @FXML private Label lblmaHD, lbltenKH, lblsdtKH, lblsuKien, lblKhuVuc,
                        lblTongTien, lblGiamGia, lblGiamGia1, lblThue,
                        lblTongTT, lblTienThua, lblCoc, lblConLai;

    // ── Services (NO entity/DAO usage) ────────────────────────────────────
    private final HoaDonService   hoaDonService  = new HoaDonService();
    private final KhuyenMaiService kmService     = new KhuyenMaiService();

    // ── Controller state (DTO only, not Entity) ────────────────────────────
    private HoaDonDTO hdHienTai;
    private List<HoaDonDTO> allHoaDon = new ArrayList<>();

    // ═════════════════════════════════════════════════════════════════════
    //  INIT
    // ═════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        loadAllHoaDon();
        xuLyHienThiTienMat();
        btnThanhToan.setOnAction(e -> xuLyThanhToan());
        txtMaGG.textProperty().addListener((obs, oldV, newV) -> updateThanhTien());
        btnSearch.setOnAction(e -> timKiemHoaDon());
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) timKiemHoaDon();
        });
        Platform.runLater(() -> addShortcuts(searchField.getScene()));
    }

    private void addShortcuts(Scene scene) {
        if (scene == null) return;
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
            () -> { searchField.requestFocus(); searchField.selectAll(); }
        );
        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN),
            this::xuLyThanhToan
        );
    }

    // ═════════════════════════════════════════════════════════════════════
    //  QR CAMERA
    // ═════════════════════════════════════════════════════════════════════

    private boolean dangQuetQR = false;

    @FXML
    private void handleCameraButton() {
        if (dangQuetQR) return;
        dangQuetQR = true;
        new Thread(() -> {
            String maQR = QrCodeController.scanQRCodeWithPreview();
            Platform.runLater(() -> {
                dangQuetQR = false;
                if (maQR != null) {
                    txtMaGG.setText(maQR);
                    updateThanhTien();
                }
            });
        }).start();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  BILLING DISPLAY — delegates ALL calculations to HoaDonService
    // ═════════════════════════════════════════════════════════════════════

    private void updateThanhTien() {
        if (hdHienTai == null || hdHienTai.getMaHD() == null) return;

        // Apply current voucher from textfield
        String code = txtMaGG.getText().trim();
        if (!code.isEmpty() && kmService.isKmConHieuLuc(code)) {
            hdHienTai.setMaKM(code);
        } else {
            hdHienTai.setMaKM(null);
        }

        // Enrich DTO with computed billing (Service does all math)
        hoaDonService.enrichWithBilling(hdHienTai);

        lblTongTien.setText(formatCurrency(hdHienTai.getTongTienTruoc()));
        lblGiamGia .setText(formatCurrency(hdHienTai.getTongTienKhuyenMai()));
        lblGiamGia1.setText("( Voucher: "  + formatCurrency(hdHienTai.getTienMaKM())
                         + " | Hạng: "   + formatCurrency(hdHienTai.getTienHangKM()) + " )");
        lblThue    .setText(formatCurrency(hdHienTai.getThue()));
        lblTongTT  .setText(formatCurrency(hdHienTai.getTongTienSau()));
        lblCoc     .setText(formatCurrency(hdHienTai.getCoc()));
        lblConLai  .setText(formatCurrency(hdHienTai.getTongTienSau()));

        if (rdoTienMat.isSelected()) taoGoiYTienKhach();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  LOAD / DISPLAY INVOICE LIST
    // ═════════════════════════════════════════════════════════════════════

    public void loadAllHoaDon() {
        vboxHoaDon.getChildren().clear();
        allHoaDon = hoaDonService.getAllNgayHomNay();   // Service returns DTO
        renderInvoiceList(allHoaDon);
    }

    private void renderInvoiceList(List<HoaDonDTO> list) {
        vboxHoaDon.getChildren().clear();
        for (HoaDonDTO hd : list) {
            if (hd.getTrangThai() != 1) continue;

            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER);
            hbox.getStyleClass().add("invoice-card");

            ImageView imageView = new ImageView(
                    new Image(getClass().getResourceAsStream("/IMG/ban/IN.png")));
            imageView.setFitWidth(100);
            imageView.setFitHeight(60);

            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            String sdtKH = hd.getSdtKH() != null ? hd.getSdtKH() : "Không có";
            String maBan = hd.getMaBan() != null ? hd.getMaBan() : "?";

            VBox info = new VBox(lblMaHD, new Label("SĐT: " + sdtKH), new Label("Bàn: " + maBan));
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            Button btnTime = new Button("🕒");
            btnTime.getStyleClass().add("time-btn");

            hbox.getChildren().addAll(imageView, info, region, btnTime);
            hbox.setOnMouseClicked(e -> onSelectInvoice(hd));

            vboxHoaDon.getChildren().add(hbox);
        }
    }

    private void onSelectInvoice(HoaDonDTO hd) {
        hdHienTai = hd;
        txtMaGG.clear();

        lblmaHD  .setText(hd.getMaHD());
        lbltenKH .setText(hd.getTenKH() != null ? hd.getTenKH() : "Không rõ");
        lblsdtKH .setText(hd.getSdtKH() != null ? hd.getSdtKH() : "Không có");
        lblsuKien.setText(hd.getTenSK() != null ? hd.getTenSK() : "Không có");
        lblKhuVuc.setText(hd.getTenKhuVuc() != null ? hd.getTenKhuVuc() : "?");

        updateThanhTien();

        // Load chi tiết in background
        new Thread(() -> {
            List<ChiTietHoaDonDTO> chiTiet = hoaDonService.getChiTiet(hd.getMaHD());
            Platform.runLater(() -> renderChiTiet(chiTiet, hd));
        }).start();
    }

    private void renderChiTiet(List<ChiTietHoaDonDTO> chiTiet, HoaDonDTO hd) {
        vboxMenu.getChildren().clear();
        int stt = 1;
        for (ChiTietHoaDonDTO ct : chiTiet) {
            HBox row = new HBox(10);
            row.getStyleClass().add("menu-row");

            Label lblSTT      = new Label(String.valueOf(stt++));       lblSTT.getStyleClass().add("col-stt");
            Label lblName     = new Label(ct.getTenMon());               lblName.getStyleClass().add("col-name");
            Label lblQty      = new Label(String.valueOf(ct.getSoLuong())); lblQty.getStyleClass().add("col-qty");
            Label lblPrice    = new Label(formatCurrency(ct.getGiaBanTaiLucLapHD())); lblPrice.getStyleClass().add("col-price");
            Label lblDiscount = new Label(ct.getPhanTramLoiTaiLucLapHD() + "%"); lblDiscount.getStyleClass().add("col-discount");
            Label lblTotal    = new Label(formatCurrency(ct.getThanhTien())); lblTotal.getStyleClass().add("col-total");

            row.getChildren().addAll(lblSTT, lblName, lblQty, lblPrice, lblDiscount, lblTotal);
            vboxMenu.getChildren().add(row);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  CASH SUGGESTION
    // ═════════════════════════════════════════════════════════════════════

    private void xuLyHienThiTienMat() {
        paymentGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            boolean isTienMat = (newT == rdoTienMat);
            vboxTienMat.setVisible(isTienMat);
            Button[] nut = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
            for (Button b : nut) b.setDisable(!isTienMat);
            if (isTienMat) taoGoiYTienKhach();
        });
    }

    private void taoGoiYTienKhach() {
        double tongTien = parseCurrency(lblConLai.getText());
        if (tongTien <= 0) return;

        double base = Math.round(tongTien / 1000.0) * 1000;
        double[] goiY;

        if (base < 1_000_000) {
            goiY = new double[]{ base,
                Math.ceil(base / 10_000)  * 10_000,
                Math.ceil(base / 50_000)  * 50_000,
                Math.ceil(base / 100_000) * 100_000,
                500_000, 1_000_000 };
        } else if (base < 5_000_000) {
            goiY = new double[]{ base,
                Math.ceil(base / 50_000)  * 50_000,
                Math.ceil(base / 100_000) * 100_000,
                Math.ceil(base / 500_000) * 500_000,
                5_000_000, 10_000_000 };
        } else {
            goiY = new double[]{ base,
                Math.ceil(base / 100_000)   * 100_000,
                Math.ceil(base / 500_000)   * 500_000,
                Math.ceil(base / 1_000_000) * 1_000_000,
                base + 2_000_000, base + 5_000_000 };
        }

        Button[] nut = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
        for (int i = 0; i < nut.length; i++) {
            if (i < goiY.length) {
                double val = goiY[i];
                nut[i].setText(formatCurrency(val));
                nut[i].setVisible(true);
                nut[i].setOnAction(e -> {
                    txtTienKhachDua.setText(formatCurrency(val));
                    tinhTienThua();
                });
            } else {
                nut[i].setVisible(false);
            }
        }
    }

    private void tinhTienThua() {
        double tong     = parseCurrency(lblConLai.getText());
        double tienKhach = parseCurrency(txtTienKhachDua.getText());
        double thua     = tienKhach - tong;
        thua = thua < 1_000 ? 0 : Math.round(thua / 1_000.0) * 1_000;
        lblTienThua.setText(formatCurrency(thua));
    }

    // ═════════════════════════════════════════════════════════════════════
    //  CHECKOUT FLOW — delegates to HoaDonService
    // ═════════════════════════════════════════════════════════════════════

    private void xuLyThanhToan() {
        if (hdHienTai == null) {
            AlertCus.show("Chưa chọn hóa đơn", "Vui lòng chọn hóa đơn trước khi thanh toán!");
            return;
        }

        boolean isTienMat = rdoTienMat.isSelected();
        double tongConLai = parseCurrency(lblConLai.getText().trim());

        if (isTienMat) {
            double tienKhach = parseCurrency(txtTienKhachDua.getText().trim());
            if (tienKhach < tongConLai) {
                AlertCus.show("Thiếu tiền", "Số tiền khách đưa chưa đủ để thanh toán!");
                return;
            }

            doCheckout(isTienMat, tongConLai);

        } else {
            // Bank transfer — show QR then confirm
            QRThanhToan.hienThiQRPanel(tongConLai, hdHienTai.getMaHD(), () ->
                Platform.runLater(() -> doCheckout(false, tongConLai))
            );
        }
    }

    /** Calls HoaDonService.checkout() — NO entity, NO DAO, NO business logic here. */
    private void doCheckout(boolean isTienMat, double tongConLai) {
        try {
            hoaDonService.checkout(
                hdHienTai.getMaHD(),
                hdHienTai.getMaKM(),
                isTienMat
            );

            AlertCus.show("Thanh toán thành công",
                "Hóa đơn " + hdHienTai.getMaHD() + " đã hoàn tất.\n"
                + "Tổng: " + formatCurrency(tongConLai));

            // Print invoice — pass DTO-filled model
            // HoaDonIn.previewHoaDon(hdHienTai);  // TODO: update HoaDonIn to accept DTO

            loadAllHoaDon();
            clearCheckoutInfo();
        } catch (IllegalStateException ex) {
            AlertCus.show("Không thể thanh toán", ex.getMessage());
        } catch (Exception ex) {
            AlertCus.show("Lỗi", "Đã xảy ra lỗi: " + ex.getMessage());
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  SEARCH
    // ═════════════════════════════════════════════════════════════════════

    private void timKiemHoaDon() {
        String kw = searchField.getText().trim().toLowerCase();
        if (kw.isEmpty()) { loadAllHoaDon(); return; }

        List<HoaDonDTO> ketQua = allHoaDon.stream()
            .filter(hd -> {
                String maHD  = hd.getMaHD()  != null ? hd.getMaHD().toLowerCase()  : "";
                String maBan = hd.getMaBan() != null ? hd.getMaBan().toLowerCase() : "";
                String sdt   = hd.getSdtKH() != null ? hd.getSdtKH()              : "";
                return maHD.startsWith(kw) || maBan.startsWith(kw) || sdt.startsWith(kw);
            })
            .collect(Collectors.toList());

        renderInvoiceList(ketQua);
    }

    // ═════════════════════════════════════════════════════════════════════
    //  UTILITIES
    // ═════════════════════════════════════════════════════════════════════

    private void clearCheckoutInfo() {
        hdHienTai = null;
        lblmaHD.setText(""); lbltenKH.setText(""); lblsdtKH.setText("");
        lblsuKien.setText(""); lblKhuVuc.setText("");
        lblTongTien.setText("0 đ"); lblGiamGia.setText("0 đ");
        lblThue.setText("0 đ"); lblTongTT.setText("0 đ");
        lblCoc.setText("0 đ"); lblConLai.setText("0 đ");
        lblTienThua.setText("0 đ");
        txtMaGG.clear(); txtTienKhachDua.clear();
        vboxMenu.getChildren().clear();
    }

    private double parseCurrency(String text) {
        if (text == null || text.isBlank()) return 0;
        String clean = text.replaceAll("[^\\d]", "");
        return clean.isEmpty() ? 0 : Double.parseDouble(clean);
    }

    private String formatCurrency(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        return new DecimalFormat("#,###", symbols).format(amount) + " đ";
    }
}
