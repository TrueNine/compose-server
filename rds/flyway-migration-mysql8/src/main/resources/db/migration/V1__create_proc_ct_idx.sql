-- Create index procedure for MySQL
delimiter
$$

drop procedure if exists ct_idx$$
create procedure ct_idx(
    in tab_name varchar (128),
    in col_name varchar (255)
)
begin
    declare
col_count int default 0;
    declare
idx_count int default 0;
    declare
sql_stmt text;

    -- check if column exists
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = col_name;

if
col_count > 0 then
        -- check if index already exists
        set @index_name = concat(col_name, '_idx');
select count(*)
into idx_count
from information_schema.statistics
where table_schema = database()
  and table_name = tab_name
  and index_name = @index_name;

if
idx_count = 0 then
            set sql_stmt = concat('create index ', @index_name, ' on ', tab_name, ' (', col_name, ')');
            set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;
end if;
end$$

delimiter ;
