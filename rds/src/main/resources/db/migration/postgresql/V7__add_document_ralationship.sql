create table if not exists mu_document
(
  d_type          integer not null,            -- 证件类型,身份证，户口本
  c_type          integer not null,            -- 证件处理类型，拍照，扫描件，截图，视频
  p_type          integer not null,            -- 证件印面类型，正面，反面，双面
  user_id         bigint        default null,  -- 用户 id
  mark_user_id    bigint        default null,  -- 标记用户 id
  name            varchar(255)  default null,  -- 证件名称
  audit_status    integer       default null,  -- 审核状态
  doc             varchar(255)  default null,  -- 证件描述
  remark          text          default null,  -- 证件备注
  create_datetime timestamp     default now(), -- 创建时间
  create_ip       varchar(255)  default null,  -- 创建 ip
  create_device_id   varchar(1023) default null,  -- 创建证件的设备 id
  create_user_id  bigint        default null,  -- 证件提交人 id
  att_id          bigint  not null             -- 证件 附件 id
);
comment on table mu_document is '用户 标记用户  证件';
select add_base_struct('mu_document');
create index on mu_document (user_id);
create index on mu_document (mark_user_id);
create index on mu_document (create_user_id);
create index on mu_document (att_id);
