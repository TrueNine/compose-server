CREATE
    TABLE
        IF NOT EXISTS audit(
            status INTEGER DEFAULT 0, -- 状态
            create_datetime datetime DEFAULT now(), -- 创建时间
            audit_user_id BIGINT DEFAULT NULL, -- 审核人
            remark text DEFAULT NULL, -- 审核备注
            ref_id BIGINT NOT NULL, -- 审核对象id
            ref_type INTEGER NOT NULL, -- 审核对象类型
            audit_ip VARCHAR(64) DEFAULT NULL, -- 审核人 ip
            audit_device_id VARCHAR(255) DEFAULT NULL, -- 审核人设备 idINDEX(status),INDEX(audit_user_id),INDEX(ref_id),INDEX(ref_type)
        ) DEFAULT charset = utf8mb4,
        comment '审核备注';

CALL add_base_struct('audit');

CREATE
    TABLE
        IF NOT EXISTS audit_attachment(
            att_id BIGINT NOT NULL, -- 附件 id
            audit_id BIGINT NOT NULL, -- 审核图片
            status INT DEFAULT NULL, -- 审核状态INDEX(att_id),INDEX(audit_id),INDEX(status)
        ) DEFAULT charset = utf8mb4,
        comment '审核附件';

CALL add_base_struct('audit_attachment');
