CREATE
    TABLE
        IF NOT EXISTS usr(
            create_user_id BIGINT DEFAULT NULL comment '创建此账户的 user id',
            account VARCHAR(255) DEFAULT NULL comment '账号',
            nick_name VARCHAR(2047) DEFAULT NULL comment '呢称',
            doc text DEFAULT NULL comment '描述',
            pwd_enc VARCHAR(2047) DEFAULT NULL comment '密码',
            ban_time datetime DEFAULT NULL comment '被封禁结束时间',
            last_login_time datetime DEFAULT now() comment '最后请求时间',
            UNIQUE(account) comment '账号唯一'
        ) DEFAULT charset = utf8mb4,
        comment '用户';

CALL add_base_struct('usr');

INSERT
    INTO
        usr(
            id,
            create_user_id,
            account,
            nick_name,
            pwd_enc,
            last_login_time,
            doc
        )
    VALUES(
        0,
        0,
        'root',
        'ROOT',
        '$2a$14$4.QaPjTjIPILS5EnK3q3yu/OoKiuVykyLiDOIVIFy0ypbs9CL7wNi',
        now(),
        '超级管理员账号'
    ),
    (
        1,
        0,
        'usr',
        'USR',
        '$2a$14$Rfvt1A9RVEgp47pTTiT1KeKSJt14CtSJsv2iSggLTQJcgUHA5o0sa',
        now(),
        '普通用户账号'
    );

CREATE
    TABLE
        IF NOT EXISTS user_info(
            user_id BIGINT DEFAULT NULL comment '用户账号id',
            create_user_id BIGINT DEFAULT NULL comment '创建此用户信息的 user id',
            pri BOOLEAN DEFAULT TRUE comment '首选用户信息',
            avatar_img_id BIGINT comment '用户头像',
            first_name VARCHAR(4095) comment '姓',
            last_name VARCHAR(4095) comment '名',
            email VARCHAR(255) comment '邮箱',
            birthday datetime comment '生日',
            address_details_id BIGINT comment '地址',
            phone VARCHAR(255) DEFAULT NULL comment '电话号码',
            spare_phone VARCHAR(255) DEFAULT NULL comment '备用手机',
            id_card VARCHAR(255) DEFAULT NULL comment '身份证',
            gender tinyint DEFAULT NULL comment ' 性别：0女，1难，2未知',
            wechat_openid VARCHAR(255) DEFAULT NULL comment '微信个人 openId',
            wechat_account VARCHAR(255) DEFAULT NULL comment '微信个人账号',
            wechat_authid VARCHAR(127) DEFAULT NULL comment '微信自定义登录id',
            qq_openid VARCHAR(255) DEFAULT NULL,
            qq_account VARCHAR(255) DEFAULT NULL,
            address_code VARCHAR(127) DEFAULT NULL,
            address_id BIGINT DEFAULT NULL,
            INDEX(user_id),
            INDEX(create_user_id),
            INDEX(phone),
            INDEX(email),
            INDEX(id_card),
            INDEX(wechat_openid) comment '微信 openId 经常查询',
            INDEX(wechat_authid) comment '微信自定义登录id经常查询',
            INDEX(address_details_id) comment '外联 地址详情',
            INDEX(avatar_img_id) comment '外联 文件'
        ) DEFAULT charset = utf8mb4,
        comment '用户信息';

CALL add_base_struct('user_info');

INSERT
    INTO
        user_info(
            id,
            user_id,
            pri,
            first_name,
            last_name,
            email,
            birthday,
            phone,
            gender
        )
    VALUES(
        0,
        0,
        TRUE,
        'R',
        'OOT',
        'g@g.com',
        '1997-11-04',
        '13711111111',
        1
    ),
    (
        1,
        1,
        TRUE,
        'U',
        'SR',
        'g@g.com',
        '1997-11-04',
        '13722222222',
        1
    );

CREATE
    TABLE
        IF NOT EXISTS ROLE(
            name VARCHAR(255) comment '角色名称',
            doc text comment '角色描述'
        ) DEFAULT charset = utf8mb4,
        comment '角色';

CALL add_base_struct('role');

