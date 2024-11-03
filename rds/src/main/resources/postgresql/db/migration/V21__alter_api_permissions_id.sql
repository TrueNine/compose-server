do $$ begin if exists(
    select
        1
    from
        information_schema.columns
    where
        table_name = 'api'
        and column_name = 'permissions_id'
) then alter table
    if exists api alter column permissions_id type bigint,
    alter column permissions_id
set
    default null;
end if;
end $$;
