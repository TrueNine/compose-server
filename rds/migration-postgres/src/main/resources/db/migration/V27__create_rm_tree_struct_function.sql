create
    or replace function rm_presort_tree_struct(
        tab_name varchar(128)
    ) returns void as $$ declare existing_column_name text;

foreach existing_column_name in array array [ 'rln',
'rrn',
'tgi',
'nlv' ] loop select
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
