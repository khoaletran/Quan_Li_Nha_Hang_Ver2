USE qlnh_ver2;

-- ================================
-- TẮT FK ĐỂ TRUNCATE
-- ================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE chitiethoadon;
TRUNCATE TABLE hoadon;
TRUNCATE TABLE ban;
TRUNCATE TABLE mon;
TRUNCATE TABLE coc;
TRUNCATE TABLE thoigiandoiban;
TRUNCATE TABLE sukien;
TRUNCATE TABLE phantramgiaban;
TRUNCATE TABLE khuyenmai;
TRUNCATE TABLE loaimon;
TRUNCATE TABLE loaiban;
TRUNCATE TABLE khuvuc;
TRUNCATE TABLE nhanvien;
TRUNCATE TABLE khachhang;
TRUNCATE TABLE hangkhachhang;

SET FOREIGN_KEY_CHECKS = 1;

-- =============== HẠNG KHÁCH HÀNG ===============
INSERT INTO hangkhachhang (maHang, diemHang, giamGia, moTa) VALUES
('HH0001', 0, 0, 'Hạng Đồng - Mới tham gia'),
('HH0002', 200, 5, 'Hạng Bạc - Khách thân thiết'),
('HH0003', 500, 10, 'Hạng Vàng - Khách VIP nhỏ'),
('HH0004', 1000, 15, 'Hạng Bạch Kim - Khách VIP lớn'),
('HH0005', 2000, 20, 'Hạng Kim Cương - Khách siêu VIP');

-- =============== KHÁCH HÀNG ===============
INSERT INTO khachhang (maKH, maHang, tenKH, sdt, gioiTinh, diemTichLuy) VALUES
('KH0001', 'HH0001', 'Lê Minh Tuấn', '0912345678', 1, 120),
('KH0002', 'HH0002', 'Nguyễn Thị Hoa', '0987654321', 0, 350),
('KH0003', 'HH0003', 'Trần Quốc Nhã', '0905123456', 1, 700),
('KH0004', 'HH0004', 'Đỗ Minh Quân', '0977333444', 1, 1100),
('KH0005', 'HH0005', 'Trần Lê Khoa', '0933444555', 1, 2100);

-- =============== NHÂN VIÊN ===============
INSERT INTO nhanvien (maNV, tenNV, sdt, gioiTinh, quanLi, ngayVaoLam, trangThai, matKhau) VALUES
('NV0001', 'Nguyễn Hà Nhật Khanh', '0911002233', 0, 1, '2024-12-01', 1, 'Zzz@1111'),
('NV0002', 'Trần Lê Khoa', '0909123123', 1, 1, '2025-01-05', 1, 'Zzz@1111'),
('NV0003', 'Đỗ Minh Quân', '0977333555', 1, 0, '2025-02-20', 1, 'Zzz@1111'),
('NV0004', 'Lê Hồng Nhung', '0967222333', 0, 0, '2025-03-12', 1, 'Zzz@1111'),
('NV0005', 'Phạm Gia Huy', '0915666777', 1, 0, '2025-04-01', 1, 'Zzz@1111');

-- =============== KHU VỰC ===============
INSERT INTO khuvuc (maKhuVuc, tenKhuVuc) VALUES
('KV0001', 'Outdoor'),
('KV0002', 'Indoor'),
('KV0003', 'VIP');

-- =============== LOẠI BÀN ===============
INSERT INTO loaiban (maLoaiBan, tenLoaiBan, soLuong) VALUES
('LB0001', 'Loại A', 2),
('LB0002', 'Loại B', 4),
('LB0003', 'Loại C', 8),
('LB0004', 'Loại D', 12),
('LB0005', 'Loại E', 25);

-- =============== LOẠI MÓN ===============
INSERT INTO loaimon (maLoaiMon, tenLoaiMon, moTa) VALUES
('LM0001', 'Món khai vị', 'Món ăn nhẹ dùng trước bữa chính'),
('LM0002', 'Món chính', 'Món hải sản đặc trưng của nhà hàng'),
('LM0003', 'Món tráng miệng', 'Kem, chè, bánh ngọt...'),
('LM0004', 'Nước uống', 'Nước ngọt, sinh tố, bia rượu...'),
('LM0005', 'Món đặc biệt', 'Món signature - cua hoàng đế, lẩu cua, tôm hùm...');

