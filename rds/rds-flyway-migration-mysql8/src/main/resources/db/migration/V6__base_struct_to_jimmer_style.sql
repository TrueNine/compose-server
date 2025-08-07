-- Fix base struct to jimmer style procedure for MySQL
delimiter
$$

drop procedure if exists base_struct_to_jimmer_style$$
create procedure base_struct_to_jimmer_style(
  in tab_name varchar(128)
)
begin
  declare
    col_count int default 0;
  declare
    rlv_data_type varchar(255);
  declare
    rlv_column_default varchar(255);
  declare
    ldf_data_type varchar(255);
  declare
    ldf_column_default varchar(255);
  declare
    sql_stmt text;
  declare
    continue handler for sqlexception
    begin
      rollback;
      resignal;
    end;

  -- Check if table exists
  select count(*)
  into col_count
  from information_schema.tables
  where table_schema = database()
    and table_name = tab_name;

  if
    col_count = 0 then
    signal sqlstate '45000' set message_text = 'Table does not exist';
  end if;

  start transaction;

-- Handle rlv column adjustments
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'rlv';

  if
    col_count > 0 then
    -- Get current rlv column information
    select data_type, column_default
    into rlv_data_type, rlv_column_default
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'rlv';

    -- Ensure rlv column is INT type with default 0 and NOT NULL
    if lower(rlv_data_type) != 'int' or rlv_column_default != '0' then
      set sql_stmt = concat('alter table ', tab_name, ' modify column rlv int default 0 not null');
      set
        @sql = sql_stmt;
      prepare stmt from @sql;
      execute stmt;
      deallocate prepare stmt;
    end if;
  end if;

-- Handle ldf column adjustments (convert any type to timestamp)
  select count(*)
  into col_count
  from information_schema.columns
  where table_schema = database()
    and table_name = tab_name
    and column_name = 'ldf';

  if
    col_count > 0 then
    -- Get current ldf column information
    select data_type, column_default
    into ldf_data_type, ldf_column_default
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'ldf';

    -- Only process if not already timestamp
    if lower(ldf_data_type) != 'timestamp' then
      -- Always use column replacement approach to avoid type conversion issues
      -- Add temporary column
      set sql_stmt = concat('alter table ', tab_name, ' add column temp_ldf timestamp null default null');
      set
        @sql = sql_stmt;
      prepare stmt from @sql;
      execute stmt;
      deallocate prepare stmt;

      -- For boolean/tinyint, convert true to current_timestamp, false/null to null
      -- For other types, just set all to null (data will be lost)
      if lower(ldf_data_type) = 'tinyint' then
        set sql_stmt = concat('update ', tab_name,
                              ' set temp_ldf = case when ldf = 1 then current_timestamp else null end');
        set
          @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
      end if;

      -- Drop old column
      set sql_stmt = concat('alter table ', tab_name, ' drop column ldf');
      set
        @sql = sql_stmt;
      prepare stmt from @sql;
      execute stmt;
      deallocate prepare stmt;

      -- Rename temp column to ldf
      set sql_stmt = concat('alter table ', tab_name, ' change column temp_ldf ldf timestamp null default null');
      set
        @sql = sql_stmt;
      prepare stmt from @sql;
      execute stmt;
      deallocate prepare stmt;
    end if;

    -- Ensure ldf has correct default value (null)
    select column_default
    into ldf_column_default
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'ldf';

    if ldf_column_default is not null and ldf_column_default != 'null' then
      set sql_stmt = concat('alter table ', tab_name,
                            ' alter column ldf set default null');
      set
        @sql = sql_stmt;
      prepare stmt from @sql;
      execute stmt;
      deallocate prepare stmt;
    end if;
  end if;
  commit;
end$$
delimiter ;