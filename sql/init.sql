CREATE DATABASE IF NOT EXISTS smart_insight
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE smart_insight;

DROP TABLE IF EXISTS document_info;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_info;

CREATE TABLE user_info
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '用户密码(BCrypt加密)',
    email VARCHAR(100) COMMENT '用户邮箱',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '用户状态:0正常 1禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除'
)
COMMENT='用户信息表'
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE role
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
)
COMMENT='角色表'
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_role
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role(user_id, role_id)
)
COMMENT='用户角色关联表'
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE document_info
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档ID',
    owner_id BIGINT NOT NULL COMMENT '文档所属用户ID',
    title VARCHAR(200) NOT NULL COMMENT '文档标题',
    description VARCHAR(1000) COMMENT '文档描述',
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '文档状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    KEY idx_document_owner(owner_id),
    KEY idx_document_status(status),
    KEY idx_document_create_time(create_time)
)
COMMENT='文档元数据表'
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO role (role_name) VALUES ('ADMIN'), ('USER');

-- 示例管理员密码必须替换为真实 BCrypt 哈希后才能用于登录。
INSERT INTO user_info (username, password, email, status, deleted)
VALUES ('admin', '$2a$10$examplebcryptpassword', 'admin@smart.com', 0, 0);