-- =============== KHUYẾN MÃI ===============
INSERT INTO khuyenmai (maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai)
VALUES
('KM0001', 'Giảm giá khai trương', 100, '2025-01-01', '2025-01-31', 'KHAITRUONG', 20, 1),
('KM0002', 'Sinh nhật khách hàng', 50, '2025-02-01', '2025-02-28', 'SINHNHAT', 15, 1),
('KM0003', 'Giảm giá lễ tình nhân', 80, '2025-02-10', '2025-02-20', 'TINHNHAN', 10, 0),
('KM0004', 'Khuyến mãi cuối tuần', 200, '2025-03-01', '2025-03-31', 'CUOITUAN', 5, 0),
('KM0005', 'Giảm giá hè sôi động', 150, '2025-06-01', '2025-06-30', 'MUAHE', 25, 1);

-- =============== PHẦN TRĂM GIÁ BÁN ===============
INSERT INTO phantramgiaban (maPTGB, maLoaiMon, maMon, phanTramLoi, ngayApDung) VALUES
('PG0001', 'LM0001', NULL, 35, '2025-04-01'),
('PG0002', 'LM0002', NULL, 45, '2025-04-01'),
('PG0003', 'LM0003', NULL, 25, '2025-04-01'),
('PG0004', 'LM0004', NULL, 20, '2025-04-01'),
('PG0005', 'LM0005', NULL, 50, '2025-04-01');

-- =============== SỰ KIỆN ===============
INSERT INTO sukien (maSK, tenSK, moTa, gia) VALUES
('SK0001', 'Sinh nhật', 'Gói trang trí & nhạc sinh nhật', 200000),
('SK0002', 'Tiệc kỷ niệm', 'Không gian riêng tư cho công ty', 300000),
('SK0003', 'Đám cưới', 'Dịch vụ tiệc cưới sang trọng', 500000),
('SK0004', 'Họp mặt bạn bè', 'Không gian ấm cúng, nhẹ nhàng', 150000),
('SK0005', 'Tiệc Giáng Sinh', 'Sự kiện đặc biệt cuối năm', 250000);

-- =============== THỜI GIAN ĐỔI BÀN ===============
INSERT INTO thoigiandoiban (maTGDB, loaiDatBan, thoiGian) VALUES
('TD0001', 1, 15), 
('TD0002', 0, 5);  

-- =============== CỌC ===============
INSERT INTO coc (maCoc, loaiCoc, phanTramCoc, soTienCoc, maLoaiBan, maKhuVuc)
VALUES
-- INDOOR
('CO0001', 0, 0, 200000, 'LB0001', 'KV0002'), 
('CO0002', 0, 0, 300000, 'LB0002', 'KV0002'), 
('CO0003', 1, 20, 0,      'LB0003', 'KV0002'), 
('CO0004', 1, 30, 0,      'LB0004', 'KV0002'), 

-- OUTDOOR
('CO0005', 0, 0, 100000, 'LB0001', 'KV0001'), 
('CO0006', 0, 0, 200000, 'LB0002', 'KV0001'), 
('CO0007', 1, 30, 0,      'LB0003', 'KV0001'), 
('CO0008', 1, 35, 0,      'LB0004', 'KV0001'), 

-- VIP
('CO0009', 1, 38, 0,      'LB0004', 'KV0003'), 
('CO0010', 1, 38, 0,      'LB0005', 'KV0003'); 

