create
    table
        if not exists audit(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            status integer default 0,
            create_datetime timestamp default now(),
            audit_user_id bigint default null,
            ref_id bigint default null,
            ref_type integer default null,
            audit_ip varchar(64) default null,
            audit_device_id varchar(255) default null,
            remark text default null
        );

create
    table
        if not exists audit_attachment(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            att_id bigint not null,
            audit_id bigint not null,
            status int default null
        );
