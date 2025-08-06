-- all to nullable procedure for mysql
delimiter
$$

drop procedure if exists all_to_nullable$$
create procedure all_to_nullable(
    in tab_name varchar (128)
)
begin
    declare
done int default false;
    declare
col_name varchar(255);
    declare
col_type varchar(255);
    declare
sql_stmt text;
    declare
cur cursor for
select column_name, column_type
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name not in (select column_name
                          from information_schema.key_column_usage
                          where table_schema = database()
                            and table_name = tab_name
                            and constraint_name = 'PRIMARY');
declare
continue handler for not found set done = true;

open cur;

read_loop
:
    loop
        fetch cur into col_name, col_type;
        if
done then
            leave read_loop;
end if;

        -- modify column to be nullable
        set
sql_stmt = concat('alter table ', tab_name, ' modify column ', col_name, ' ', col_type, ' null');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end loop;

close cur;
end$$
delimiter ;