-- === Mon =======
INSERT INTO mon (maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) VALUES
('MM0001', 'Gỏi cua lột', 'Món khai vị tươi mát, vị chua ngọt', 'goi_cua_lot.jpg', 85000, 20, 'LM0001'),
('MM0002', 'Chả giò hải sản', 'Cuốn giòn rụm, nhân tôm mực', 'cha_gio_hai_san.jpg', 75000, 25, 'LM0001'),
('MM0003', 'Súp cua trứng bách thảo', 'Súp nóng hổi, thơm mùi cua', 'sup_cua_trung.jpg', 65000, 30, 'LM0001'),
('MM0004', 'Nghêu hấp sả', 'Nghêu tươi hấp sả gừng, thanh vị', 'ngheu_hap_sa.jpg', 70000, 28, 'LM0001'),
('MM0005', 'Hàu nướng mỡ hành', 'Hàu tươi nướng thơm béo', 'hau_nuong_mo_hanh.jpg', 90000, 15, 'LM0001'),
('MM0006', 'Mực chiên giòn', 'Mực tươi tẩm bột chiên vàng', 'muc_chien_gion.jpg', 85000, 22, 'LM0001'),
('MM0007', 'Càng ghẹ rang muối', 'Càng ghẹ rang giòn, vị đậm đà', 'cang_ghe_muoi.jpg', 95000, 18, 'LM0001'),
('MM0008', 'Khoai tây chiên', 'Món ăn nhẹ giòn rụm', 'khoai_tay_chien.jpg', 55000, 35, 'LM0001'),
('MM0009', 'Salad rong biển trứng cua', 'Món salad thanh mát, vị lạ miệng', 'salad_rong_bien.jpg', 78000, 25, 'LM0001'),
('MM0010', 'Bạch tuộc nướng sa tế', 'Món khai vị cay nhẹ, thơm nức', 'bach_tuoc_sate.jpg', 98000, 20, 'LM0001'),
('MM0011', 'Súp hải sản chua cay', 'Vị Thái cay chua hấp dẫn', 'sup_hai_san_thai.jpg', 72000, 18, 'LM0001'),
('MM0012', 'Gỏi ngó sen tôm thịt', 'Món truyền thống, vị giòn và thanh', 'goi_ngo_sen.jpg', 78000, 24, 'LM0001'),
('MM0013', 'Bánh mì nướng bơ tỏi', 'Ăn kèm món chính rất hợp', 'banh_mi_bo_toi.jpg', 45000, 40, 'LM0001'),
('MM0014', 'Chả ốc nướng lá lốt', 'Hương vị đặc trưng Việt Nam', 'cha_oc_la_lot.jpg', 80000, 18, 'LM0001'),
('MM0015', 'Sò điệp nướng phô mai', 'Món nướng béo thơm, cực hấp dẫn', 'so_diep_pho_mai.jpg', 95000, 15, 'LM0001'),
('MM0016', 'Cánh gà chiên mắm', 'Vị mặn ngọt đậm đà', 'canh_ga_chien_mam.jpg', 75000, 20, 'LM0001'),
('MM0017', 'Salad trộn dầu giấm', 'Món nhẹ, chống ngán', 'salad_dau_giam.jpg', 60000, 28, 'LM0001'),
('MM0018', 'Súp bí đỏ kem tôm', 'Súp mịn béo, vị tôm ngọt', 'sup_bi_do_tom.jpg', 70000, 26, 'LM0001'),
('MM0019', 'Gỏi xoài khô cá sặc', 'Vị chua cay mặn ngọt hấp dẫn', 'goi_xoai_ca_sac.jpg', 75000, 22, 'LM0001'),
('MM0020', 'Hàu sống chanh muối', 'Hàu tươi ăn cùng chanh ớt', 'hau_song_anh.jpg', 95000, 10, 'LM0001');

