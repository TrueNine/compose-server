create
    or replace function base_struct_to_jimmer_style(
        tab_name varchar(128)
    ) returns void as $$ declare existing_column_name text;

ldf_data_type text;

ldf_column_default text;

rlv_data_type text;

rlv_column_default text;

begin -- rlv 字段处理
select
    column_name,
    data_type,
    column_default into
        existing_column_name,
        rlv_data_type,
        rlv_column_default
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'rlv';

if existing_column_name is not null then -- 仅当类型不是 int 时才转换
if rlv_data_type != 'integer' then execute format(
    'alter table if exists %I alter column rlv type int using rlv::int;',
    tab_name
);
end if;

-- 仅当 default 不为 0 时才设置
if rlv_column_default is null
or rlv_column_default != '0' then execute format(
    'alter table if exists %I alter column rlv set default 0;',
    tab_name
);
end if;
end if;

-- ldf 字段处理
select
    column_name,
    data_type,
    column_default into
        existing_column_name,
        ldf_data_type,
        ldf_column_default
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if existing_column_name is not null then if ldf_data_type = 'boolean' then -- 仅当 default 存在时才 drop
if ldf_column_default is not null then execute format(
    'alter table if exists %I alter column ldf drop default;',
    tab_name
);
end if;

select
    data_type into
        ldf_data_type
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if ldf_data_type != 'timestamp without time zone' then execute format(
    'alter table if exists %I alter column ldf type timestamp using (case when ldf = true then now() else null end);',
    tab_name
);
end if;

select
    column_default into
        ldf_column_default
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if ldf_column_default is not null then execute format(
    'alter table if exists %I alter column ldf set default null;',
    tab_name
);
end if;

elsif ldf_data_type = 'integer' then -- integer 类型全部置为 null，避免类型转换报错
if ldf_column_default is not null then execute format(
    'alter table if exists %I alter column ldf drop default;',
    tab_name
);
end if;

select
    data_type into
        ldf_data_type
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if ldf_data_type != 'timestamp without time zone' then execute format(
    'alter table if exists %I alter column ldf type timestamp using null;',
    tab_name
);
end if;

select
    column_default into
        ldf_column_default
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if ldf_column_default is not null then execute format(
    'alter table if exists %I alter column ldf set default null;',
    tab_name
);
end if;
else -- 其他类型
if ldf_column_default is not null then execute format(
    'alter table if exists %I alter column ldf drop default;',
    tab_name
);
end if;

if ldf_data_type != 'timestamp without time zone' then execute format(
    'alter table if exists %I alter column ldf type timestamp using ldf::timestamp;',
    tab_name
);
end if;

select
    column_default into
        ldf_column_default
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if ldf_column_default is not null then execute format(
    'alter table if exists %I alter column ldf set default null;',
    tab_name
);
end if;
end if;
end if;
end;

$$ language plpgsql;