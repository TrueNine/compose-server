create table if not exists mark_user
(
  user_mark_type     integer         default null, -- 标记用户类别
  audit_status       integer         default null, -- 审核状态
  user_id            bigint unsigned default null, -- 绑定 的 user_id
  account            varchar(255)    default null, -- 绑定的用户账号
  actual_name        varchar(255)    default null, -- 真实姓名
  phone              varchar(64)     default null, -- 手机号
  spare_phone        varchar(64)     default null, -- 备用手机号
  idcard             varchar(127)    default null, -- 身份证号
  email              varchar(127)    default null, -- 邮箱
  remark             text            default null, -- 备注
  wechat_openid      varchar(255)    default null, -- 微信 openid
  wechat_account     varchar(255)    default null, -- 微信账号
  qq_openid          varchar(255)    default null, -- qq openid
  qq_account         varchar(255)    default null, -- qq 账号
  address_code       varchar(127)    default null, -- 所属地域
  address_details_id bigint unsigned default null, -- 详细地址 id
  unique (user_id),
  unique (phone),
  unique (idcard),
  unique (email),
  unique (wechat_openid),
  unique (wechat_account),
  unique (qq_openid),
  unique (qq_account),
  index (address_details_id),
  index (address_code)
) default charset = utf8mb4, comment '标记用户表（表示系统以外的用户）';
call add_base_struct('mark_user');