INSERT INTO mon (maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) VALUES
('MM0021', 'Cua hoàng đế hấp bia', 'Món chính sang trọng, thịt ngọt', 'cua_hoang_de.jpg', 1250000, 10, 'LM0002'),
('MM0022', 'Lẩu hải sản Thái Lan', 'Chua cay hấp dẫn', 'lau_hai_san_thai.jpg', 350000, 20, 'LM0002'),
('MM0023', 'Cơm chiên hải sản', 'Món chính no bụng', 'com_chien_hai_san.jpg', 95000, 25, 'LM0002'),
('MM0024', 'Mì xào hải sản', 'Mì dai, tôm mực tươi', 'mi_xao_hai_san.jpg', 85000, 22, 'LM0002'),
('MM0025', 'Ghẹ rang me', 'Vị chua ngọt kích thích vị giác', 'ghe_rang_me.jpg', 99000, 18, 'LM0002'),
('MM0026', 'Tôm sú nướng muối ớt', 'Tôm tươi nướng cay nhẹ', 'tom_su_muoi_ot.jpg', 98000, 20, 'LM0002'),
('MM0027', 'Mực nhồi thịt hấp', 'Món nóng, mềm thơm', 'muc_nhoi_thit.jpg', 110000, 15, 'LM0002'),
('MM0028', 'Cua rang muối HongKong', 'Cua giòn vị mặn nhẹ', 'cua_muoi_hk.jpg', 1150000, 8, 'LM0002'),
('MM0029', 'Cá chẽm sốt chanh dây', 'Sốt chua ngọt đặc biệt', 'ca_chem_chanh_day.jpg', 180000, 12, 'LM0002'),
('MM0030', 'Lẩu riêu cua bắp bò', 'Món lẩu đậm vị Bắc', 'lau_rieu_cua.jpg', 280000, 10, 'LM0002'),
('MM0031', 'Cá hồi nướng bơ tỏi', 'Thịt cá béo mềm, thơm lừng', 'ca_hoi_bo_toi.jpg', 220000, 15, 'LM0002'),
('MM0032', 'Tôm càng xanh hấp bia', 'Tôm to ngọt thịt', 'tom_cang_xanh.jpg', 270000, 12, 'LM0002'),
('MM0033', 'Cơm chiên trứng cua', 'Món cơm vàng thơm', 'com_chien_trung_cua.jpg', 90000, 30, 'LM0002'),
('MM0034', 'Hàu nướng phô mai', 'Béo thơm nồng nàn', 'hau_pho_mai.jpg', 98000, 20, 'LM0002'),
('MM0035', 'Lẩu tôm hùm mini', 'Sang trọng, thơm ngon', 'lau_tom_hum.jpg', 550000, 8, 'LM0002'),
('MM0036', 'Cá mú hấp xì dầu', 'Món Trung Hoa tinh tế', 'ca_mu_xi_dau.jpg', 250000, 10, 'LM0002'),
('MM0037', 'Cá basa kho tộ', 'Món truyền thống Việt Nam', 'ca_basa_kho_to.jpg', 85000, 25, 'LM0002'),
('MM0038', 'Ghẹ hấp bia', 'Tươi ngon, đậm đà', 'ghe_hap_bia.jpg', 120000, 20, 'LM0002'),
('MM0039', 'Tôm chiên bột', 'Món giòn rụm, trẻ em thích', 'tom_chien_bot.jpg', 95000, 18, 'LM0002'),
('MM0040', 'Cá hồi áp chảo', 'Món healthy, ngon nhẹ', 'ca_hoi_ap_chao.jpg', 210000, 14, 'LM0002'),
('MM0041', 'Lẩu cua đồng', 'Món đậm vị miền Tây', 'lau_cua_dong.jpg', 260000, 15, 'LM0002'),
('MM0042', 'Mì Ý sốt cua', 'Mì Ý fusion phong cách Việt', 'mi_y_sot_cua.jpg', 120000, 18, 'LM0002'),
('MM0043', 'Tôm nướng bơ tỏi', 'Món thơm nức, ai cũng mê', 'tom_bo_toi.jpg', 95000, 20, 'LM0002'),
('MM0044', 'Cá thu sốt cà', 'Món dân dã ngon cơm', 'ca_thu_sot_ca.jpg', 85000, 22, 'LM0002'),
('MM0045', 'Cua sốt Singapore', 'Món đặc trưng vị cay nồng', 'cua_singapore.jpg', 1100000, 10, 'LM0002'),
('MM0046', 'Mực nướng ngũ vị', 'Món hải sản nướng độc đáo', 'muc_ngu_vi.jpg', 105000, 15, 'LM0002'),
('MM0047', 'Cơm tôm rim mặn', 'Món cơm đậm đà hương vị', 'com_tom_rim.jpg', 88000, 25, 'LM0002'),
('MM0048', 'Thịt ba rọi nướng riềng', 'Kết hợp hải vị và thịt Việt', 'ba_roi_rieng.jpg', 90000, 20, 'LM0002'),
('MM0049', 'Cua rang tiêu đen', 'Cua tươi rang cay nhẹ', 'cua_rang_tieu.jpg', 1150000, 8, 'LM0002'),
('MM0050', 'Mì xào thập cẩm', 'Món kết hợp đa dạng hải sản', 'mi_xao_thap_cam.jpg', 95000, 22, 'LM0002');

