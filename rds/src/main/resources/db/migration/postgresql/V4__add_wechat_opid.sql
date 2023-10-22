-- 增加微信 open id
alter table user_info
  add column wechat_openid varchar(255) null,
  add column wechat_authid varchar(255),
  add unique (wechat_openid);
create index on user_info(wechat_openid);
create index on user_info(wechat_authid);

