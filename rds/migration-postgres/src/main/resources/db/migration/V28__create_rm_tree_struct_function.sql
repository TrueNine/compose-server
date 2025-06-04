create
    or replace function rm_presort_tree_struct(
        tab_name varchar(128)
    ) returns void as $$ declare column_to_drop text;

existing_column_name text;

begin foreach column_to_drop in array array [ 'rln',
'rrn',
'tgi',
'nlv' ] loop select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = column_to_drop;

if existing_column_name is not null then execute format(
    'alter table if exists %I drop column if exists %I;',
    tab_name,
    column_to_drop
);
end if;
end loop;
end;

$$ language plpgsql;
