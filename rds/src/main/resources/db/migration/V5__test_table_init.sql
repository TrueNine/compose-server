CREATE TABLE IF NOT EXISTS db_test_presort_tree
(
  title VARCHAR(1023) NULL COMMENT '测试数据'
) DEFAULT CHARSET = utf8mb4,COMMENT '测试预排序树';
CALL add_base_struct('db_test_presort_tree');
CALL add_presort_tree_struct('db_test_presort_tree');
