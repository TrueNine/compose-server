create
or replace function base_struct_to_jimmer_style(
    tab_name varchar(128)
) returns void as
$$
declare
existing_column_name text;
    ldf_data_type
text;
    ldf_column_default
text;
    rlv_data_type
text;
    rlv_column_default
text;

begin
select column_name,
       data_type,
       column_default
into
    existing_column_name,
    rlv_data_type,
    rlv_column_default
from information_schema.columns
where table_name = tab_name
  and column_name = 'rlv';

if
existing_column_name is not null then
        if rlv_data_type != 'integer' then
            if rlv_column_default is not null then
                execute format(
                    'alter table if exists %I alter column rlv drop default;',
                    tab_name
                        );
end if;

execute format(
    'alter table if exists %I alter column rlv type int using rlv::int;',
    tab_name
        );
end if;

        if
rlv_column_default is null or rlv_column_default != '0' then
            execute format(
                'alter table if exists %I alter column rlv set default 0;',
                tab_name
                    );
end if;

        -- ensure rlv column is NOT NULL as per README requirements
execute format(
    'alter table if exists %I alter column rlv set not null;',
    tab_name
        );
end if;

select column_name,
       data_type,
       column_default
into
    existing_column_name,
    ldf_data_type,
    ldf_column_default
from information_schema.columns
where table_name = tab_name
  and column_name = 'ldf';

if
existing_column_name is not null then
        if ldf_data_type = 'boolean' then
            if ldf_column_default is not null then
                execute format(
                    'alter table if exists %I alter column ldf drop default;',
                    tab_name
                        );
end if;

select data_type
into
    ldf_data_type
from information_schema.columns
where table_name = tab_name
  and column_name = 'ldf';

if
ldf_data_type != 'timestamp without time zone' then
                execute format(
                    'alter table if exists %I alter column ldf type timestamp using (case when ldf = true then now() else null end);',
                    tab_name
                        );
end if;

select column_default
into
    ldf_column_default
from information_schema.columns
where table_name = tab_name
  and column_name = 'ldf';

if
ldf_column_default is not null then
                execute format(
                    'alter table if exists %I alter column ldf set default null;',
                    tab_name
                        );
end if;

        elsif
ldf_data_type = 'integer' then
            if ldf_column_default is not null then
                execute format(
                    'alter table if exists %I alter column ldf drop default;',
                    tab_name
                        );
end if;

select data_type
into
    ldf_data_type
from information_schema.columns
where table_name = tab_name
  and column_name = 'ldf';

if
ldf_data_type != 'timestamp without time zone' then
                execute format(
                    'alter table if exists %I alter column ldf type timestamp using null;',
                    tab_name
                        );
end if;

select column_default
into
    ldf_column_default
from information_schema.columns
where table_name = tab_name
  and column_name = 'ldf';

if
ldf_column_default is not null then
                execute format(
                    'alter table if exists %I alter column ldf set default null;',
                    tab_name
                        );
end if;
else
            if ldf_column_default is not null then
                execute format(
                    'alter table if exists %I alter column ldf drop default;',
                    tab_name
                        );
end if;

            if
ldf_data_type != 'timestamp without time zone' then
                execute format(
                    'alter table if exists %I alter column ldf type timestamp using ldf::timestamp;',
                    tab_name
                        );
end if;

select column_default
into
    ldf_column_default
from information_schema.columns
where table_name = tab_name
  and column_name = 'ldf';

if
ldf_column_default is not null then
                execute format(
                    'alter table if exists %I alter column ldf set default null;',
                    tab_name
                        );
end if;
end if;
end if;
end;

$$
language plpgsql;
