CREATE
    TABLE
        IF NOT EXISTS db_test_presort_tree(
            title VARCHAR(1023) NULL comment '测试数据'
        ) DEFAULT charset = utf8mb4,
        comment '测试预排序树';

CALL add_base_struct('db_test_presort_tree');

CALL add_presort_tree_struct('db_test_presort_tree');
