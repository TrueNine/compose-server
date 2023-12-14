create table if not exists user_document
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
  wm_att_id        bigint       not null        -- 证件水印 附件 id
);
comment on table user_document is '用户 标记用户  证件';
select add_base_struct('user_document');
create index on user_document (user_id);
create index on user_document (user_info_id);
create index on user_document (create_user_id);
create index on user_document (att_id);
create index on user_document (wm_att_id);
