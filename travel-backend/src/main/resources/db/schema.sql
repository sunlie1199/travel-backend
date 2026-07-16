-- 山水记 · 数据库初始化脚本
-- MySQL 5.7+ 必须，以支持 JSON 类型

CREATE DATABASE IF NOT EXISTS travel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travel;

-- 目的地表
CREATE TABLE destinations (
    id          VARCHAR(64)    PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    region      VARCHAR(50)    NOT NULL,
    country     VARCHAR(50)    NOT NULL DEFAULT '中国',
    status      VARCHAR(10)    NOT NULL DEFAULT 'wishlist' COMMENT 'visited | wishlist',
    description TEXT           NOT NULL,
    highlights  JSON           DEFAULT NULL,
    best_season VARCHAR(100)   DEFAULT NULL,
    images      JSON           DEFAULT NULL,
    latitude    DECIMAL(10, 7) DEFAULT NULL,

    longitude   DECIMAL(10, 7) DEFAULT NULL,
    rating      DECIMAL(2, 1)  DEFAULT 4.5,
    tags        JSON           DEFAULT NULL,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 旅行者-目的地关联表
CREATE TABLE traveler_destinations (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    destination_id  VARCHAR(64) NOT NULL,
    traveler_id     VARCHAR(10) NOT NULL COMMENT 'male | female',
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dest_traveler (destination_id, traveler_id),
    FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE CASCADE
);

-- 管理员用户表
CREATE TABLE users (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE audit_logs (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL                    COMMENT '操作人',
    module          VARCHAR(20)  NOT NULL                    COMMENT '模块: AUTH / DESTINATION / FILE',
    action          VARCHAR(20)  NOT NULL                    COMMENT '操作: LOGIN / CREATE / UPDATE / DELETE / UPLOAD / DELETE_FILE',
    resource_id     VARCHAR(64)  DEFAULT NULL                COMMENT '资源 ID（目的地 ID 等，登录时为 NULL）',
    resource_name   VARCHAR(200) DEFAULT NULL                COMMENT '资源名称（目的地名 / 文件名）',
    detail          JSON         DEFAULT NULL                COMMENT '操作详情（变更前后数据、上传 URL 等）',
    ip              VARCHAR(45)  DEFAULT NULL                COMMENT '客户端 IP',
    success         TINYINT(1)   NOT NULL DEFAULT 1          COMMENT '是否成功: 1-成功 0-失败',
    error_msg       VARCHAR(500) DEFAULT NULL                COMMENT '失败原因',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_username   (username),
    INDEX idx_module     (module),
    INDEX idx_action     (action),
    INDEX idx_created_at (created_at)
) COMMENT '操作审计日志';
