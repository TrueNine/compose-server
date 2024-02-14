create table if not exists db_test_period_converter
(
    periods varchar(127) null comment '测试数据'
) default charset = utf8mb4,comment '测试预排序树';
call add_base_struct('db_test_period_converter');


create table if not exists db_test_duration_converter
(
    durations varchar(127) null comment '测试数据'
) default charset = utf8mb4,comment '测试预排序树';
call add_base_struct('db_test_duration_converter');
