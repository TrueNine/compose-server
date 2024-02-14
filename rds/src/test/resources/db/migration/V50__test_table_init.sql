create table if not exists db_test_presort_tree
(
    title varchar(1023) null comment '测试数据'
) default charset = utf8mb4,comment '测试预排序树';
call add_base_struct('db_test_presort_tree');
call add_presort_tree_struct('db_test_presort_tree');
