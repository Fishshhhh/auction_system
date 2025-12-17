-- 初始化测试数据
SET NAMES utf8mb4;

-- 插入资产分类数据
INSERT INTO asset_category (id, name, code, description, status, created_time, updated_time) VALUES
(1, '二手设备', 'EQUIPMENT', '各类二手工业设备', 1, NOW(), NOW()),
(2, '办公用品', 'OFFICE', '办公家具及设备', 1, NOW(), NOW()),
(3, '交通工具', 'VEHICLE', '汽车、摩托车等交通工具', 1, NOW(), NOW()),
(4, '电子产品', 'ELECTRONICS', '手机、电脑等电子产品', 1, NOW(), NOW()),
(5, '家居用品', 'HOME', '家具、家电等家居用品', 1, NOW(), NOW());

-- 插入拍卖模板数据
INSERT INTO auction_template (id, name, auction_type, config_params, description, is_default, status, created_time, updated_time) VALUES
(1, '公开实时竞价模板', 1, '{"startPrice": 10000, "bidIncrement": 500, "duration": 7200}', '适用于一般物品的公开竞价拍卖', 1, 1, NOW(), NOW()),
(2, '暗拍模板', 2, '{"minimumPrice": 5000, "bidLimit": 1}', '密封报价，只允许报价一次', 0, 1, NOW(), NOW()),
(3, '无底价拍卖模板', 3, '{"startPrice": 0, "bidIncrement": 100, "reservePriceProtection": false}', '起拍价为0的拍卖', 0, 1, NOW(), NOW()),
(4, '定向拍卖模板', 4, '{"qualificationRequired": true}', '仅限特定资格用户参与的拍卖', 0, 1, NOW(), NOW()),
(5, '降价拍卖模板', 5, '{"initialPrice": 50000, "priceStep": 2000, "timeStep": 1800, "minPrice": 10000}', '定时降价直到有人接受价格', 0, 1, NOW(), NOW());

-- 插入用户数据
INSERT INTO user (id, username, email, phone, full_name, user_type, status, created_time, updated_time) VALUES
(1, 'admin', 'admin@example.com', '13800138000', '系统管理员', 1, 1, NOW(), NOW()),
(2, 'operator', 'operator@example.com', '13800138001', '运营人员', 2, 1, NOW(), NOW()),
(3, 'seller1', 'seller1@example.com', '13800138002', '卖家张三', 3, 1, NOW(), NOW()),
(4, 'seller2', 'seller2@example.com', '13800138003', '卖家李四', 3, 1, NOW(), NOW()),
(5, 'buyer1', 'buyer1@example.com', '13800138004', '买家王五', 4, 1, NOW(), NOW()),
(6, 'buyer2', 'buyer2@example.com', '13800138005', '买家赵六', 4, 1, NOW(), NOW());

-- 插入资产数据
INSERT INTO asset (id, name, description, asset_no, category_id, starting_price, reserve_price, current_price, owner_id, status, auction_status, auction_type, created_time, updated_time, published_time, auction_start_time, auction_end_time) VALUES
(1, '品牌笔记本电脑', '高性能商务笔记本，九成新', 'ASSET001', 4, 5000.00, 4000.00, 5000.00, 3, 4, 2, 1, NOW(), NOW(), NOW(), DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR)),
(2, '豪华办公桌椅套装', '人体工学设计，高端办公家具', 'ASSET002', 2, 3000.00, 2500.00, 3000.00, 4, 4, 2, 5, NOW(), NOW(), NOW(), DATE_ADD(NOW(), INTERVAL -30 MINUTE), DATE_ADD(NOW(), INTERVAL 30 MINUTE));

-- 插入资产图片数据
INSERT INTO asset_image (id, asset_id, image_url, sort_order, is_cover, created_time) VALUES
(1, 1, 'https://example.com/images/laptop1.jpg', 1, 1, NOW()),
(2, 1, 'https://example.com/images/laptop2.jpg', 2, 0, NOW()),
(3, 2, 'https://example.com/images/desk1.jpg', 1, 1, NOW());

-- 插入拍卖数据
INSERT INTO auction (id, asset_id, template_id, start_price, current_price, reserve_price, start_time, end_time, auction_status, auction_type, created_time, updated_time) VALUES
(1, 1, 1, 5000.00, 5200.00, 4000.00, DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), 2, 1, NOW(), NOW()),
(2, 2, 5, 50000.00, 48000.00, NULL, DATE_ADD(NOW(), INTERVAL -30 MINUTE), DATE_ADD(NOW(), INTERVAL 30 MINUTE), 2, 5, NOW(), NOW());

-- 插入出价数据
INSERT INTO bid (id, auction_id, user_id, bid_price, bid_status, created_time) VALUES
(1, 1, 5, 5100.00, 1, DATE_ADD(NOW(), INTERVAL -45 MINUTE)),
(2, 1, 5, 5200.00, 1, DATE_ADD(NOW(), INTERVAL -30 MINUTE));