do $$ begin if exists(
    select
        1
    from
        information_schema.columns
    where
        table_name = 'dynamic_form_value'
        and column_name = 'values'
) then alter table
    if exists dynamic_form_value rename
values to json_values;

alter table
    if exists dynamic_form_value alter column json_values type json,
    alter column json_values
set
    default null;
end if;
end $$;
