-- Base structure procedures for MySQL
delimiter
$$

drop procedure if exists add_base_struct$$
create procedure add_base_struct(
    in tab_name varchar(128)
)
begin
    declare
        col_count int default 0;
        declare
        sql_stmt  text;
        declare
        has_data  boolean default false;
        declare
        continue  handler for sqlexception
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

-- Add id column if not exists
    select count(*)
    into col_count
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'id';

    if
        col_count = 0 then
        -- Check if table has existing data
        set @row_count = 0;
        set
            sql_stmt = concat
            ('select count(*) into @row_count from ', tab_name);
        set @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;

        set
            has_data = (@row_count > 0);

        if
            has_data then
            -- Table has existing data: use AUTO_INCREMENT temporarily to fill IDs
            set sql_stmt = concat('alter table ', tab_name, ' add column id bigint not null auto_increment primary key first');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;

-- Remove AUTO_INCREMENT attribute - final field should have no auto-increment
            set
                sql_stmt = concat
                ('alter table ', tab_name, ' modify column id bigint not null');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        else
            -- Table is empty: directly add non-auto-increment id field
            set sql_stmt = concat('alter table ', tab_name, ' add column id bigint not null primary key first');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        end if;
    end if;

    -- Add rlv column (Row Lock Version) - MUST NOT be NULL
    select count(*)
    into col_count
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'rlv';

    if
        col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column rlv int default 0 not null');
        set @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
    end if;

    -- Add crd column (Created Row Datetime)
    select count(*)
    into col_count
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'crd';

    if
        col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column crd timestamp default current_timestamp');
        set @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
    end if;

    -- Add mrd column (Modify Row Datetime) - should default to NULL
    select count(*)
    into col_count
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'mrd';

    if
        col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column mrd timestamp default null');
        set @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
    end if;

    -- Add ldf column (Logic Delete Flag)
    select count(*)
    into col_count
    from information_schema.columns
    where table_schema = database()
      and table_name = tab_name
      and column_name = 'ldf';

    if
        col_count = 0 then
        set sql_stmt = concat('alter table ', tab_name, ' add column ldf timestamp null default null');
        set @sql = sql_stmt;
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
    end if;
    commit;
    end$$

    drop procedure if exists rm_base_struct$$
    create procedure rm_base_struct(
        in tab_name varchar(128)
    )
    begin
        declare
            col_count            int default 0;
            declare
            sql_stmt             text;
            declare
            pk_constraint_exists int default 0;
            declare
            continue             handler for sqlexception
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

        -- Remove columns in reverse order (ldf, mrd, crd, rlv, id)

-- Remove ldf column
        select count(*)
        into col_count
        from information_schema.columns
        where table_schema = database()
          and table_name = tab_name
          and column_name = 'ldf';

        if
            col_count > 0 then
            set sql_stmt = concat('alter table ', tab_name, ' drop column ldf');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        end if;

        -- Remove mrd column
        select count(*)
        into col_count
        from information_schema.columns
        where table_schema = database()
          and table_name = tab_name
          and column_name = 'mrd';

        if
            col_count > 0 then
            set sql_stmt = concat('alter table ', tab_name, ' drop column mrd');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        end if;

        -- Remove crd column
        select count(*)
        into col_count
        from information_schema.columns
        where table_schema = database()
          and table_name = tab_name
          and column_name = 'crd';

        if
            col_count > 0 then
            set sql_stmt = concat('alter table ', tab_name, ' drop column crd');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        end if;

        -- Remove rlv column
        select count(*)
        into col_count
        from information_schema.columns
        where table_schema = database()
          and table_name = tab_name
          and column_name = 'rlv';

        if
            col_count > 0 then
            set sql_stmt = concat('alter table ', tab_name, ' drop column rlv');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        end if;

        -- Check if primary key constraint exists before dropping
        select count(*)
        into pk_constraint_exists
        from information_schema.table_constraints
        where table_schema = database()
          and table_name = tab_name
          and constraint_type = 'PRIMARY KEY';

-- Remove id column and primary key constraint
        select count(*)
        into col_count
        from information_schema.columns
        where table_schema = database()
          and table_name = tab_name
          and column_name = 'id';

        if
            col_count > 0 then
            if pk_constraint_exists > 0 then
                set sql_stmt = concat('alter table ', tab_name, ' drop primary key');
                set @sql = sql_stmt;
                prepare stmt from @sql;
                execute stmt;
                deallocate prepare stmt;
            end if;

            set sql_stmt = concat('alter table ', tab_name, ' drop column id');
            set @sql = sql_stmt;
            prepare stmt from @sql;
            execute stmt;
            deallocate prepare stmt;
        end if;
        commit;
        end$$
delimiter ;
