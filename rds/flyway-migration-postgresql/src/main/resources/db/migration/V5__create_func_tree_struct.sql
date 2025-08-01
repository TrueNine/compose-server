create
or replace function add_tree_struct(
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
end $$
language plpgsql;
