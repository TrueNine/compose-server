create
or replace function add_tree_struct(
    tab_name varchar(128)
) returns void as
$$
declare
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
end
$$
language plpgsql;

create
or replace function rm_tree_struct(
    tab_name varchar(128)
) returns void as
$$
declare
existing_column_name text;

begin
    -- check if table exists
    if
not exists (select 1 from information_schema.tables where table_name = tab_name) then
        raise exception 'Table % does not exist', tab_name;
end if;

    -- remove rpi column
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'rpi';

if
existing_column_name is not null then
        execute format(
            'alter table if exists %I drop column if exists %I;',
            tab_name,
            'rpi'
                );
end if;
end
$$
language plpgsql;
