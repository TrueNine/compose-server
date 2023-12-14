# 设置时区为 +8:00
set TIME_ZONE = '+8:00';
set charset utf8mb4;
flush privileges;

set names utf8mb4;
set FOREIGN_KEY_CHECKS = 0;

# 基础表字段
delimiter $$
create procedure add_base_struct(in tab_name varchar(128))
begin
  set @after = concat(
    'alter table', tab_name,
    'add `id` bigint unsigned primary key comment \'主键\',',
    'add `rlv` bigint unsigned default 0 comment \'乐观锁版本号 row lock version\',',
    'add `ldf` boolean default false comment \'逻辑删除标志 logic delete flag\',',
    'add `crd` datetime default now() comment \'字段创建时间 create row datetime\',',
    'add `mrd` datetime default now() comment \'字段修改时间 modify row datetime\',',
    'engine = InnoDB,',
    'default charset = utf8mb4,',
    'auto_increment = 100;'
               );
  set @statement = concat(@after);
  prepare state
    from @statement;

  set @tbl_exist = (select count(1)
                    from information_schema.tables
                    where table_schema = (select database())
                      and table_name = tab_name);
  set @col_exists = (select count(1)
                     from information_schema.columns
                     where table_schema = (select database())
                       and table_name = tab_name
                       and column_name in ('id', 'rct', 'rcb'));
  if ((@tbl_exist) > 0 and (@col_exists) <= 0) then
    execute state;
  end if;
end $$
delimiter ;

# 预排序树结构
delimiter $$
create procedure add_presort_tree_struct(in tab_name varchar(128))
begin
  set @after = concat(
    'alter table', tab_name,
    'add `rpi` bigint unsigned default null comment \'父节点id parent id\',',
    'add `rln` bigint unsigned default 1 comment \'左节点 row left node\',',
    'add `rrn` bigint unsigned default 2 comment \'右节点 row right node\',',
    'add `nlv` bigint unsigned default 0 comment \'节点级别 node level\',',
    'add `tgi` varchar(64) default \'0\' comment \'树组id tree group id\',',
    'add index(`rln`) comment \'索引左节点\',',
    'add index(`rrn`) comment \'索引右节点\',',
    'add index(`tgi`) comment \'树组id\',',
    'add index(`rpi`) comment \'自联 父节点\';');
  set @statement = concat(@after);
  prepare state
    from @statement;

  set @tbl_exist = (select count(1)
                    from information_schema.tables
                    where table_schema = (select database())
                      and table_name = tab_name);
  set @col_exists = (select count(1)
                     from information_schema.columns
                     where table_schema = (select database())
                       and table_name = tab_name
                       and column_name in ('rpi', 'rln', 'rrn'));
  if ((@tbl_exist) > 0 and (@col_exists) <= 0) then
    execute state;
  end if;
end $$
delimiter ;

# 任意外键类型结构
delimiter $$
create procedure add_reference_any_type_struct(
  in tab_name varchar(128),
  in typ_comm varchar(100)
)
begin
  set @after = concat(
    'alter table', tab_name,
    'add `typ` int default 0 comment \'外键类型描述符 type, 用于描述: ',
    typ_comm, '\',',
    'add index(`typ`),',
    'add `ari` bigint unsigned comment \'任意外键 any reference id\',',
    'add index(`ari`);');
  set @statement = concat(@after);
  prepare state
    from @statement;

  set @tbl_exist = (select count(1)
                    from information_schema.tables
                    where table_schema = (select database())
                      and table_name = tab_name);
  set @col_exists = (select count(1)
                     from information_schema.columns
                     where table_schema = (select database())
                       and table_name = tab_name
                       and column_name in ('ari', 'typ'));
  if ((@tbl_exist) > 0 and (@col_exists) <= 0) then
    execute state;
  end if;
end $$
delimiter ;
