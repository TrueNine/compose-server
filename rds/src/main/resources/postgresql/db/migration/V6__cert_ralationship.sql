create
    table
        if not exists cert(
            wm_code varchar(255) not null,
            do_type integer not null,
            co_type integer not null,
            po_type integer not null,
            user_id bigint default null,
            user_info_id bigint default null,
            name varchar(255) default null,
            audit_status integer default null,
            doc varchar(255) default null,
            remark text default null,
            create_datetime timestamp default now(),
            create_ip varchar(255) default null,
            create_device_id varchar(1023) default null,
            create_user_id bigint not null,
            att_id bigint not null,
            wm_att_id bigint not null
        );

comment on
table
    cert is '用户 标记用户  证件';

select
    add_base_struct('cert');

create
    index on
    cert(user_id);

create
    index on
    cert(user_info_id);

create
    index on
    cert(create_user_id);

create
    index on
    cert(att_id);

create
    index on
    cert(wm_att_id);
