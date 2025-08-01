-- base structure procedures for mysql
delimiter
$$

drop procedure if exists add_base_struct$$
create procedure add_base_struct(
    in tab_name varchar (128)
)
begin
    declare
col_count int default 0;
    declare
sql_stmt text;

    -- add id column if not exists
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'id';
if
col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column id bigint not null auto_increment primary key first');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

    -- add rlv column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'rlv';
if
col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column rlv int default 0');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

    -- add crd column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'crd';
if
col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column crd timestamp default current_timestamp');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

    -- add mrd column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'mrd';
if
col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column mrd timestamp default current_timestamp on update current_timestamp');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

    -- add ldf column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'ldf';
if
col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column ldf timestamp null default null');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;
end$$

drop procedure if exists rm_base_struct$$
create procedure rm_base_struct(
    in tab_name varchar (128)
)
begin
    declare
col_count int default 0;
    declare
sql_stmt text;

    -- remove columns in reverse order
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'ldf';
if
col_count > 0 then
        set sql_stmt = concat('alter table ', tab_name, ' drop column ldf');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'mrd';
if
col_count > 0 then
        set sql_stmt = concat('alter table ', tab_name, ' drop column mrd');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'crd';
if
col_count > 0 then
        set sql_stmt = concat('alter table ', tab_name, ' drop column crd');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'rlv';
if
col_count > 0 then
        set sql_stmt = concat('alter table ', tab_name, ' drop column rlv');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'id';
if
col_count > 0 then
        set sql_stmt = concat('alter table ', tab_name, ' drop primary key, drop column id');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end if;
end$$

delimiter ;
