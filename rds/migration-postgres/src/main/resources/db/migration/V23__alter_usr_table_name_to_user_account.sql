do
$$
    begin
        if exists(select 1
                  from information_schema.tables as t
                  where t.table_name = 'usr')
        then
            alter table usr
                rename to user_account;
        end if;
    end
$$;
