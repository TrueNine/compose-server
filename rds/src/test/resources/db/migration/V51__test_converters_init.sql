CREATE
    TABLE
        IF NOT EXISTS db_test_period_converter(
            periods VARCHAR(127) NULL comment '测试数据'
        ) DEFAULT charset = utf8mb4,
        comment '测试预排序树';

CALL add_base_struct('db_test_period_converter');

CREATE
    TABLE
        IF NOT EXISTS db_test_duration_converter(
            durations VARCHAR(127) NULL comment '测试数据'
        ) DEFAULT charset = utf8mb4,
        comment '测试预排序树';

CALL add_base_struct('db_test_duration_converter');
