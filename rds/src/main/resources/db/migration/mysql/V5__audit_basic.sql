create table if not exists audit
(
  status          integer  default 0,       -- 状态
  create_datetime datetime default now(),   -- 创建时间
  audit_user_id   bigint   default null,    -- 审核人
  remark          text     default null,    -- 审核备注
  ref_id          bigint unsigned not null, -- 审核对象id
  ref_type        integer     not null,-- 审核对象类型
  index (status),
  index (audit_user_id),
  index (ref_id),
  index (ref_type)
) default charset = utf8mb4, comment '审核备注';
call add_base_struct('audit');



create table if not exists audit_attachment
(
  att_id   bigint not null, -- 附件 id
  audit_id bigint not null, -- 审核图片
  status   int default null,-- 审核状态
  index (att_id),
  index (audit_id),
  index (status)
) default charset = utf8mb4, comment '审核附件';
call add_base_struct('audit_attachment');