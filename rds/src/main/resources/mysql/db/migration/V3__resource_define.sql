CREATE
    TABLE
        IF NOT EXISTS api(
            name VARCHAR(128) DEFAULT NULL comment '名称',
            doc VARCHAR(128) DEFAULT NULL comment '描述',
            permissions_id BIGINT DEFAULT 3 comment '访问需要权限',
            api_path text comment '路径',
            api_method text comment '请求方式',
            api_protocol VARCHAR(63) DEFAULT NULL comment '请求协议',
            INDEX(permissions_id) comment '外联 权限'
        ) DEFAULT charset = utf8mb4,
        comment 'api';

CALL add_base_struct('api');

CREATE
    TABLE
        IF NOT EXISTS api_call_record(
            api_id BIGINT NOT NULL comment 'api',
            device_code text NULL comment '设备 id, 浏览器为 agent',
            req_ip VARCHAR(63) NULL comment '请求 ip',
            login_ip VARCHAR(63) NULL comment '登录 ip',
            resp_code INT NULL comment '响应码',
            resp_result_enc text comment '请求结果',
            INDEX(api_id) comment '外联 api'
        ) DEFAULT charset = utf8mb4,
        comment 'API请求记录';

CALL add_base_struct('api_call_record');

CREATE
    TABLE
        IF NOT EXISTS attachment(
            meta_name VARCHAR(127) NULL comment '原始名称',
            save_name VARCHAR(127) comment '存储后名称',
            base_url VARCHAR(255) NULL comment '基本url',
            base_uri VARCHAR(255) NULL comment '基本uri',
            url_name VARCHAR(127) NULL comment '资源路径名称',
            url_doc VARCHAR(255) comment '资源路径描述',
            url_id BIGINT NULL comment '根路径链接路径自连接id',
            att_type INT NOT NULL comment '附件类别（URL、附件）',
            SIZE BIGINT DEFAULT NULL comment '文件大小',
            mime_type VARCHAR(63) comment 'MIME TYPE',
            INDEX(url_id) comment '自连接 id',
            INDEX(meta_name) comment '原始名称经常搜索',
            INDEX(base_url) comment '根路径经常检索',
            INDEX(base_uri) comment '根路径经常检索',
            INDEX(att_type) comment '附件类型经常检索',
            INDEX(mime_type) comment '媒体类型经常检索'
        ) DEFAULT charset = utf8mb4,
        comment '文件';

CALL add_base_struct('attachment');

CREATE
    TABLE
        IF NOT EXISTS address(
            code VARCHAR(255) comment '代码',
            name VARCHAR(127) comment '名称',
            # 中国最长的地名是新疆维吾尔自治区昌吉回族自治州木垒哈萨克自治县大南沟乌孜别克族乡 year_version VARCHAR(15) NULL comment '年份版本号',
            LEVEL INT DEFAULT 0 comment '级别 0 为国家',
            center VARCHAR(255) NULL comment '定位',
            leaf BOOLEAN DEFAULT FALSE comment '是否为终结地址（如市辖区）',
            UNIQUE(code) comment '行政区代码唯一',
            INDEX(name) comment '名称经常检索'
        ) DEFAULT charset = utf8mb4,
        comment '行政区代码';

CALL add_base_struct('address');

CALL add_presort_tree_struct('address');

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
        )
    VALUES(
        0,
        0,
        '000000000000',
        '',
        1,
        2,
        0,
        NULL
    );

CREATE
    TABLE
        IF NOT EXISTS address_details(
            address_id BIGINT NOT NULL comment '地址id',
            user_id BIGINT NULL comment '用户 id',
            phone VARCHAR(127) comment '联系电话',
            name VARCHAR(255) comment '联系人名称',
            address_code VARCHAR(31) NOT NULL comment '地址代码',
            address_details text NOT NULL comment '地址详情',
            center VARCHAR(255) comment '定位',
            INDEX(address_id) comment '外联 地址',
            INDEX(user_id) comment '外联 用户',
            INDEX(address_code) comment '外联 地址代码'
        ) DEFAULT charset = utf8mb4,
        comment '地址详情';

CALL add_base_struct('address_details');

CREATE
    TABLE
        IF NOT EXISTS table_row_delete_record(
            table_names VARCHAR(127) NULL comment '表名',
            user_id BIGINT NULL comment '删除用户id',
            user_account VARCHAR(255) NULL comment '删除用户账户',
            delete_datetime datetime DEFAULT now() comment '删除时间',
            entity json NOT NULL comment '删除实体',
            INDEX(table_names) comment '表名经常查询',
            INDEX(user_account) comment '用户账户经常查询',
            INDEX(user_id) comment '外联 用户'
        ) DEFAULT charset = utf8mb4,
        comment '数据删除记录';

CALL add_base_struct('table_row_delete_record');

CREATE
    TABLE
        IF NOT EXISTS table_row_change_record(
            TYPE BOOLEAN NOT NULL comment '变更类型：插入：true，修改：false',
            table_names VARCHAR(127) NULL comment '表名',
            create_user_id BIGINT NULL comment '创建用户id',
            create_user_account CHAR(255) NULL comment '创建用户账户',
            create_datetime datetime NULL comment '创建时间',
            create_entity json NULL comment '创建实体',
            last_modify_user_id BIGINT NULL comment '最后修改用户id',
            last_modify_user_account CHAR(255) NULL comment '最后修改用户账户',
            last_modify_datetime datetime NULL comment '最后修改时间',
            last_modify_entity json NOT NULL comment '最后修改实体',
            INDEX(table_names) comment '表名经常查询',
            INDEX(create_user_account) comment '创建账户经常查询',
            INDEX(last_modify_user_account) comment '最后修改用户账户经常查询',
            INDEX(create_user_id) comment '外联 用户',
            INDEX(last_modify_user_id) comment '外联 用户'
        ) DEFAULT charset = utf8mb4,
        comment '数据变更记录';

CALL add_base_struct('table_row_change_record');
