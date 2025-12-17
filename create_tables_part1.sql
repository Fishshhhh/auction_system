-- 拍卖系统数据库表结构定义（第一部分）

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