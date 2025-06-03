create
    or replace function ct_idx(
        tab_name varchar(128),
        col_name varchar(255)
    ) returns void as $$ declare existing_column_name text;

begin select
    column_name into
        existing_column_name
    from
        information_schema.columns
    where
        table_name = tab_name
        and column_name = col_name;

if existing_column_name is not null then execute format(
    'create index if not exists %I_idx on %I (%I);',
    col_name,
    tab_name,
    col_name
);
end if;
end;

$$ language plpgsql;
