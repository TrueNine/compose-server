create table if not exists user_document
(
  wm_code         varchar(255)    default null,  -- 水印码
  d_type          integer         not null,      -- 证件类型,身份证，户口本
  c_type          integer         not null,      -- 证件处理类型，拍照，扫描件，截图，视频
  p_type          integer         not null,      -- 证件印面类型，正面，反面，双面
  user_id         bigint unsigned default null,  -- 用户 id
  name            varchar(255)    default null,  -- 证件名称
  audit_status    integer         default null,  -- 审核状态
  doc             varchar(255)    default null,  -- 证件描述
  remark          text            default null,  -- 证件备注
  create_datetime timestamp       default now(), -- 创建时间
  create_ip       varchar(255)    default null,  -- 创建 ip
  create_device   varchar(1023)   default null,  -- 创建证件的设备 id
  create_user_id  bigint unsigned default null,  -- 证件提交人 id
  att_id          bigint unsigned not null,      -- 证件 附件 id
  wm_att_id       bigint unsigned default null,  -- 证件 水印 附件 id
  index (user_id),
  index (wm_att_id),
  index (create_user_id),
  index (att_id)
) comment '用户 标记用户  证件';
call add_base_struct('user_document');
