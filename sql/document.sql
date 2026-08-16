CREATE TABLE document_info
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档ID',

    owner_id BIGINT NOT NULL COMMENT '文档所属用户ID',

    title VARCHAR(255) NOT NULL COMMENT '文档标题',
    description VARCHAR(1000) DEFAULT NULL COMMENT '文档描述',

    file_name VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
    file_size BIGINT DEFAULT NULL COMMENT '文件大小，单位：字节',
    file_type VARCHAR(100) DEFAULT NULL COMMENT '文件类型，例如 pdf/docx/txt',
    storage_key VARCHAR(500) DEFAULT NULL COMMENT '文件存储Key',

    status TINYINT NOT NULL DEFAULT 0
        COMMENT '文档状态:0草稿 1处理中 2就绪 3失败',

    error_message VARCHAR(2000) DEFAULT NULL COMMENT '处理失败原因',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
        COMMENT '更新时间',

    deleted TINYINT NOT NULL DEFAULT 0
        COMMENT '逻辑删除:0未删除 1已删除',

    KEY idx_document_owner(owner_id),
    KEY idx_document_status(status),
    KEY idx_document_owner_status(owner_id, status),
    KEY idx_document_create_time(create_time)
)
COMMENT='文档元数据表'
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE document_info
    ADD COLUMN file_path VARCHAR(500) NULL COMMENT '文件存储路径';