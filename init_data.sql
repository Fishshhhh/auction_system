-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空表数据，按照外键依赖顺序（从子表到父表）
TRUNCATE TABLE order_item;
TRUNCATE TABLE orders;
TRUNCATE TABLE bid;
TRUNCATE TABLE auction_asset;
TRUNCATE TABLE auction;
TRUNCATE TABLE asset_image;
TRUNCATE TABLE asset;
TRUNCATE TABLE auction_template;
TRUNCATE TABLE asset_category;
TRUNCATE TABLE system_config;
TRUNCATE TABLE `user`;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入用户数据
INSERT INTO `user` (id, username, password, email, phone, full_name, user_type, status, created_time) VALUES
(1, 'admin', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'admin@example.com', '13800000000', '管理员用户', 1, 1, NOW()),
(2, 'operator', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'operator@example.com', '13800000001', '运营人员', 2, 1, NOW()),
(3, 'seller', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'seller@example.com', '13800000002', '卖家用户', 3, 1, NOW()),
(4, 'buyer', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'buyer@example.com', '13800000003', '买家用户', 4, 1, NOW()),
(5, 'buyer2', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'buyer2@example.com', '13800000004', '买家用户2', 4, 1, NOW());

-- 插入资产分类数据
INSERT INTO asset_category (id, name, code, description, status, created_time) VALUES
(1, '电子产品', 'ELECTRONICS', '各类电子产品设备', 1, NOW()),
(2, '家居用品', 'HOME_APPLIANCES', '家用电器和家具', 1, NOW()),
(3, '交通工具', 'VEHICLES', '汽车、摩托车等交通工具', 1, NOW()),
(4, '收藏品', 'COLLECTIBLES', '古董、艺术品等收藏品', 1, NOW()),
(5, '奢侈品', 'LUXURY', '名牌包、手表等奢侈品', 1, NOW());

-- 插入拍卖模板数据
INSERT INTO auction_template (id, name, auction_type, description, is_default, status, bid_increment, extend_time, extend_threshold, initial_price, price_step, price_drop_interval, min_price, qualification_required, deposit_rate, created_time) VALUES
(1, '标准公开竞价拍卖', 1, '标准的公开实时竞价拍卖模板', 1, 1, 100.00, 5, 300, NULL, NULL, NULL, NULL, 0, 10.00, NOW()),
(2, '暗拍', 2, '密封递价拍卖模板', 0, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 15.00, NOW()),
(3, '无底价拍卖', 3, '无底价拍卖模板', 0, 1, 50.00, 5, 300, NULL, NULL, NULL, NULL, 0, 5.00, NOW()),
(4, '定向拍卖', 4, '仅限特定用户参与的拍卖模板', 0, 1, 200.00, 10, 600, NULL, NULL, NULL, NULL, 1, 20.00, NOW()),
(5, '降价拍卖', 5, '系统自动降价的荷兰式拍卖模板', 0, 1, NULL, NULL, NULL, 10000.00, 500.00, 5, 1000.00, 0, 10.00, NOW());

-- 插入系统配置数据
INSERT INTO system_config (id, config_key, config_value, display_name, description, config_group, status, created_time) VALUES
(1, 'site_name', '在线拍卖系统', '站点名称', '网站显示名称', 'basic', 1, NOW()),
(2, 'site_description', '专业的在线拍卖平台', '站点描述', '网站SEO描述信息', 'basic', 1, NOW()),
(3, 'contact_email', 'support@example.com', '联系邮箱', '客服联系邮箱', 'contact', 1, NOW()),
(4, 'contact_phone', '400-123-4567', '联系电话', '客服联系电话', 'contact', 1, NOW());

-- 插入资产数据
INSERT INTO asset (id, name, description, asset_no, category_id, starting_price, reserve_price, current_price, owner_id, status, auction_status, auction_type, created_time, published_time, auction_start_time, auction_end_time) VALUES
(1, '苹果 MacBook Pro笔记本电脑', '2023款MacBook Pro 14英寸 M2芯片 16GB内存 512GB固态硬盘', 'AST2023120001', 1, 15000.00, 12000.00, 15000.00, 3, 4, 1, 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 25 HOUR)),
(2, '实木餐桌椅套装', '北欧风格实木餐桌椅六件套，包含1张餐桌和5把椅子', 'AST2023120002', 2, 8000.00, NULL, 8000.00, 3, 4, 1, 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(3, '劳力士潜航者腕表', '劳力士潜航者系列男士机械腕表，钢带黑面，2022年购买', 'AST2023120003', 5, 80000.00, 70000.00, 80000.00, 3, 4, 1, 5, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 26 HOUR));

-- 插入资产图片数据
INSERT INTO asset_image (id, asset_id, image_url, sort_order, is_cover, created_time) VALUES
(1, 1, 'https://example.com/images/macbook1.jpg', 1, 1, NOW()),
(2, 1, 'https://example.com/images/macbook2.jpg', 2, 0, NOW()),
(3, 2, 'https://example.com/images/table1.jpg', 1, 1, NOW()),
(4, 3, 'https://example.com/images/rolex1.jpg', 1, 1, NOW());

-- 插入拍卖数据
INSERT INTO auction (id, template_id, start_price, current_price, reserve_price, start_time, end_time, auction_status, auction_type, created_time, updated_time) VALUES
(1, 1, 15000.00, 15000.00, 12000.00, DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), 2, 1, NOW(), NOW()),
(2, 5, 8000.00, 7500.00, NULL, DATE_ADD(NOW(), INTERVAL -30 MINUTE), DATE_ADD(NOW(), INTERVAL 30 MINUTE), 2, 5, NOW(), NOW()),
(3, 1, 80000.00, 80000.00, 70000.00, DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 25 HOUR), 1, 1, NOW(), NOW());

-- 插入拍卖资产关联数据
INSERT INTO auction_asset (id, auction_id, asset_id, quantity, start_price, current_price, reserve_price, created_time) VALUES
(1, 1, 1, 1, 15000.00, 15000.00, 12000.00, NOW()),
(2, 2, 2, 1, 8000.00, 7500.00, NULL, NOW()),
(3, 3, 3, 1, 80000.00, 80000.00, 70000.00, NOW());

-- 插入出价数据
INSERT INTO bid (id, auction_id, user_id, bid_price, quantity, bid_status, created_time) VALUES
(1, 1, 4, 15100.00, 1, 1, DATE_ADD(NOW(), INTERVAL -45 MINUTE)),
(2, 1, 5, 15200.00, 1, 1, DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
(3, 2, 4, 7500.00, 1, 1, DATE_ADD(NOW(), INTERVAL -15 MINUTE));

-- 插入订单数据
INSERT INTO orders (id, order_no, auction_id, buyer_id, seller_id, order_amount, quantity, order_status, created_time, updated_time, payment_time) VALUES
(1, 'ORD2023120001', 1, 5, 3, 15200.00, 1, 2, DATE_ADD(NOW(), INTERVAL -15 MINUTE), DATE_ADD(NOW(), INTERVAL -15 MINUTE), DATE_ADD(NOW(), INTERVAL -10 MINUTE));

-- 插入订单项数据
INSERT INTO order_item (id, order_id, asset_id, quantity, unit_price, subtotal_amount, created_time) VALUES
(1, 1, 1, 1, 15200.00, 15200.00, NOW());