create
    table
        if not exists idcard_2(
            user_id bigint default null,
            user_info_id bigint default null,
            address_details_id bigint default null,
            name varchar(255) not null,
            gender integer default null,
            code varchar(64) not null,
            birthday date default null,
            ethnic_group varchar(127) default null,
            expire_date date default null,
            issue_organ varchar(255) default null
        ) default charset = utf8mb4,
        comment '身份证2代';

call add_base_struct('idcard_2');

create
    table
        if not exists dis_cert_2(
            user_id bigint default null,
            user_info_id bigint default null,
            name varchar(255) default null,
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
        ) default charset = utf8mb4,
        comment '残疾证2代';

call add_base_struct('dis_cert_2');

create
    table
        if not exists household_cert(
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
            idcard_code varchar(255) default null,
            education_level integer default null,
            occupation varchar(255) default null,
            military_service_status varchar(255) default null,
            service_address_details_id bigint default null,
            issue_date date default null
        ) default charset = utf8mb4,
        comment '户口登记卡';

call add_base_struct('household_cert');

create
    table
        bank_card(
            user_id bigint not null,
            user_info_id bigint default null,
            code varchar(255) not null,
            country varchar(255) default null,
            bank_group integer default null,
            bank_type integer default null,
            reserve_phone varchar(255) default null,
            issue_address_details text default null
        ) default charset = utf8mb4,
        comment '银行卡';

call add_base_struct('bank_card');

create
    table
        if not exists biz_cert(
            user_id bigint not null, -- 上传人
            title varchar(255) not null, -- 公司名称
            reg_capital decimal(
                10,
                2
            ) default null, -- 注册资本
            create_date date default null, -- 成立日期
            uni_credit_code varchar(255) default null, -- 统一社会信用代码TYPE VARCHAR(127) DEFAULT NULL, -- 类型
            leader_name varchar(255) default null, -- 法定代表人
            biz_range text default null, -- 经营范围
            address_code varchar(255) default null, -- 地址编码
            address_details_id bigint default null, -- 地址详情 id
            issue_date date default null, -- 签发日期INDEX(user_id),INDEX(address_code),INDEX(address_details_id)
        ) comment '营业执照';

call add_presort_tree_struct('biz_licence');
