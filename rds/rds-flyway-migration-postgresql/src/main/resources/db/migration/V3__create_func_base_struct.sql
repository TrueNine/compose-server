create
or replace function add_base_struct(
    tab_name varchar(128)
) returns void as
$$
declare
column_definitions text[][] := array [[ 'id',
        'bigint not null',
        'add primary key (id)' ],
        [ 'rlv',
            'int default 0 not null',
            null ],
        [ 'crd',
            'timestamp default current_timestamp',
            null ],
        [ 'mrd',
            'timestamp default null',
            null ],
        [ 'ldf',
            'timestamp default null',
            null ]];
    existing_column_name
text;
    column_definition
text[];
    row_count
bigint;
    has_id_column
boolean;

begin
    -- check if table exists
    if
not exists (select 1 from information_schema.tables where table_name = tab_name) then
        raise exception 'Table % does not exist', tab_name;
end if;

    -- 检查是否存在 id 字段
select count(1) > 0
into has_id_column
from information_schema.columns
where table_name = tab_name
  and column_name = 'id';

-- 检查表数据量
execute format('select count(*) from %I', tab_name) into row_count;

-- 处理 id 字段添加
if
not has_id_column then
        if row_count > 0 then
            -- 对于有数据的表：使用临时序列为现有数据生成 ID
            execute 'create sequence if not exists temp_seq start 1';

            -- 增加 id 字段并用 sequence 填充
execute format(
    'alter table if exists %I add column if not exists id bigint default nextval(''temp_seq'')',
    tab_name
        );

execute format(
    'update %I set id = nextval(''temp_seq'') where id is null',
    tab_name
        );

execute format(
    'alter table if exists %I alter column id set not null',
    tab_name
        );

execute format(
    'alter table if exists %I add primary key (id)',
    tab_name
        );

-- 删除临时 sequence
execute 'drop sequence if exists temp_seq cascade';
else
            -- 对于空表：直接添加无默认值的 id 字段
            execute format(
                'alter table if exists %I add column if not exists id bigint not null primary key',
                tab_name
                    );
end if;
end if;

    foreach
column_definition slice 1 in array column_definitions
        loop
select column_name
into
    existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = column_definition[1];

if
existing_column_name is null then
                execute format(
                    'alter table if exists %I add column if not exists %I %s;',
                    tab_name,
                    column_definition[1],
                    column_definition[2]
                        );

                if
column_definition[3] is not null then
                    execute format(
                        'alter table if exists %I %s;',
                        tab_name,
                        column_definition[3]
                            );
end if;
end if;
end loop;
end;

$$
language plpgsql;

create
or replace function rm_base_struct(
    tab_name varchar(128)
) returns void as
$$
declare
existing_column_name text;
    pk_constraint_name
text;
    column_to_drop
text;

begin
    -- check if table exists
    if
not exists (select 1 from information_schema.tables where table_name = tab_name) then
        raise exception 'Table % does not exist', tab_name;
end if;

    -- remove columns in reverse order: ldf, mrd, crd, rlv, id
    foreach
column_to_drop in array array ['ldf', 'mrd', 'crd', 'rlv']
        loop
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = column_to_drop;

if
existing_column_name is not null then
                execute format(
                    'alter table if exists %I drop column if exists %I;',
                    tab_name,
                    column_to_drop
                        );
end if;
end loop;

    -- handle primary key constraint and id column separately
select constraint_name
into pk_constraint_name
from information_schema.table_constraints
where table_name = tab_name
  and constraint_type = 'PRIMARY KEY';

if
pk_constraint_name is not null then
        execute format(
            'alter table if exists %I drop constraint if exists %I;',
            tab_name,
            pk_constraint_name
                );
end if;

    -- remove id column
select column_name
into existing_column_name
from information_schema.columns
where table_name = tab_name
  and column_name = 'id';

if
existing_column_name is not null then
        execute format(
            'alter table if exists %I drop column if exists %I;',
            tab_name,
            'id'
                );
end if;
end;

$$
language plpgsql;

create
or replace function all_to_nullable(
    tab_name varchar(128)
) returns void as
$$
declare
column_info record;

begin
for column_info in
select c.column_name,
       c.data_type,
       c.is_nullable,
       c.column_default
from information_schema.columns c
where c.table_name = tab_name
  and c.column_name not in (select kcu.column_name
                            from information_schema.table_constraints tc
                                     join information_schema.key_column_usage kcu on
                                tc.constraint_name = kcu.constraint_name
                            where tc.table_name = tab_name
                              and tc.constraint_type = 'PRIMARY KEY')
    loop
            if column_info.is_nullable = 'NO'
                or column_info.column_default is not null then
                execute format(
                    'alter table %I alter column %I drop default, alter column %I drop not null;',
                    tab_name,
                    column_info.column_name,
                    column_info.column_name
                        );
end if;
end loop;
end;

$$
language plpgsql;
