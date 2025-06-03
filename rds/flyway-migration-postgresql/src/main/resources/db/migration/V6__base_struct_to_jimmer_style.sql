create
    or replace function base_struct_to_jimmer_style(
        tab_name varchar(128)
    ) returns void as $$ declare existing_column_name text;

begin -- 处理 rlv 字段
select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'rlv';

if existing_column_name is not null then execute format(
    'alter table if exists %I alter column rlv type int using rlv::int, alter column rlv set default 0;',
    tab_name
);
end if;

-- 处理 ldf 字段
select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if existing_column_name is not null then execute format(
    'alter table if exists %I alter column ldf drop default;',
    tab_name
);

execute format(
    'alter table if exists %I alter column ldf type timestamp using ldf::timestamp;',
    tab_name
);

execute format(
    'alter table if exists %I alter column ldf set default null;',
    tab_name
);
end if;
end;

$$ language plpgsql;