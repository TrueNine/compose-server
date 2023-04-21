# 增加微信 open id
ALTER TABLE `user_info`
  ADD COLUMN wechat_open_id VARCHAR(255) NULL COMMENT '微信个人 openId',
  ADD INDEX (wechat_open_id) COMMENT '微信 openId 经常查询',
  ADD UNIQUE (wechat_open_id) COMMENT '微信 openId唯一';
