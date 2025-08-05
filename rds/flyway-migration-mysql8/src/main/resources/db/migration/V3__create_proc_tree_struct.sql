-- Tree structure procedures for MySQL
delimiter
$$

drop procedure if exists add_tree_struct$$
create procedure add_tree_struct(
    in tab_name varchar(128)
)
begin
    declare
        col_count  int default 0;
        declare
        sql_stmt   text;

        -- add rpi column if not exists
        select     count(*) into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'rpi';
        if
                   col_count = 0 then
        set        sql_stmt  = concat('alter table ', tab_name, ' add column rpi bigint default null');
        set @sql             = sql_stmt;
        prepare    stmt from @sql;
        execute    stmt;
        deallocate prepare stmt;
        end        if;

        -- create index for rpi column
        call       ct_idx(tab_name, 'rpi');
        end$$
                   drop procedure if exists rm_tree_struct$$
        create     procedure rm_tree_struct(
        in         tab_name varchar (128)
        )
    begin
        declare
        col_count int default 0;
        declare
        sql_stmt text;

        -- remove rpi column if exists
        select count(*) into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'rpi';

        if
            col_count > 0 then
        set sql_stmt = concat('alter table ', tab_name, ' drop column rpi');
        set @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
        end if;
        end$$
            delimiter ;
