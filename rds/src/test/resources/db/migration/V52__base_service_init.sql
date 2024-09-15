create
    table
        if not exists db_test_service(
            title varchar(127) null comment '名称',
            center varchar(255) null comment '位置测试'
        ) default charset = utf8mb4,
        comment 'base service 测试表';

call add_base_struct('db_test_service');

call add_presort_tree_struct('db_test_service');
