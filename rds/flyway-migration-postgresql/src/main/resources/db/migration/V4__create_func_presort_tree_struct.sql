create
or replace function add_presort_tree_struct(
        tab_name varchar(128)
    ) returns void as $$ declare
existing_column_name text;

begin
    -- check if table exists
    if
not exists (select 1 from information_schema.tables where table_name = tab_name) then
        raise exception 'Table % does not exist', tab_name;
end if;

    -- add rpi column (Row Parent Id)
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rpi';

if
existing_column_name is null then
        execute format(
            'alter table if exists %I add column if not exists rpi bigint default null;',
            tab_name
        );
        -- create index using ct_idx function
        perform
ct_idx(tab_name, 'rpi');
end if;

    -- add rln column (Row Left Node)
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rln';

if
existing_column_name is null then
        execute format(
            'alter table if exists %I add column if not exists rln bigint default 1;',
            tab_name
        );
        -- create index using ct_idx function
        perform
ct_idx(tab_name, 'rln');
end if;

    -- add rrn column (Row Right Node)
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rrn';

if
existing_column_name is null then
        execute format(
            'alter table if exists %I add column if not exists rrn bigint default 2;',
            tab_name
        );
        -- create index using ct_idx function
        perform
ct_idx(tab_name, 'rrn');
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

begin
    -- check if table exists
    if
not exists (select 1 from information_schema.tables where table_name = tab_name) then
        raise exception 'Table % does not exist', tab_name;
end if;

    -- remove columns in reverse order: rrn, rln, rpi
    foreach
column_to_drop in array array['rrn', 'rln', 'rpi'] loop
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = column_to_drop;

if
existing_column_name is not null then
            execute format(
                'alter table if exists %I drop column if exists %I;',
                tab_name,
                column_to_drop
            );
end if;
end loop;
end;

$$
language plpgsql;
