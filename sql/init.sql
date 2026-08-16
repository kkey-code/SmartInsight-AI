-- =========================================================
-- SmartInsight-AI 数据库初始化脚本
-- MySQL 8.x
-- =========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS smart_insight
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE smart_insight;


-- =========================================================
-- 2. 删除旧表
-- =========================================================

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_info;


-- =========================================================
-- 3. 用户表
-- =========================================================

CREATE TABLE user_info
(
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',

    username VARCHAR(50) NOT NULL COMMENT '用户名',

    password VARCHAR(255) NOT NULL COMMENT '用户密码，BCrypt加密',

    email VARCHAR(100) DEFAULT NULL COMMENT '用户邮箱',

    status TINYINT NOT NULL DEFAULT 0 COMMENT '用户状态：0正常，1禁用',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',

    PRIMARY KEY (id),

    UNIQUE KEY uk_user_username (username)

)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='用户信息表';


-- =========================================================
-- 4. 角色表
-- =========================================================

CREATE TABLE role
(
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',

    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (id),

    UNIQUE KEY uk_role_name (role_name)

)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='角色表';


-- =========================================================
-- 5. 用户角色关联表
-- =========================================================

CREATE TABLE user_role
(
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    role_id BIGINT NOT NULL COMMENT '角色ID',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (id),

    UNIQUE KEY uk_user_role (user_id, role_id),

    KEY idx_user_id (user_id),

    KEY idx_role_id (role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
        REFERENCES user_info (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
        REFERENCES role (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='用户角色关联表';


-- =========================================================
-- 6. 初始化角色
-- =========================================================

INSERT INTO role (role_name)
VALUES
    ('ADMIN'),
    ('USER');


-- =========================================================
-- 7. 初始化管理员
-- =========================================================
--
-- 密码：
-- 这里使用 BCrypt 密文。
-- 当前只是初始化测试账号。
--
-- 用户名：admin
-- 密码：admin123
--
-- 如果你已经有正式管理员密码，不要继续使用这个测试密码。
--

INSERT INTO user_info
(
    username,
    password,
    email,
    status,
    deleted
)
VALUES
(
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@smart.com',
    0,
    0
);


-- =========================================================
-- 8. 给 admin 分配 ADMIN 角色
-- =========================================================

INSERT INTO user_role
(
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM user_info u
JOIN role r
    ON r.role_name = 'ADMIN'
WHERE u.username = 'admin';


-- =========================================================
-- 9. 验证初始化结果
-- =========================================================

SELECT
    u.id,
    u.username,
    u.email,
    u.status,
    u.deleted
FROM user_info u;


SELECT
    r.id,
    r.role_name
FROM role r;


SELECT
    u.username,
    r.role_name
FROM user_role ur
JOIN user_info u
    ON u.id = ur.user_id
JOIN role r
    ON r.id = ur.role_id
ORDER BY u.id, r.id;