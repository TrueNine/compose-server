-- Presort tree structure procedures for MySQL
delimiter
$$

drop procedure if exists add_presort_tree_struct$$
create procedure add_presort_tree_struct(
  in tab_name varchar(128)
)
begin
  declare
    col_count int default 0;
  declare
    sql_stmt text;

  -- add rpi column if not exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rpi';
  if
    col_count = 0 then
    set sql_stmt = concat('alter table ', tab_name, ' add column rpi bigint default null');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- add rln column if not exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rln';
  if
    col_count = 0 then
    set sql_stmt = concat('alter table ', tab_name, ' add column rln bigint default 1');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- add rrn column if not exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rrn';
  if
    col_count = 0 then
    set sql_stmt = concat('alter table ', tab_name, ' add column rrn bigint default 2');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- add nlv column (Node Level) if not exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'nlv';
  if
    col_count = 0 then
    set sql_stmt = concat('alter table ', tab_name, ' add column nlv int default 0');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- add tgi column (Tree Group Id) if not exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'tgi';
  if
    col_count = 0 then
    set sql_stmt = concat('alter table ', tab_name, ' add column tgi varchar(255) default null');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- create indexes for all columns
  call ct_idx(tab_name, 'rpi');
  call ct_idx(tab_name, 'rln');
  call ct_idx(tab_name, 'rrn');
  call ct_idx(tab_name, 'nlv');
  call ct_idx(tab_name, 'tgi');
end$$
drop procedure if exists rm_presort_tree_struct$$
create procedure rm_presort_tree_struct(
  in tab_name varchar(128)
)
begin
  declare
    col_count int default 0;
  declare
    sql_stmt text;

  -- remove tgi column if exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'tgi';

  if
    col_count > 0 then
    set sql_stmt = concat('alter table ', tab_name, ' drop column tgi');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- remove nlv column if exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'nlv';

  if
    col_count > 0 then
    set sql_stmt = concat('alter table ', tab_name, ' drop column nlv');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- remove rrn column if exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rrn';

  if
    col_count > 0 then
    set sql_stmt = concat('alter table ', tab_name, ' drop column rrn');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- remove rln column if exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rln';

  if
    col_count > 0 then
    set sql_stmt = concat('alter table ', tab_name, ' drop column rln');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;

  -- remove rpi column if exists
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rpi';

  if
    col_count > 0 then
    set sql_stmt = concat('alter table ', tab_name, ' drop column rpi');
    set
      @sql = sql_stmt;
    prepare stmt from @sql;
    execute stmt;
    deallocate prepare stmt;
  end if;
end$$
delimiter ;
