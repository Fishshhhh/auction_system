-- 拍卖系统数据库表结构定义（第二部分）

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

-- 拍卖表
CREATE TABLE IF NOT EXISTS auction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- 删除原有的asset_id字段，因为现在支持多资产
    -- asset_id BIGINT NOT NULL,
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
    -- FOREIGN KEY (asset_id) REFERENCES asset(id),
    FOREIGN KEY (template_id) REFERENCES auction_template(id)
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

-- 出价表
CREATE TABLE IF NOT EXISTS bid (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    bid_price DECIMAL(10,2) NOT NULL,
    -- 添加购买数量字段
    quantity INT NOT NULL DEFAULT 1,
    bid_status INT DEFAULT 1 COMMENT '1-有效 2-无效 3-中标 4-失败',
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
    -- 删除原有的asset_id字段，因为一个订单可能包含多个资产
    -- asset_id BIGINT NOT NULL,
    bid_id BIGINT,
    order_amount DECIMAL(10,2) NOT NULL,
    -- 添加购买数量字段
    quantity INT NOT NULL DEFAULT 1,
    order_status INT DEFAULT 1 COMMENT '1-待支付 2-已支付 3-已完成 4-已取消 5-退款中 6-已退款',
    created_time DATETIME,
    updated_time DATETIME,
    payment_time DATETIME,
    completed_time DATETIME,
    FOREIGN KEY (auction_id) REFERENCES auction(id),
    FOREIGN KEY (buyer_id) REFERENCES `user`(id),
    FOREIGN KEY (seller_id) REFERENCES `user`(id),
    -- FOREIGN KEY (asset_id) REFERENCES asset(id),
    FOREIGN KEY (bid_id) REFERENCES bid(id)
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