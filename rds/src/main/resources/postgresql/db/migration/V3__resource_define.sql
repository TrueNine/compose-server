CREATE
    TABLE
        IF NOT EXISTS api(
            name VARCHAR(128) DEFAULT NULL,
            doc VARCHAR(128) DEFAULT NULL,
            permissions_id BIGINT DEFAULT 3,
            api_path text,
            api_method text,
            api_protocol VARCHAR(63) DEFAULT NULL
        );

comment ON
TABLE
    api IS 'api';

SELECT
    add_base_struct('api');

CREATE
    INDEX ON
    api(permissions_id);

CREATE
    TABLE
        IF NOT EXISTS api_call_record(
            api_id BIGINT NOT NULL,
            device_code text NULL,
            req_ip VARCHAR(63) NULL,
            login_ip VARCHAR(63) NULL,
            resp_code INT NULL,
            resp_result_enc text
        );

comment ON
TABLE
    api_call_record IS 'API请求记录';

SELECT
    add_base_struct('api_call_record');

CREATE
    INDEX ON
    api_call_record(api_id);

CREATE
    TABLE
        IF NOT EXISTS attachment(
            meta_name VARCHAR(127) DEFAULT NULL,
            save_name VARCHAR(127),
            base_url VARCHAR(255),
            base_uri VARCHAR(255),
            url_name VARCHAR(127) DEFAULT NULL,
            url_doc VARCHAR(255),
            url_id BIGINT NULL,
            att_type INT NOT NULL,
            SIZE BIGINT DEFAULT NULL,
            mime_type VARCHAR(63)
        );

comment ON
TABLE
    attachment IS '文件';

SELECT
    add_base_struct('attachment');

CREATE
    INDEX ON
    attachment(url_id);

CREATE
    INDEX ON
    attachment(meta_name);

CREATE
    INDEX ON
    attachment(base_url);

CREATE
    INDEX ON
    attachment(base_uri);

CREATE
    INDEX ON
    attachment(att_type);

CREATE
    INDEX ON
    attachment(mime_type);

CREATE
    TABLE
        IF NOT EXISTS address(
            code VARCHAR(255) NULL,
            name VARCHAR(127) NULL, -- 中国最长的地名是新疆维吾尔自治区昌吉回族自治州木垒哈萨克自治县大南沟乌孜别克族乡
            year_version VARCHAR(15) NULL,
            LEVEL INTEGER DEFAULT 0,
            center VARCHAR(255) NULL,
            leaf BOOLEAN DEFAULT FALSE,
            UNIQUE(code)
        );

comment ON
TABLE
    address IS '行政区代码';

SELECT
    add_base_struct('address');

SELECT
    add_presort_tree_struct('address');

CREATE
    INDEX ON
    address(name);

INSERT
    INTO
        address(
            id,
            LEVEL,
            code,
            name,
            rln,
            rrn,
            tgi,
            center
        ) SELECT
            *
        FROM
            (
            VALUES(
                0,
                0,
                '000000000000',
                '',
                1,
                2,
                0,
                NULL
            )
            ) AS tmp(
                id,
                LEVEL,
                code,
                name,
                rln,
                rrn,
                tgi,
                center
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    address a
                WHERE
                    a.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS address_details(
            address_id BIGINT NOT NULL,
            user_id BIGINT NULL,
            phone VARCHAR(127),
            name VARCHAR(255),
            address_code VARCHAR(31) NOT NULL,
            address_details text NOT NULL,
            center VARCHAR(255)
        );

comment ON
TABLE
    address_details IS '地址详情';

SELECT
    add_base_struct('address_details');

CREATE
    INDEX ON
    address_details(address_id);

CREATE
    INDEX ON
    address_details(user_id);

CREATE
    INDEX ON
    address_details(address_code);

CREATE
    TABLE
        IF NOT EXISTS table_row_delete_record(
            table_names VARCHAR(127) NULL,
            user_id BIGINT NULL,
            user_account VARCHAR(255) NULL,
            delete_datetime TIMESTAMP DEFAULT now(),
            entity json NOT NULL
        );

comment ON
TABLE
    table_row_delete_record IS '数据删除记录';

SELECT
    add_base_struct('table_row_delete_record');

CREATE
    INDEX ON
    table_row_delete_record(table_names);

CREATE
    INDEX ON
    table_row_delete_record(user_account);

CREATE
    INDEX ON
    table_row_delete_record(user_id);

CREATE
    TABLE
        IF NOT EXISTS table_row_change_record(
            TYPE BOOLEAN NOT NULL,
            table_names VARCHAR(127) NULL,
            create_user_id BIGINT NULL,
            create_user_account CHAR(255) NULL,
            create_datetime TIMESTAMP NULL,
            create_entity json NULL,
            last_modify_user_id BIGINT NULL,
            last_modify_user_account CHAR(255) NULL,
            last_modify_datetime TIMESTAMP NULL,
            last_modify_entity json NOT NULL
        );

comment ON
TABLE
    table_row_change_record IS '数据变更记录';

SELECT
    add_base_struct('table_row_change_record');

CREATE
    INDEX ON
    table_row_change_record(table_names);

CREATE
    INDEX ON
    table_row_change_record(create_user_account);

CREATE
    INDEX ON
    table_row_change_record(last_modify_user_account);

CREATE
    INDEX ON
    table_row_change_record(create_user_id);

CREATE
    INDEX ON
    table_row_change_record(last_modify_user_id);