INSERT INTO mon (maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) VALUES
('MM0051', 'Chè hạt sen', 'Món tráng miệng thanh mát', 'che_hat_sen.jpg', 45000, 30, 'LM0003'),
('MM0052', 'Kem vani', 'Kem ngọt nhẹ, mát lạnh', 'kem_vani.jpg', 40000, 25, 'LM0003'),
('MM0053', 'Bánh flan', 'Mềm, béo, thơm caramel', 'banh_flan.jpg', 35000, 35, 'LM0003'),
('MM0054', 'Chè khúc bạch', 'Món tráng miệng nổi tiếng', 'che_khuc_bach.jpg', 48000, 28, 'LM0003'),
('MM0055', 'Sâm bổ lượng', 'Thanh mát, bổ dưỡng', 'sam_bo_luong.jpg', 50000, 20, 'LM0003'),
('MM0056', 'Rau câu dừa', 'Món mát lạnh, ngon miệng', 'rau_cau_dua.jpg', 40000, 30, 'LM0003'),
('MM0057', 'Kem xoài', 'Kem trái cây nhiệt đới', 'kem_xoai.jpg', 42000, 25, 'LM0003'),
('MM0058', 'Chè thái', 'Ngọt mát, nhiều topping', 'che_thai.jpg', 48000, 25, 'LM0003'),
('MM0059', 'Pudding dâu', 'Món mềm béo, vị dâu nhẹ', 'pudding_dau.jpg', 45000, 20, 'LM0003'),
('MM0060', 'Chuối nướng nước cốt dừa', 'Món dân dã đặc sản Nam Bộ', 'chuoi_nuong.jpg', 55000, 20, 'LM0003'),
('MM0061', 'Kem sầu riêng', 'Béo thơm đậm đà', 'kem_sau_rieng.jpg', 48000, 18, 'LM0003'),
('MM0062', 'Chè đậu xanh đánh', 'Món truyền thống thanh nhẹ', 'che_dau_xanh.jpg', 42000, 22, 'LM0003'),
('MM0063', 'Bánh crepe sầu riêng', 'Mềm thơm, vị độc đáo', 'crepe_sau_rieng.jpg', 55000, 15, 'LM0003'),
('MM0064', 'Kem dừa xiêm', 'Món được yêu thích nhất hè', 'kem_dua_xiem.jpg', 48000, 20, 'LM0003'),
('MM0065', 'Chè bưởi', 'Ngọt nhẹ, giòn thơm', 'che_buoi.jpg', 45000, 28, 'LM0003'),
('MM0066', 'Bánh plan trân châu', 'Sự kết hợp mới lạ', 'plan_tran_chau.jpg', 50000, 15, 'LM0003'),
('MM0067', 'Chè trái cây', 'Tươi mát, nhiều vitamin', 'che_trai_cay.jpg', 45000, 30, 'LM0003'),
('MM0068', 'Kem socola', 'Ngọt đắng cân bằng', 'kem_socola.jpg', 45000, 25, 'LM0003'),
('MM0069', 'Rau câu cafe', 'Món tráng miệng hiện đại', 'rau_cau_cafe.jpg', 40000, 22, 'LM0003'),
('MM0070', 'Chè thập cẩm', 'Món Việt quen thuộc', 'che_thap_cam.jpg', 45000, 28, 'LM0003');

