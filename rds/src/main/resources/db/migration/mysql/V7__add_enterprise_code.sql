create table if not exists biz_licence
(
  user_id            bigint unsigned not null,     -- 上传人
  title              varchar(255)    not null,     -- 公司名称
  reg_capital        decimal(10, 2)  default null, -- 注册资本
  create_date        date            default null, -- 成立日期
  uni_credit_code    varchar(255)    default null, -- 统一社会信用代码
  type               varchar(127)    default null, -- 类型
  leader_name        varchar(255)    default null, -- 法定代表人
  biz_range          text            default null, -- 经营范围
  address_code       varchar(255)    default null, -- 地址编码
  address_details_id bigint unsigned default null, -- 地址详情 id
  issue_date         date            default null, -- 签发日期
  index (user_id),
  index (address_code),
  index (address_details_id)
) comment '营业执照';
call add_presort_tree_struct('biz_licence');
