create
    or replace function add_base_struct(
        tab_name varchar(128)
    ) returns void as $$ declare column_definitions text [][]:= array [[ 'id',
    'bigint not null',
    'add primary key (id)' ],
    [ 'rlv',
    'int default 0',
    null ],
    [ 'crd',
    'timestamp default now()',
    null ],
    [ 'mrd',
    'timestamp default now()',
    null ],
    [ 'ldf',
    'timestamp default null',
    null ]];

existing_column_name text;

column_definition text [];

begin foreach column_definition slice 1 in array column_definitions loop select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = column_definition [ 1 ];

if existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists %I %s;',
    tab_name,
    column_definition [ 1 ],
    column_definition [ 2 ]
);

if column_definition [ 3 ] is not null then execute format(
    'alter table if exists %I %s;',
    tab_name,
    column_definition [ 3 ]
);
end if;
end if;
end loop;
end;

$$ language plpgsql;

create
    or replace function rm_base_struct(
        tab_name varchar(128)
    ) returns void as $$ declare existing_column_name text;

pk_constraint_name text;

begin select
    constraint_name into
        pk_constraint_name
    from
        information_schema.table_constraints
    where
        table_name = tab_name
        and constraint_type = 'PRIMARY KEY';

if pk_constraint_name is not null then execute format(
    'alter table if exists %I drop constraint if exists %I;',
    tab_name,
    pk_constraint_name
);
end if;

foreach existing_column_name in array array [ 'id',
'rlv',
'crd',
'mrd',
'ldf' ] loop select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = existing_column_name;

if existing_column_name is not null then execute format(
    'alter table if exists %I drop column if exists %I;',
    tab_name,
    existing_column_name
);
end if;
end loop;
end;

$$ language plpgsql;

create
    or replace function all_to_nullable(
        tab_name varchar(128)
    ) returns void as $$ declare column_info record;

begin for column_info in select
    c.column_name,
    c.data_type,
    c.is_nullable,
    c.column_default
from
    information_schema.columns c
where
    c.table_name = tab_name
    and c.column_name not in(
        select
            kcu.column_name
        from
            information_schema.table_constraints tc join information_schema.key_column_usage kcu on
            tc.constraint_name = kcu.constraint_name
        where
            tc.table_name = tab_name
            and tc.constraint_type = 'PRIMARY KEY'
    ) loop if column_info.is_nullable = 'NO'
    or column_info.column_default is not null then execute format(
        'alter table %I alter column %I drop default, alter column %I drop not null;',
        tab_name,
        column_info.column_name,
        column_info.column_name
    );
end if;
end loop;
end;

$$ language plpgsql;