INSERT INTO mon (maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) VALUES
('MM0071', 'Nước suối', 'Nước tinh khiết đóng chai', 'nuoc_suoi.jpg', 15000, 60, 'LM0004'),
('MM0072', 'Coca Cola', 'Nước ngọt có gas', 'coca.jpg', 20000, 50, 'LM0004'),
('MM0073', 'Pepsi', 'Nước ngọt phổ biến', 'pepsi.jpg', 20000, 50, 'LM0004'),
('MM0074', 'Sprite', 'Nước chanh có gas', 'sprite.jpg', 20000, 40, 'LM0004'),
('MM0075', 'Trà đào cam sả', 'Món uống hot trend', 'tra_dao_cam_sa.jpg', 45000, 30, 'LM0004'),
('MM0076', 'Sinh tố xoài', 'Thơm ngon, mát lạnh', 'sinh_to_xoai.jpg', 40000, 25, 'LM0004'),
('MM0077', 'Sinh tố bơ', 'Béo ngậy, thơm ngon', 'sinh_to_bo.jpg', 42000, 25, 'LM0004'),
('MM0078', 'Soda chanh', 'Giải khát sảng khoái', 'soda_chanh.jpg', 35000, 35, 'LM0004'),
('MM0079', 'Soda việt quất', 'Ngon, mát, đẹp mắt', 'soda_viet_quat.jpg', 38000, 30, 'LM0004'),
('MM0080', 'Nước cam ép', 'Bổ sung vitamin C', 'nuoc_cam_ep.jpg', 40000, 30, 'LM0004'),
('MM0081', 'Trà tắc mật ong', 'Thanh mát, dễ uống', 'tra_tac_mat_ong.jpg', 35000, 35, 'LM0004'),
('MM0082', 'Sinh tố dâu', 'Món được yêu thích', 'sinh_to_dau.jpg', 42000, 25, 'LM0004'),
('MM0083', 'Nước ép dưa hấu', 'Mát lạnh ngày hè', 'ep_dua_hau.jpg', 40000, 25, 'LM0004'),
('MM0084', 'Nước ép cà rốt', 'Tốt cho sức khỏe, vị ngọt tự nhiên', 'ep_ca_rot.jpg', 40000, 25, 'LM0004'),
('MM0085', 'Nước ép táo', 'Tươi ngon, thanh vị', 'ep_tao.jpg', 40000, 25, 'LM0004'),
('MM0086', 'Nước ép dứa', 'Ngọt dịu, tốt cho tiêu hóa', 'ep_dua.jpg', 38000, 25, 'LM0004'),
('MM0087', 'Cà phê đen đá', 'Cà phê đậm đà truyền thống', 'ca_phe_den.jpg', 30000, 35, 'LM0004'),
('MM0088', 'Cà phê sữa đá', 'Món quen thuộc, béo thơm', 'ca_phe_sua.jpg', 35000, 30, 'LM0004'),
('MM0089', 'Sinh tố dưa gang', 'Mát lạnh, thơm ngọt', 'sinh_to_dua_gang.jpg', 42000, 20, 'LM0004'),
('MM0090', 'Trà sữa trân châu', 'Món uống hot trend giới trẻ', 'tra_sua_tran_chau.jpg', 48000, 30, 'LM0004');

INSERT INTO mon (maMon, tenMon, moTa, hinhAnh, giaGoc, soLuong, loaiMon) VALUES
('MM0091', 'Tôm hùm nướng bơ tỏi', 'Món cao cấp được ưa chuộng', 'tom_hum_bo_toi.jpg', 950000, 10, 'LM0005'),
('MM0092', 'Lẩu cua hoàng đế', 'Món signature đặc biệt của CrabKing', 'lau_cua_hoang_de.jpg', 1500000, 5, 'LM0005'),
('MM0093', 'Cua Alaska hấp bia', 'Món đẳng cấp, thịt ngọt mềm', 'cua_alaska.jpg', 1800000, 4, 'LM0005'),
('MM0094', 'Cua sốt trứng muối', 'Món độc quyền siêu đỉnh', 'cua_trung_muoi.jpg', 1200000, 8, 'LM0005'),
('MM0095', 'Lẩu tôm hùm đặc biệt', 'Món ăn sang trọng, đậm đà', 'lau_tom_hum_dac_biet.jpg', 1300000, 6, 'LM0005'),
('MM0096', 'Cua rang me CrabKing', 'Hương vị độc quyền của nhà hàng', 'cua_rang_me_ck.jpg', 1100000, 10, 'LM0005'),
('MM0097', 'Tôm càng xanh sốt phô mai', 'Món hải sản béo thơm', 'tom_cang_pho_mai.jpg', 550000, 8, 'LM0005'),
('MM0098', 'Súp bào ngư vi cá', 'Món thượng hạng cho thực khách VIP', 'sup_bao_ngu_vi_ca.jpg', 900000, 6, 'LM0005'),
('MM0099', 'Cua King Crab sốt Singapore', 'Món biểu tượng của nhà hàng', 'kingcrab_singapore.jpg', 1400000, 5, 'LM0005'),
('MM0100', 'Tôm hùm hấp rượu vang', 'Món kết hợp hương vị châu Âu tinh tế', 'tom_hum_ruou_vang.jpg', 1600000, 4, 'LM0005');

