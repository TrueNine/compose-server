create
    table
        if not exists cert(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
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

create
    table
        if not exists idcard_2(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint not null,
            user_info_id bigint not null,
            address_details_id bigint default null,
            name varchar(255) not null,
            gender integer default null,
            code varchar(64) not null,
            birthday date default null,
            ethnic_group varchar(127) default null,
            expire_date date default null,
            issue_organ varchar(255) default null
        );

create
    table
        if not exists dis_cert_2(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint default null,
            user_info_id bigint default null,
            name varchar(255) not null,
            gender integer default null,
            code varchar(64) default null,
            type integer not null,
            level integer not null,
            issue_date date default null,
            expire_time date default null,
            address_details_id bigint default null,
            guardian varchar(255) default null,
            guardian_phone varchar(127) default null,
            birthday date default null
        );

create
    table
        if not exists household_cert(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint default null,
            user_info_id bigint default null,
            household_type integer default null,
            household_primary_name varchar(255) default null,
            code varchar(255) default null,
            address_details_id bigint default null,
            issue_organ varchar(255) default null,
            name varchar(255) not null,
            old_name varchar(255) default null,
            relationship integer default null,
            gender integer default null,
            ethnic_group varchar(127) default null,
            birthday date default null,
            height decimal(
                4,
                2
            ) default null,
            blood_type integer default null,
            place_birth_address_details_id bigint default null,
            origin_address_details_id bigint default null,
            idcard_code varchar(255) not null,
            education_level integer default null,
            occupation varchar(255) default null,
            military_service_status varchar(255) default null,
            service_address_details_id bigint default null,
            issue_date date default null
        );

create
    table
        if not exists bank_card(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint default null,
            user_info_id bigint default null,
            code varchar(255) not null,
            country varchar(255) default null,
            bank_group integer default null,
            bank_type integer default null,
            reserve_phone varchar(255) default null,
            issue_address_details text default null
        );

create
    table
        if not exists biz_cert(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint default null,
            user_info_id bigint default null,
            title varchar(255) not null,
            reg_capital decimal(
                20,
                2
            ) default null,
            create_date date default null,
            uni_credit_code varchar(255) default null,
            type varchar(127) default null,
            leader_name varchar(255) default null,
            biz_range text default null,
            address_code varchar(255) default null,
            address_details_id bigint default null,
            issue_date date default null
        );
