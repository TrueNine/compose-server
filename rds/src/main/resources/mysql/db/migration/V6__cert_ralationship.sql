create
    table
    if not exists cert
(
    wm_code          varchar(255) not null,       -- 水印码
    do_type          integer      not null,       -- 证件类型,身份证，户口本
    co_type          integer      not null,       -- 证件处理类型，拍照，扫描件，截图，视频
    po_type          integer      not null,       -- 证件印面类型，正面，反面，双面
    user_id          bigint        default null,  -- 用户 id
    user_info_id     bigint        default null,  -- 用户信息 id
    name             varchar(255)  default null,  -- 证件名称
    audit_status     integer       default null,  -- 审核状态
    doc              varchar(255)  default null,  -- 证件描述
    remark           text          default null,  -- 证件备注
    create_datetime  timestamp     default now(), -- 创建时间
    create_ip        varchar(255)  default null,  -- 创建 ip
    create_device_id varchar(1023) default null,  -- 创建证件的设备 id
    create_user_id   bigint       not null,       -- 证件提交人 id
    att_id           bigint       not null,       -- 证件 附件 id
    wm_att_id        bigint        default null,  -- 证件 水印 附件 idINDEX(user_id),INDEX(user_info_id),INDEX(wm_att_id),INDEX(create_user_id),INDEX(att_id)
) comment '用户 标记用户  证件';

call add_base_struct('cert');
