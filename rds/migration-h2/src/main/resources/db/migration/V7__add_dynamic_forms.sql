create
    table
        if not exists dynamic_form(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            dynamic_form_group_id bigint not null,
            version varchar(5) not null,
            value_type varchar(127) not null,
            component_type varchar(127) not null,
            index int not null,
            label varchar(127) default null,
            placeholder varchar(127) default null,
            group_index int default null,
            readonly boolean default null,
            required boolean default null,
            description text default null,
            default_value json default null,
            options json default null,
            rules json default null
        );

alter table
    if exists dynamic_form add column if not exists interactive boolean default null;

create
    table
        if not exists dynamic_form_group(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar(127) unique not null,
            doc text default null
        );

create
    table
        if not exists dynamic_form_value(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            form_group_id bigint not null,
            api_for varchar(127) default null,
            platform_for varchar(127) default null,
            input_user_id bigint default null,
            input_user_info_id bigint default null,
            input_datetime bigint not null,
            audit_id bigint default null,
            json_values json default null
        );
