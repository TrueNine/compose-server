create
or replace function add_presort_tree_struct(
        tab_name varchar(128)
    ) returns void as $$ declare
existing_column_name text;

begin
select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rpi';

if
existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists rpi bigint default null;',
    tab_name
);

execute format(
        'create index if not exists rpi_idx on %I (rpi);',
        tab_name
        );
end if;

select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rln';

if
existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists rln bigint default 1;',
    tab_name
);

execute format(
        'create index if not exists rln_idx on %I (rln);',
        tab_name
        );
end if;

select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rrn';

if
existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists rrn bigint default 2;',
    tab_name
);

execute format(
        'create index if not exists rrn_idx on %I (rrn);',
        tab_name
        );
end if;

select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'nlv';

if
existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists nlv bigint default 0;',
    tab_name
);
end if;

select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'tgi';

if
existing_column_name is null then execute format(
    'alter table if exists %I add column if not exists tgi varchar(64) default ''0''::varchar;',
    tab_name
);

execute format(
        'create index if not exists tgi_idx on %I(tgi);',
        tab_name
        );
end if;
end $$
language plpgsql;

create
or replace function rm_presort_tree_struct(
        tab_name varchar(128)
    ) returns void as $$ declare
column_to_drop text;

existing_column_name
text;

begin foreach
column_to_drop in array array [ 'rln',
'rrn',
'tgi',
'nlv' ] loop
select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = column_to_drop;

if
existing_column_name is not null then execute format(
    'alter table if exists %I drop column if exists %I;',
    tab_name,
    column_to_drop
);
end if;
end loop;
end;

$$
language plpgsql;