-- === Sửa đường dẫn ảnh ===
UPDATE mon
SET hinhAnh =
CASE loaiMon
    WHEN 'LM0001' THEN CONCAT('khaivi', (ABS(CRC32(maMon)) % 3) + 1, '.jpg')
    WHEN 'LM0002' THEN CONCAT('chinh', (ABS(CRC32(maMon)) % 3) + 1, '.jpg')
    WHEN 'LM0003' THEN CONCAT('trangmieng', (ABS(CRC32(maMon)) % 3) + 1, '.jpg')
    WHEN 'LM0004' THEN CONCAT('uong', (ABS(CRC32(maMon)) % 3) + 1, '.jpg')
    WHEN 'LM0005' THEN CONCAT('db', (ABS(CRC32(maMon)) % 3) + 1, '.jpg')
END;

-- === Ban ===
INSERT INTO ban (maBan, trangThai, maLoaiBan, maKhuVuc) VALUES
-- Loại A
('BO0001', 0, 'LB0001', 'KV0001'),
('BO0002', 0, 'LB0001', 'KV0001'),
('BO0003', 0, 'LB0001', 'KV0001'),
('BO0004', 0, 'LB0001', 'KV0001'),
('BO0005', 0, 'LB0001', 'KV0001'),
('BO0006', 0, 'LB0001', 'KV0001'),
('BO0007', 0, 'LB0001', 'KV0001'),
('BO0008', 0, 'LB0001', 'KV0001'),
('BO0009', 0, 'LB0001', 'KV0001'),
('BO0010', 0, 'LB0001', 'KV0001'),
('BO0011', 0, 'LB0001', 'KV0001'),
('BO0012', 0, 'LB0001', 'KV0001'),

-- Loại B
('BO0013', 0, 'LB0002', 'KV0001'),
('BO0014', 0, 'LB0002', 'KV0001'),
('BO0015', 0, 'LB0002', 'KV0001'),
('BO0016', 0, 'LB0002', 'KV0001'),
('BO0017', 0, 'LB0002', 'KV0001'),
('BO0018', 0, 'LB0002', 'KV0001'),
('BO0019', 0, 'LB0002', 'KV0001'),
('BO0020', 0, 'LB0002', 'KV0001'),
('BO0021', 0, 'LB0002', 'KV0001'),
('BO0022', 0, 'LB0002', 'KV0001'),
('BO0023', 0, 'LB0002', 'KV0001'),
('BO0024', 0, 'LB0002', 'KV0001'),
('BO0025', 0, 'LB0002', 'KV0001'),
('BO0026', 0, 'LB0002', 'KV0001'),
('BO0027', 0, 'LB0002', 'KV0001'),
('BO0028', 0, 'LB0002', 'KV0001'),

-- Loại C
('BO0029', 0, 'LB0003', 'KV0001'),
('BO0030', 0, 'LB0003', 'KV0001'),
('BO0031', 0, 'LB0003', 'KV0001'),
('BO0032', 0, 'LB0003', 'KV0001'),
('BO0033', 0, 'LB0003', 'KV0001'),
('BO0034', 0, 'LB0003', 'KV0001'),
('BO0035', 0, 'LB0003', 'KV0001'),
('BO0036', 0, 'LB0003', 'KV0001'),
('BO0037', 0, 'LB0003', 'KV0001'),
('BO0038', 0, 'LB0003', 'KV0001'),

