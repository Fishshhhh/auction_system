-- 修复数据库字符集问题
ALTER DATABASE jingpaidb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 修复表字符集
ALTER TABLE asset_category CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE asset CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE asset_image CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE auction_template CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE auction CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE bid CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE orders CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE system_config CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 清空并重新插入资产分类数据
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM asset_category;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO asset_category (id, name, code, description, status, created_time, updated_time) VALUES
(1, '二手设备', 'EQUIPMENT', '各类二手工业设备', 1, NOW(), NOW()),
(2, '办公用品', 'OFFICE', '办公家具及设备', 1, NOW(), NOW()),
(3, '交通工具', 'VEHICLE', '汽车、摩托车等交通工具', 1, NOW(), NOW()),
(4, '电子产品', 'ELECTRONICS', '手机、电脑等电子产品', 1, NOW(), NOW()),
(5, '家居用品', 'HOME', '家具、家电等家居用品', 1, NOW(), NOW());