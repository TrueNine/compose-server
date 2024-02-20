CREATE
    TABLE
        IF NOT EXISTS cert(
            wm_code VARCHAR(255) NOT NULL, -- 水印码
            do_type INTEGER NOT NULL, -- 证件类型,身份证，户口本
            co_type INTEGER NOT NULL, -- 证件处理类型，拍照，扫描件，截图，视频
            po_type INTEGER NOT NULL, -- 证件印面类型，正面，反面，双面
            user_id BIGINT DEFAULT NULL, -- 用户 id
            user_info_id BIGINT DEFAULT NULL, -- 用户信息 id
            name VARCHAR(255) DEFAULT NULL, -- 证件名称
            audit_status INTEGER DEFAULT NULL, -- 审核状态
            doc VARCHAR(255) DEFAULT NULL, -- 证件描述
            remark text DEFAULT NULL, -- 证件备注
            create_datetime TIMESTAMP DEFAULT now(), -- 创建时间
            create_ip VARCHAR(255) DEFAULT NULL, -- 创建 ip
            create_device_id VARCHAR(1023) DEFAULT NULL, -- 创建证件的设备 id
            create_user_id BIGINT NOT NULL, -- 证件提交人 id
            att_id BIGINT NOT NULL, -- 证件 附件 id
            wm_att_id BIGINT NOT NULL -- 证件水印 附件 id

        );

comment ON
TABLE
    cert IS '用户 标记用户  证件';

SELECT
    add_base_struct('cert');

CREATE
    INDEX ON
    cert(user_id);

CREATE
    INDEX ON
    cert(user_info_id);

CREATE
    INDEX ON
    cert(create_user_id);

CREATE
    INDEX ON
    cert(att_id);

CREATE
    INDEX ON
    cert(wm_att_id);
