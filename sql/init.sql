CREATE DATABASE IF NOT EXISTS smart_insight
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE smart_insight;

DROP TABLE IF EXISTS user_info;


CREATE TABLE user_info
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',

    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',

    password VARCHAR(255) NOT NULL COMMENT '用户密码(BCrypt加密)',

    email VARCHAR(100) COMMENT '用户邮箱',

    status TINYINT DEFAULT 1 COMMENT '用户状态:1正常 0禁用',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    update_time DATETIME DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'

)
COMMENT='用户信息表'

ENGINE=InnoDB

DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS role;


CREATE TABLE role
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',

    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'

)
COMMENT='角色表'

ENGINE=InnoDB

DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS user_role;


CREATE TABLE user_role
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    role_id BIGINT NOT NULL COMMENT '角色ID',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',


    UNIQUE KEY uk_user_role(user_id,role_id)

)
COMMENT='用户角色关联表'

ENGINE=InnoDB

DEFAULT CHARSET=utf8mb4;

INSERT INTO role
(
    role_name
)
VALUES
(
    'ADMIN'
),
(
    'USER'
);

INSERT INTO user_info
(
 username,
 password,
 email
)
VALUES
(
 'admin',
 '$2a$10$examplebcryptpassword',
 'admin@smart.com'
);