do $$ begin if exists(
    select
        1
    from
        information_schema.tables as t
    where
        t.table_name = 'usr'
)
and not exists(
    select
        1
    from
        information_schema.tables as t
    where
        t.table_name = 'user_account'
) then alter table
    if exists usr rename to user_account;
end if;
end $$;
