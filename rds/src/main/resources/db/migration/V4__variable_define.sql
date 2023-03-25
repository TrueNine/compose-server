CREATE TABLE IF NOT EXISTS durations
(
  name       VARCHAR(255) COMMENT '名称',
  years      INT UNSIGNED DEFAULT 0 COMMENT '年',
  months     INT UNSIGNED DEFAULT 0 COMMENT '月',
  month_days INT UNSIGNED DEFAULT 30 COMMENT '月计算天数',
  days       INT UNSIGNED DEFAULT 0 COMMENT '日',
  weeks      INT UNSIGNED DEFAULT 0 COMMENT '星期',
  hours      INT UNSIGNED DEFAULT 0 COMMENT '小时',
  minutes    BIGINT       DEFAULT 0 COMMENT '分钟',
  seconds    BIGINT       DEFAULT 0 COMMENT '秒',
  millis     BIGINT       DEFAULT 0 COMMENT '毫秒'
) DEFAULT CHARSET = utf8mb4,
  COMMENT '时间区间，只能存在一个';
CALL base_tab('durations');


INSERT INTO durations
SET id   = 0,
    name = '三年',
    years=3;
INSERT INTO durations
SET id=1,
    name='一周',
    days=7,
    weeks=1;


CREATE TABLE IF NOT EXISTS tab_order
(
  name    VARCHAR(255)     NOT NULL COMMENT '名称',
  doc     TEXT COMMENT '描述',
  ordered BIGINT DEFAULT 0 NOT NULL COMMENT '排序值',
  INDEX (ordered)
) DEFAULT CHARSET = utf8mb4,COMMENT '排序';
CALL base_tab('tab_order');

INSERT INTO tab_order
SET id=0,
    ordered=0,
    name='默认',
    doc='默认排序，务必不要删除';
