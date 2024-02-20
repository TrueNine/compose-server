CREATE
    TABLE
        IF NOT EXISTS idcard_2(
            user_id BIGINT NOT NULL,
            user_info_id BIGINT NOT NULL,
            address_details_id BIGINT DEFAULT NULL,
            name VARCHAR(255) NOT NULL,
            gender INTEGER DEFAULT NULL,
            code VARCHAR(64) NOT NULL,
            birthday DATE DEFAULT NULL,
            ethnic_group VARCHAR(127) DEFAULT NULL,
            expire_date DATE DEFAULT NULL,
            issue_organ VARCHAR(255) DEFAULT NULL
        );

comment ON
TABLE
    idcard_2 IS '身份证2代';

SELECT
    add_base_struct('idcard_2');

CREATE
    TABLE
        IF NOT EXISTS dis_cert_2(
            user_id BIGINT DEFAULT NULL,
            user_info_id BIGINT DEFAULT NULL,
            name VARCHAR(255) NOT NULL,
            gender INTEGER DEFAULT NULL,
            code VARCHAR(64) DEFAULT NULL,
            TYPE INTEGER NOT NULL,
            LEVEL INTEGER NOT NULL,
            issue_date DATE DEFAULT NULL,
            expire_time DATE DEFAULT NULL,
            address_details_id BIGINT DEFAULT NULL,
            guardian VARCHAR(255) DEFAULT NULL,
            guardian_phone VARCHAR(127) DEFAULT NULL,
            birthday DATE DEFAULT NULL
        );

comment ON
TABLE
    dis_cert_2 IS '残疾证2代';

SELECT
    add_base_struct('dis_cert_2');

CREATE
    TABLE
        IF NOT EXISTS household_cert(
            user_id BIGINT DEFAULT NULL,
            user_info_id BIGINT DEFAULT NULL,
            household_type INTEGER DEFAULT NULL,
            household_primary_name VARCHAR(255) DEFAULT NULL,
            code VARCHAR(255) DEFAULT NULL,
            address_details_id BIGINT DEFAULT NULL,
            issue_organ VARCHAR(255) DEFAULT NULL,
            name VARCHAR(255) NOT NULL,
            old_name VARCHAR(255) DEFAULT NULL,
            relationship INTEGER DEFAULT NULL,
            gender INTEGER DEFAULT NULL,
            ethnic_group VARCHAR(127) DEFAULT NULL,
            birthday DATE DEFAULT NULL,
            height DECIMAL(
                4,
                2
            ) DEFAULT NULL,
            blood_type INTEGER DEFAULT NULL,
            place_birth_address_details_id BIGINT DEFAULT NULL,
            origin_address_details_id BIGINT DEFAULT NULL,
            idcard_code VARCHAR(255) NOT NULL,
            education_level INTEGER DEFAULT NULL,
            occupation VARCHAR(255) DEFAULT NULL,
            military_service_status VARCHAR(255) DEFAULT NULL,
            service_address_details_id BIGINT DEFAULT NULL,
            issue_date DATE DEFAULT NULL
        );

comment ON
TABLE
    household_cert IS '户口登记卡';

SELECT
    add_base_struct('household_cert');

CREATE
    TABLE
        IF NOT EXISTS bank_card(
            user_id BIGINT DEFAULT NULL,
            user_info_id BIGINT DEFAULT NULL,
            code VARCHAR(255) NOT NULL,
            country VARCHAR(255) DEFAULT NULL,
            bank_group INTEGER DEFAULT NULL,
            bank_type INTEGER DEFAULT NULL,
            reserve_phone VARCHAR(255) DEFAULT NULL,
            issue_address_details text DEFAULT NULL
        );

comment ON
TABLE
    bank_card IS '银行卡';

SELECT
    add_base_struct('bank_card');

CREATE
    TABLE
        IF NOT EXISTS biz_cert(
            user_id BIGINT DEFAULT NULL, -- 上传人
            user_info_id BIGINT DEFAULT NULL, -- 用户信息 id
            title VARCHAR(255) NOT NULL, -- 公司名称
            reg_capital DECIMAL(
                10,
                2
            ) DEFAULT NULL, -- 注册资本
            create_date DATE DEFAULT NULL, -- 成立日期
            uni_credit_code VARCHAR(255) DEFAULT NULL, -- 统一社会信用代码TYPE VARCHAR(127) DEFAULT NULL, -- 类型
            leader_name VARCHAR(255) DEFAULT NULL, -- 法定代表人
            biz_range text DEFAULT NULL, -- 经营范围
            address_code VARCHAR(255) DEFAULT NULL, -- 地址编码
            address_details_id BIGINT DEFAULT NULL, -- 地址详情 id
            issue_date DATE DEFAULT NULL -- 签发日期

        );

comment ON
TABLE
    biz_cert IS '营业执照';

SELECT
    add_base_struct('biz_cert');

CREATE
    INDEX ON
    biz_cert(user_id);

CREATE
    INDEX ON
    biz_cert(address_code);

CREATE
    INDEX ON
    biz_cert(address_details_id);
