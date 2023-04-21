# 增加微信 open id
ALTER TABLE `user_info`
  ADD COLUMN wechat_open_id VARCHAR(255) NULL COMMENT '微信个人 openId',
  ADD INDEX (wechat_open_id) COMMENT '微信 openId 经常查询',
  ADD UNIQUE (wechat_open_id) COMMENT '微信 openId唯一';

# 增加微信 open id 映射列
ALTER TABLE user_info
  ADD COLUMN wechat_auth_id VARCHAR(127) COMMENT '微信自定义登录id',
  ADD INDEX (wechat_auth_id) COMMENT '微信自定义登录id经常查询';