-- Loại D
('BO0039', 0, 'LB0004', 'KV0001'),
('BO0040', 0, 'LB0004', 'KV0001'),

-- Loại A (Khu Indoor)
('BI0001', 0, 'LB0001', 'KV0002'),
('BI0002', 0, 'LB0001', 'KV0002'),
('BI0003', 0, 'LB0001', 'KV0002'),
('BI0004', 0, 'LB0001', 'KV0002'),
('BI0005', 0, 'LB0001', 'KV0002'),
('BI0006', 0, 'LB0001', 'KV0002'),
('BI0007', 0, 'LB0001', 'KV0002'),
('BI0008', 0, 'LB0001', 'KV0002'),
('BI0009', 0, 'LB0001', 'KV0002'),
('BI0010', 0, 'LB0001', 'KV0002'),

-- Loại B (Khu Indoor)
('BI0011', 0, 'LB0002', 'KV0002'),
('BI0012', 0, 'LB0002', 'KV0002'),
('BI0013', 0, 'LB0002', 'KV0002'),
('BI0014', 0, 'LB0002', 'KV0002'),
('BI0015', 0, 'LB0002', 'KV0002'),
('BI0016', 0, 'LB0002', 'KV0002'),
('BI0017', 0, 'LB0002', 'KV0002'),
('BI0018', 0, 'LB0002', 'KV0002'),
('BI0019', 0, 'LB0002', 'KV0002'),
('BI0020', 0, 'LB0002', 'KV0002'),
('BI0021', 0, 'LB0002', 'KV0002'),
('BI0022', 0, 'LB0002', 'KV0002'),
('BI0023', 0, 'LB0002', 'KV0002'),
('BI0024', 0, 'LB0002', 'KV0002'),
('BI0025', 0, 'LB0002', 'KV0002'),
('BI0026', 0, 'LB0002', 'KV0002'),
('BI0027', 0, 'LB0002', 'KV0002'),
('BI0028', 0, 'LB0002', 'KV0002'),
('BI0029', 0, 'LB0002', 'KV0002'),
('BI0030', 0, 'LB0002', 'KV0002'),

-- Loại C (Khu Indoor)
('BI0031', 0, 'LB0003', 'KV0002'),
('BI0032', 0, 'LB0003', 'KV0002'),
('BI0033', 0, 'LB0003', 'KV0002'),
('BI0034', 0, 'LB0003', 'KV0002'),
('BI0035', 0, 'LB0003', 'KV0002'),
('BI0036', 0, 'LB0003', 'KV0002'),
('BI0037', 0, 'LB0003', 'KV0002'),
('BI0038', 0, 'LB0003', 'KV0002'),
('BI0039', 0, 'LB0003', 'KV0002'),
('BI0040', 0, 'LB0003', 'KV0002'),
('BI0041', 0, 'LB0003', 'KV0002'),
('BI0042', 0, 'LB0003', 'KV0002'),
('BI0043', 0, 'LB0003', 'KV0002'),
('BI0044', 0, 'LB0003', 'KV0002'),
('BI0045', 0, 'LB0003', 'KV0002'),

-- Loại D (Khu Indoor)
('BI0046', 0, 'LB0004', 'KV0002'),
('BI0047', 0, 'LB0004', 'KV0002'),
('BI0048', 0, 'LB0004', 'KV0002'),
('BI0049', 0, 'LB0004', 'KV0002'),
('BI0050', 0, 'LB0004', 'KV0002'),

-- Khu VIP
('BV0001', 0, 'LB0004', 'KV0003'),
('BV0002', 0, 'LB0004', 'KV0003'),
('BV0003', 0, 'LB0004', 'KV0003'),
('BV0004', 0, 'LB0004', 'KV0003'),
('BV0005', 0, 'LB0004', 'KV0003'),
('BV0006', 0, 'LB0004', 'KV0003'),
('BV0007', 0, 'LB0004', 'KV0003'),
('BV0008', 0, 'LB0005', 'KV0003'),
('BV0009', 0, 'LB0005', 'KV0003'),
('BV0010', 0, 'LB0005', 'KV0003');