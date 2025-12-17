-- Properly initialize auction_system database with sample data
-- Disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Clear tables in proper order (child to parent)
DELETE FROM order_item;
DELETE FROM orders;
DELETE FROM bid;
DELETE FROM auction_asset;
DELETE FROM auction;
DELETE FROM asset_image;
DELETE FROM asset;
DELETE FROM auction_template;
DELETE FROM asset_category;
DELETE FROM system_config;
DELETE FROM `user`;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Insert user data
INSERT INTO `user` (id, username, password, email, phone, full_name, user_type, status, created_time) VALUES
(1, 'admin', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'admin@example.com', '13800000000', 'Admin', 1, 1, NOW()),
(2, 'operator', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'operator@example.com', '13800000001', 'Operator', 2, 1, NOW()),
(3, 'seller', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'seller@example.com', '13800000002', 'Seller', 3, 1, NOW()),
(4, 'buyer', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'buyer@example.com', '13800000003', 'Buyer', 4, 1, NOW()),
(5, 'buyer2', '$2a$10$abcdefghijklmnopqrstuv.wxYZ1234567890abcdefghijklmnop', 'buyer2@example.com', '13800000004', 'Buyer2', 4, 1, NOW());

-- Insert asset category data
INSERT INTO asset_category (id, name, code, description, status, created_time) VALUES
(1, 'Electronics', 'ELECTRONICS', 'Various electronic devices', 1, NOW()),
(2, 'Home Appliances', 'HOME_APPLIANCES', 'Home appliances and furniture', 1, NOW()),
(3, 'Vehicles', 'VEHICLES', 'Cars, motorcycles and other vehicles', 1, NOW()),
(4, 'Collectibles', 'COLLECTIBLES', 'Antiques, artworks and other collectibles', 1, NOW()),
(5, 'Luxury', 'LUXURY', 'Luxury bags, watches and other luxury goods', 1, NOW());

-- Insert auction template data
INSERT INTO auction_template (id, name, auction_type, description, is_default, status, bid_increment, extend_time, extend_threshold, initial_price, price_step, price_drop_interval, min_price, qualification_required, deposit_rate, created_time) VALUES
(1, 'Standard Open Auction', 1, 'Standard open real-time bidding auction template', 1, 1, 100.00, 5, 300, NULL, NULL, NULL, NULL, 0, 10.00, NOW()),
(2, 'Sealed Bid Auction', 2, 'Sealed bid auction template', 0, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 15.00, NOW()),
(3, 'No Reserve Auction', 3, 'No reserve auction template', 0, 1, 50.00, 5, 300, NULL, NULL, NULL, NULL, 0, 5.00, NOW()),
(4, 'Targeted Auction', 4, 'Auction template for specific users only', 0, 1, 200.00, 10, 600, NULL, NULL, NULL, NULL, 1, 20.00, NOW()),
(5, 'Dutch Auction', 5, 'System automatically reduced price Dutch auction template', 0, 1, NULL, NULL, NULL, 10000.00, 500.00, 5, 1000.00, 0, 10.00, NOW());

-- Insert system configuration data
INSERT INTO system_config (id, config_key, config_value, display_name, description, config_group, status, created_time) VALUES
(1, 'site_name', 'Online Auction System', 'Site Name', 'Website display name', 'basic', 1, NOW()),
(2, 'site_description', 'Professional Online Auction Platform', 'Site Description', 'Website SEO description information', 'basic', 1, NOW()),
(3, 'contact_email', 'support@example.com', 'Contact Email', 'Customer service contact email', 'contact', 1, NOW()),
(4, 'contact_phone', '400-123-4567', 'Contact Phone', 'Customer service contact phone', 'contact', 1, NOW());

-- Insert asset data
INSERT INTO asset (id, name, description, asset_no, category_id, starting_price, reserve_price, current_price, owner_id, status, auction_status, auction_type, created_time, published_time, auction_start_time, auction_end_time) VALUES
(1, 'Apple MacBook Pro Laptop', '2023 MacBook Pro 14-inch M2 chip 16GB RAM 512GB SSD', 'AST2023120001', 1, 15000.00, 12000.00, 15000.00, 3, 4, 1, 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 25 HOUR)),
(2, 'Solid Wood Dining Table Set', 'Nordic style solid wood dining table set with 1 table and 5 chairs', 'AST2023120002', 2, 8000.00, NULL, 8000.00, 3, 4, 1, 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(3, 'Rolex Submariner Watch', 'Rolex Submariner series mens mechanical watch, steel band black face, purchased in 2022', 'AST2023120003', 5, 80000.00, 70000.00, 80000.00, 3, 4, 1, 5, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 26 HOUR));

-- Insert asset image data
INSERT INTO asset_image (id, asset_id, image_url, sort_order, is_cover, created_time) VALUES
(1, 1, 'https://example.com/images/macbook1.jpg', 1, 1, NOW()),
(2, 1, 'https://example.com/images/macbook2.jpg', 2, 0, NOW()),
(3, 2, 'https://example.com/images/table1.jpg', 1, 1, NOW()),
(4, 3, 'https://example.com/images/rolex1.jpg', 1, 1, NOW());

-- Insert auction data
INSERT INTO auction (id, template_id, start_price, current_price, reserve_price, start_time, end_time, auction_status, auction_type, created_time, updated_time) VALUES
(1, 1, 15000.00, 15000.00, 12000.00, DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), 2, 1, NOW(), NOW()),
(2, 5, 8000.00, 7500.00, NULL, DATE_ADD(NOW(), INTERVAL -30 MINUTE), DATE_ADD(NOW(), INTERVAL 30 MINUTE), 2, 5, NOW(), NOW()),
(3, 1, 80000.00, 80000.00, 70000.00, DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 25 HOUR), 1, 1, NOW(), NOW());

-- Insert auction asset association data
INSERT INTO auction_asset (id, auction_id, asset_id, quantity, start_price, current_price, reserve_price, created_time) VALUES
(1, 1, 1, 1, 15000.00, 15000.00, 12000.00, NOW()),
(2, 2, 2, 1, 8000.00, 7500.00, NULL, NOW()),
(3, 3, 3, 1, 80000.00, 80000.00, 70000.00, NOW());

-- Insert bid data
INSERT INTO bid (id, auction_id, user_id, bid_price, quantity, bid_status, created_time) VALUES
(1, 1, 4, 15100.00, 1, 1, DATE_ADD(NOW(), INTERVAL -45 MINUTE)),
(2, 1, 5, 15200.00, 1, 1, DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
(3, 2, 4, 7500.00, 1, 1, DATE_ADD(NOW(), INTERVAL -15 MINUTE));

-- Insert order data
INSERT INTO orders (id, order_no, auction_id, buyer_id, seller_id, bid_id, order_amount, quantity, order_status, created_time, updated_time, payment_time) VALUES
(1, 'ORD2023120001', 1, 5, 3, 2, 15200.00, 1, 2, DATE_ADD(NOW(), INTERVAL -15 MINUTE), DATE_ADD(NOW(), INTERVAL -15 MINUTE), DATE_ADD(NOW(), INTERVAL -10 MINUTE));

-- Insert order item data
INSERT INTO order_item (id, order_id, asset_id, quantity, unit_price, subtotal_amount, created_time) VALUES
(1, 1, 1, 1, 15200.00, 15200.00, NOW());