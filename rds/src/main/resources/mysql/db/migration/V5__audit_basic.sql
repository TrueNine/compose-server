create
    table
        if not exists audit(
            status integer default 0, -- 状态
            create_datetime datetime default now(), -- 创建时间
            audit_user_id bigint default null, -- 审核人
            remark text default null, -- 审核备注
            ref_id bigint not null, -- 审核对象id
            ref_type integer not null, -- 审核对象类型
            audit_ip varchar(64) default null, -- 审核人 ip
            audit_device_id varchar(255) default null, -- 审核人设备 idINDEX(status),INDEX(audit_user_id),INDEX(ref_id),INDEX(ref_type)
        ) default charset = utf8mb4,
        comment '审核备注';

call add_base_struct('audit');

create
    table
        if not exists audit_attachment(
            att_id bigint not null, -- 附件 id
            audit_id bigint not null, -- 审核图片
            status int default null, -- 审核状态INDEX(att_id),INDEX(audit_id),INDEX(status)
        ) default charset = utf8mb4,
        comment '审核附件';

call add_base_struct('audit_attachment');
