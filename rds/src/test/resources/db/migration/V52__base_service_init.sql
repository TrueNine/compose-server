CREATE
    TABLE
        IF NOT EXISTS db_test_service(
            title VARCHAR(127) NULL comment '名称',
            center VARCHAR(255) NULL comment '位置测试'
        ) DEFAULT charset = utf8mb4,
        comment 'base service 测试表';

CALL add_base_struct('db_test_service');

CALL add_presort_tree_struct('db_test_service');
