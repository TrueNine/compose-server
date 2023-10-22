# 增加微信 open id
alter table user_info
  add column wechat_openid varchar(255) null comment '微信个人 openId',
  add index (wechat_openid) comment '微信 openId 经常查询',
  add unique (wechat_openid) comment '微信 openId唯一';

# 增加微信 open id 映射列
alter table user_info
  add column wechat_authid varchar(127) comment '微信自定义登录id',
  add index (wechat_authid) comment '微信自定义登录id经常查询';
