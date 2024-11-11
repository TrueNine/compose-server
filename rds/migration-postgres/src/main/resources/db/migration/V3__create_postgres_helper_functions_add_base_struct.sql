create
    or replace function add_base_struct(
        tab_name varchar(128)
    ) returns void as $$ declare existing_column_name text;

begin select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'id';

if existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists id bigint not null;',
    tab_name
);

execute format(
    'alter table if exists %I add primary key (id);',
    tab_name
);
end if;

select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'rlv';

if existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists rlv bigint default 0;',
    tab_name
);
end if;

select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'crd';

if existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists crd timestamp default now();',
    tab_name
);
end if;

select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'mrd';

if existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists mrd timestamp default now();',
    tab_name
);
end if;

select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'ldf';

if existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists ldf bool default null;',
    tab_name
);
end if;
end;

$$ language plpgsql;
