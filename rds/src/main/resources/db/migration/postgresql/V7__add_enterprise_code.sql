create table if not exists biz_cert
(
  user_id            bigint         default null, -- 上传人
  user_info_id       bigint         default null, -- 用户信息 id
  title              varchar(255) not null,       -- 公司名称
  reg_capital        decimal(10, 2) default null, -- 注册资本
  create_date        date           default null, -- 成立日期
  uni_credit_code    varchar(255)   default null, -- 统一社会信用代码
  type               varchar(127)   default null, -- 类型
  leader_name        varchar(255)   default null, -- 法定代表人
  biz_range          text           default null, -- 经营范围
  address_code       varchar(255)   default null, -- 地址编码
  address_details_id bigint         default null, -- 地址详情 id
  issue_date         date           default null  -- 签发日期
);
comment on table biz_cert is '营业执照';
select add_base_struct('biz_cert');
create index on biz_cert (user_id);
create index on biz_cert (address_code);
create index on biz_cert (address_details_id);