INSERT
    INTO
        ROLE(
            id,
            name,
            doc
        )
    VALUES(
        0,
        'ROOT',
        '默认 ROOT 角色，务必不要删除'
    ),
    (
        1,
        'USER',
        '默认 USER 角色，务必不要删除'
    ),
    (
        2,
        'ADMIN',
        '默认 ADMIN 角色，务必不要删除'
    );

CREATE
    TABLE
        IF NOT EXISTS permissions(
            name VARCHAR(255) comment '权限名',
            doc text comment '权限描述'
        ) DEFAULT charset = utf8mb4,
        comment '权限';

CALL add_base_struct('permissions');

INSERT
    INTO
        permissions(
            id,
            name,
            doc
        )
    VALUES(
        0,
        'ROOT',
        '默认 ROOT 权限，务必不要删除'
    ),
    (
        1,
        'USER',
        '默认 USER 权限，务必不要删除'
    ),
    (
        2,
        'ADMIN',
        '默认 ADMIN 权限，务必不要删除'
    );

CREATE
    TABLE
        IF NOT EXISTS role_group(
            name VARCHAR(255) comment '名称',
            doc text comment '描述'
        ) DEFAULT charset = utf8mb4,
        comment '角色组';

CALL add_base_struct('role_group');

INSERT
    INTO
        role_group(
            id,
            name,
            doc
        )
    VALUES(
        0,
        'ROOT',
        '默认 ROOT 角色组，务必不要删除'
    ),
    (
        1,
        'USER',
        '默认 USER 角色组，务必不要删除'
    ),
    (
        2,
        'ADMIN',
        '默认 ADMIN 角色组，务必不要删除'
    );

CREATE
    TABLE
        IF NOT EXISTS role_permissions(
            role_id BIGINT comment '角色',
            permissions_id BIGINT comment '权限',
            INDEX(role_id) comment '外联 角色',
            INDEX(permissions_id) comment '外联 权限'
        ) DEFAULT charset = utf8mb4,
        comment '角色  权限';

CALL add_base_struct('role_permissions');

INSERT
    INTO
        role_permissions(
            id,
            role_id,
            permissions_id
        )
    VALUES(
        0,
        0,
        0
    ),
    (
        1,
        0,
        1
    ),
    (
        2,
        0,
        2
    ),
    (
        3,
        1,
        1
    ),
    (
        4,
        2,
        1
    ),
    (
        5,
        2,
        2
    );

CREATE
    TABLE
        IF NOT EXISTS role_group_role(
            role_group_id BIGINT comment '用户组',
            role_id BIGINT comment '角色',
            INDEX(role_group_id) comment '外联 角色组',
            INDEX(role_id) comment '外联 角色'
        ) DEFAULT charset = utf8mb4,
        comment '角色组  角色';

CALL add_base_struct('role_group_role');

INSERT
    INTO
        role_group_role(
            id,
            role_group_id,
            role_id
        )
    VALUES(
        0,
        0,
        0
    ),
    (
        1,
        0,
        1
    ),
    (
        2,
        0,
        2
    ),
    (
        3,
        1,
        1
    ),
    (
        4,
        2,
        1
    ),
    (
        5,
        2,
        2
    );

CREATE
    TABLE
        IF NOT EXISTS user_role_group(
            user_id BIGINT comment '用户',
            role_group_id BIGINT comment '权限组',
            INDEX(user_id) comment '外联 用户',
            INDEX(role_group_id) comment '外联 角色组'
        ) DEFAULT charset = utf8mb4,
        comment '用户  角色组';

CALL add_base_struct('user_role_group');

INSERT
    INTO
        user_role_group(
            id,
            user_id,
            role_group_id
        )
    VALUES(
        0,
        0,
        0
    ),
    (
        1,
        0,
        1
    ),
    (
        2,
        0,
        2
    ),
    (
        3,
        1,
        1
    );

CREATE
    TABLE
        IF NOT EXISTS dept(
            name VARCHAR(255) NOT NULL comment '名称',
            doc text NULL comment '描述'
        ) DEFAULT charset = utf8mb4,
        comment '部门';

CALL add_base_struct('dept');

CREATE
    TABLE
        IF NOT EXISTS user_dept(
            user_id BIGINT NOT NULL comment '用户 id',
            dept_id BIGINT NOT NULL comment '部门 id'
        ) DEFAULT charset = utf8mb4,
        comment '用户  部门';

CALL add_base_struct('user_dept');

CALL add_presort_tree_struct('user_dept');
