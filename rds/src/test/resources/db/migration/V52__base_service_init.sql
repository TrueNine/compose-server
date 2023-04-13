CREATE TABLE IF NOT EXISTS db_test_service
(
  title  VARCHAR(127) NULL COMMENT '名称',
  center VARCHAR(255)        NULL COMMENT '位置测试'
) DEFAULT CHARSET = utf8mb4,COMMENT 'base service 测试表';
CALL add_base_struct('db_test_service');
CALL add_presort_tree_struct('db_test_service');
