drop
    cast(
        bigint as varchar
    );

create
    cast(
        bigint as varchar
    ) with inout;

drop
    cast(
        varchar as bigint
    );

create
    cast(
        varchar as bigint
    ) with inout;

create
    or replace function add_presort_tree_struct(
        tab_name varchar(128)
    ) returns void as $$ declare existing_column_name text;

begin select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = 'rpi';

if existing_column_name is null then execute format(
    'alter table %I add column rpi bigint default null;',
    tab_name
);

execute format(
    'create index on %I(rpi);',
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
        and column_name = 'rln';

if existing_column_name is null then execute format(
    'alter table %I add column rln bigint default 1;',
    tab_name
);

execute format(
    'create index on %I(rln);',
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
        and column_name = 'rrn';

if existing_column_name is null then execute format(
    'alter table %I add column rrn bigint default 2;',
    tab_name
);

execute format(
    'create index on %I(rrn);',
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
        and column_name = 'nlv';

if existing_column_name is null then execute format(
    'alter table %I add column nlv bigint default 0;',
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
        and column_name = 'tgi';

if existing_column_name is null then execute format(
    'alter table %I add column tgi varchar(64) default ''0''::varchar;',
    tab_name
);

execute format(
    'create index on %I(tgi);',
    tab_name
);
end if;
end $$ language plpgsql;

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
    'alter table %I add column id bigint not null;',
    tab_name
);

execute format(
    'alter table %I add primary key (id);',
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
    'alter table %I add column rlv bigint default 0;',
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
    'alter table %I add column crd timestamp default now();',
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
    'alter table %I add column mrd timestamp default now();',
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
    'alter table %I add column ldf bool default null;',
    tab_name
);
end if;
end;

$$ language plpgsql;
