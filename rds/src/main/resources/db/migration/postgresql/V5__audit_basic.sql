create table if not exists audit
(
  status          integer   default 0,     -- 状态
  create_datetime timestamp default now(), -- 创建时间
  audit_user_id   bigint    default null,  -- 审核人
  ref_id          bigint    default null,  -- 审核对象 id
  ref_type        integer   default null,  -- 审核对象类型
  remark          text      default null   -- 审核备注
);
comment on table audit is '审核备注';
select add_base_struct('audit');
create index on audit (status);
create index on audit (audit_user_id);
create index on audit (ref_id);
create index on audit (ref_type);


create table if not exists audit_attachment
(
  att_id   bigint not null, -- 附件 id
  audit_id bigint not null, -- 审核图片
  status   int default null -- 审核状态
);
select add_base_struct('audit_attachment');
create index on audit_attachment (status);
create index on audit_attachment (att_id);
create index on audit_attachment (audit_id);
