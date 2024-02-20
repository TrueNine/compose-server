CREATE
    TABLE
        IF NOT EXISTS audit(
            status INTEGER DEFAULT 0, -- 状态
            create_datetime TIMESTAMP DEFAULT now(), -- 创建时间
            audit_user_id BIGINT DEFAULT NULL, -- 审核人
            ref_id BIGINT DEFAULT NULL, -- 审核对象 id
            ref_type INTEGER DEFAULT NULL, -- 审核对象类型
            audit_ip VARCHAR(64) DEFAULT NULL, -- 审核人 ip
            audit_device_id VARCHAR(255) DEFAULT NULL, -- 审核人设备 id
            remark text DEFAULT NULL -- 审核备注

        );

comment ON
TABLE
    audit IS '审核备注';

SELECT
    add_base_struct('audit');

CREATE
    INDEX ON
    audit(status);

CREATE
    INDEX ON
    audit(audit_user_id);

CREATE
    INDEX ON
    audit(ref_id);

CREATE
    INDEX ON
    audit(ref_type);

CREATE
    TABLE
        IF NOT EXISTS audit_attachment(
            att_id BIGINT NOT NULL, -- 附件 id
            audit_id BIGINT NOT NULL, -- 审核图片
            status INT DEFAULT NULL -- 审核状态

        );

SELECT
    add_base_struct('audit_attachment');

CREATE
    INDEX ON
    audit_attachment(status);

CREATE
    INDEX ON
    audit_attachment(att_id);

CREATE
    INDEX ON
    audit_attachment(audit_id);
