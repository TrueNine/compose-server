create
    table
        if not exists api(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar(128) default null,
            doc varchar(128) default null,
            permissions_id bigint default 3,
            api_path text,
            api_method text,
            api_protocol varchar(63) default null
        );

comment on
table
    api is 'api';

create
    table
        if not exists api_call_record(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            api_id bigint not null,
            device_code text null,
            req_ip varchar(63) null,
            login_ip varchar(63) null,
            resp_code int null,
            resp_result_enc text
        );

create
    table
        if not exists attachment(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            meta_name varchar(127) default null,
            save_name varchar(127),
            base_url varchar(255),
            base_uri varchar(255),
            url_name varchar(127) default null,
            url_doc varchar(255),
            url_id bigint null,
            att_type int not null,
            size bigint default null,
            mime_type varchar(63)
        );

comment on
table
    attachment is '文件';

create
    table
        if not exists address(
            rpi bigint default null,
            rln bigint default 1,
            rrn bigint default 2,
            nlv bigint default 0,
            tgi varchar(64) default '0',
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            code varchar(255) null,
            name varchar(127) null,
            year_version varchar(15) null,
            level integer default 0,
            center varchar(255) null,
            leaf boolean default false,
            unique(code)
        );

insert
    into
        address(
            id,
            level,
            code,
            name,
            rln,
            rrn,
            tgi,
            center
        ) select
            *
        from
            (
            values(
                0,
                0,
                '000000000000',
                '',
                1,
                2,
                0,
                null
            )
            ) as tmp(
                id,
                level,
                code,
                name,
                rln,
                rrn,
                tgi,
                center
            )
        where
            not exists(
                select
                    1
                from
                    address a
                where
                    a.id = tmp.id
            );

create
    table
        if not exists address_details(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            address_id bigint not null,
            user_id bigint null,
            phone varchar(127),
            name varchar(255),
            address_code varchar(31) not null,
            address_details text not null,
            center varchar(255)
        );

comment on
table
    address_details is '地址详情';

create
    table
        if not exists table_row_delete_record(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            table_names varchar(127) null,
            user_id bigint null,
            user_account varchar(255) null,
            delete_datetime timestamp default now(),
            entity json not null
        );

create
    table
        if not exists table_row_change_record(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            type boolean not null,
            table_names varchar(127) null,
            create_user_id bigint null,
            create_user_account char(255) null,
            create_datetime timestamp null,
            create_entity json null,
            last_modify_user_id bigint null,
            last_modify_user_account char(255) null,
            last_modify_datetime timestamp null,
            last_modify_entity json not null
        );
