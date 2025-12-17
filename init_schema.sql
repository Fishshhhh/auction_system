-- 拍卖系统数据库表结构定义

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    full_name VARCHAR(100),
    user_type INT NOT NULL COMMENT '1-管理员 2-运营 3-卖家 4-买家',
    status INT DEFAULT 1 COMMENT '1-正常 0-禁用',
    created_time DATETIME,
    updated_time DATETIME
);

-- 资产分类表
CREATE TABLE IF NOT EXISTS asset_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    status INT DEFAULT 1 COMMENT '1-启用 0-禁用',
    created_time DATETIME,
    updated_time DATETIME
);

-- 资产表
CREATE TABLE IF NOT EXISTS asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    asset_no VARCHAR(50) NOT NULL UNIQUE,
    category_id BIGINT NOT NULL,
    starting_price DECIMAL(10,2),
    reserve_price DECIMAL(10,2),
    current_price DECIMAL(10,2),
    owner_id BIGINT NOT NULL,
    status INT DEFAULT 1 COMMENT '1-草稿 2-待审核 3-审核拒绝 4-已发布',
    auction_status INT DEFAULT 1 COMMENT '1-未拍卖 2-拍卖中 3-已成交 4-已下架',
    auction_type INT COMMENT '1-公开实时竞价 2-暗拍 3-无底价 4-定向 5-降价',
    created_time DATETIME,
    updated_time DATETIME,
    published_time DATETIME,
    auction_start_time DATETIME,
    auction_end_time DATETIME,
    FOREIGN KEY (category_id) REFERENCES asset_category(id),
    FOREIGN KEY (owner_id) REFERENCES `user`(id)
);

-- 资产图片表
CREATE TABLE IF NOT EXISTS asset_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort_order INT DEFAULT 0,
    is_cover TINYINT DEFAULT 0,
    created_time DATETIME,
    FOREIGN KEY (asset_id) REFERENCES asset(id) ON DELETE CASCADE
);

-- 拍卖模板表
CREATE TABLE IF NOT EXISTS auction_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    auction_type INT NOT NULL COMMENT '1-公开实时竞价 2-暗拍 3-无底价 4-定向 5-降价',
    config_params JSON,
    description TEXT,
    is_default INT DEFAULT 0,
    status INT DEFAULT 1,
    bid_increment DECIMAL(10,2),
    extend_time INT,
    extend_threshold INT,
    initial_price DECIMAL(10,2),
    price_step DECIMAL(10,2),
    price_drop_interval INT,
    min_price DECIMAL(10,2),
    qualification_required TINYINT DEFAULT 0,
    deposit_rate DECIMAL(5,2),
    created_time DATETIME,
    updated_time DATETIME
);

-- 拍卖表
CREATE TABLE IF NOT EXISTS auction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT,
    start_price DECIMAL(10,2),
    current_price DECIMAL(10,2),
    reserve_price DECIMAL(10,2),
    buy_it_now_price DECIMAL(10,2),
    start_time DATETIME,
    end_time DATETIME,
    auction_status INT DEFAULT 1 NOT NULL COMMENT '1-未开始 2-进行中 3-已结束 4-已成交 5-流拍',
    auction_type INT COMMENT '1-公开实时竞价 2-暗拍 3-无底价 4-定向 5-降价',
    bid_increment DECIMAL(10,2),
    extend_time INT,
    extend_threshold INT,
    initial_price DECIMAL(10,2),
    price_step DECIMAL(10,2),
    price_drop_interval INT,
    min_price DECIMAL(10,2),
    next_price_drop_time DATETIME,
    qualification_required TINYINT DEFAULT 0,
    winner_user_id BIGINT,
    final_price DECIMAL(10,2),
    deposit_required TINYINT DEFAULT 0,
    deposit_amount DECIMAL(10,2),
    created_time DATETIME,
    updated_time DATETIME,
    FOREIGN KEY (template_id) REFERENCES auction_template(id)
);

-- 出价表
CREATE TABLE IF NOT EXISTS bid (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    bid_price DECIMAL(10,2) NOT NULL,
    bid_status INT DEFAULT 1 COMMENT '1-有效 2-无效 3-中标',
    created_time DATETIME,
    FOREIGN KEY (auction_id) REFERENCES auction(id),
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    auction_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    bid_id BIGINT,
    order_amount DECIMAL(10,2) NOT NULL,
    order_status INT DEFAULT 1 COMMENT '1-待支付 2-已支付 3-已完成 4-已取消 5-退款中 6-已退款',
    created_time DATETIME,
    updated_time DATETIME,
    payment_time DATETIME,
    completed_time DATETIME,
    FOREIGN KEY (auction_id) REFERENCES auction(id),
    FOREIGN KEY (buyer_id) REFERENCES `user`(id),
    FOREIGN KEY (seller_id) REFERENCES `user`(id),
    FOREIGN KEY (bid_id) REFERENCES bid(id)
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500),
    display_name VARCHAR(200),
    description VARCHAR(500),
    config_group VARCHAR(50),
    status INT DEFAULT 1 COMMENT '1-启用 0-禁用',
    created_time DATETIME,
    updated_time DATETIME
);

-- 拍卖资产关联表
CREATE TABLE IF NOT EXISTS auction_asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    start_price DECIMAL(10,2),
    current_price DECIMAL(10,2),
    reserve_price DECIMAL(10,2),
    created_time DATETIME,
    updated_time DATETIME,
    FOREIGN KEY (auction_id) REFERENCES auction(id) ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset(id)
);

-- 订单项表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal_amount DECIMAL(10,2) NOT NULL,
    created_time DATETIME,
    updated_time DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset(id)
);
