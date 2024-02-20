CREATE
    TABLE
        IF NOT EXISTS usr(
            create_user_id BIGINT DEFAULT NULL, -- 创建此账号的 user id
            account VARCHAR(255) DEFAULT NULL,
            nick_name VARCHAR(2047) DEFAULT NULL,
            doc text DEFAULT NULL,
            pwd_enc VARCHAR(2047) DEFAULT NULL,
            ban_time TIMESTAMP DEFAULT NULL,
            last_login_time TIMESTAMP DEFAULT now(),
            UNIQUE(account)
        );

comment ON
TABLE
    usr IS '用户';

SELECT
    add_base_struct('usr');

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
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                create_user_id,
                account,
                nick_name,
                pwd_enc,
                last_login_time,
                doc
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    usr u
                WHERE
                    u.account = tmp.account
                    AND u.pwd_enc = tmp.pwd_enc
            );

CREATE
    TABLE
        IF NOT EXISTS user_info(
            user_id BIGINT DEFAULT NULL,
            create_user_id BIGINT DEFAULT NULL,
            pri BOOLEAN DEFAULT TRUE, -- 首选用户信息
            avatar_img_id BIGINT DEFAULT NULL,
            first_name VARCHAR(4095) DEFAULT NULL,
            last_name VARCHAR(4095) DEFAULT NULL,
            email VARCHAR(255) DEFAULT NULL,
            birthday TIMESTAMP DEFAULT NULL,
            address_details_id BIGINT DEFAULT NULL,
            phone VARCHAR(255) DEFAULT NULL,
            spare_phone VARCHAR(255) DEFAULT NULL,
            id_card VARCHAR(255) DEFAULT NULL,
            gender INT DEFAULT NULL,
            wechat_openid VARCHAR(255) DEFAULT NULL,
            wechat_account VARCHAR(255) DEFAULT NULL,
            wechat_authid VARCHAR(255) DEFAULT NULL,
            qq_openid VARCHAR(255) DEFAULT NULL,
            qq_account VARCHAR(255) DEFAULT NULL,
            address_code VARCHAR(127) DEFAULT NULL,
            address_id BIGINT DEFAULT NULL
        );

comment ON
TABLE
    user_info IS '用户信息';

SELECT
    add_base_struct('user_info');

CREATE
    INDEX ON
    user_info(user_id);

CREATE
    INDEX ON
    user_info(create_user_id);

CREATE
    INDEX ON
    user_info(phone);

CREATE
    INDEX ON
    user_info(email);

CREATE
    INDEX ON
    user_info(id_card);

CREATE
    INDEX ON
    user_info(address_details_id);

CREATE
    INDEX ON
    user_info(avatar_img_id);

CREATE
    INDEX ON
    user_info(wechat_openid);

CREATE
    INDEX ON
    user_info(wechat_authid);

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
        ) SELECT
            *
        FROM
            (
            VALUES(
                0,
                0,
                TRUE,
                'R',
                'OOT',
                'g@g.com',
                to_timestamp(
                    '1997-11-04',
                    'YYYY-MM-DD'
                ),
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
                to_timestamp(
                    '1997-11-04',
                    'YYYY-MM-DD'
                ),
                '13722222222',
                1
            )
            ) AS tmp(
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
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    user_info i
                WHERE
                    i.user_id = tmp.user_id
                    AND i.pri = tmp.pri
            );

CREATE
    TABLE
        IF NOT EXISTS ROLE(
            name VARCHAR(255) NOT NULL,
            doc text
        );

comment ON
TABLE
    ROLE IS '角色';

SELECT
    add_base_struct('role');

INSERT
    INTO
        ROLE(
            id,
            name,
            doc
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                name,
                doc
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    ROLE r
                WHERE
                    r.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS permissions(
            name VARCHAR(255) NOT NULL,
            doc text
        );

comment ON
TABLE
    permissions IS '权限';

SELECT
    add_base_struct('permissions');

INSERT
    INTO
        permissions(
            id,
            name,
            doc
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                name,
                doc
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    permissions p
                WHERE
                    p.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS role_group(
            name VARCHAR(255) NOT NULL,
            doc text
        );

comment ON
TABLE
    role_group IS '角色组';

SELECT
    add_base_struct('role_group');

INSERT
    INTO
        role_group(
            id,
            name,
            doc
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                name,
                doc
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    role_group r
                WHERE
                    r.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS role_permissions(
            role_id BIGINT NOT NULL,
            permissions_id BIGINT NOT NULL
        );

comment ON
TABLE
    role_permissions IS '角色  权限';

SELECT
    add_base_struct('role_permissions');

CREATE
    INDEX ON
    role_permissions(role_id);

CREATE
    INDEX ON
    role_permissions(permissions_id);

INSERT
    INTO
        role_permissions(
            id,
            role_id,
            permissions_id
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                role_id,
                permissions_id
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    role_permissions r
                WHERE
                    r.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS role_group_role(
            role_group_id BIGINT NOT NULL,
            role_id BIGINT NOT NULL
        );

comment ON
TABLE
    role_group_role IS '角色组  角色';

SELECT
    add_base_struct('role_group_role');

CREATE
    INDEX ON
    role_group_role(role_group_id);

CREATE
    INDEX ON
    role_group_role(role_id);

INSERT
    INTO
        role_group_role(
            id,
            role_group_id,
            role_id
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                role_group_id,
                role_id
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    role_group_role r
                WHERE
                    r.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS user_role_group(
            user_id BIGINT NOT NULL,
            role_group_id BIGINT NOT NULL
        );

comment ON
TABLE
    user_role_group IS '用户  角色组';

SELECT
    add_base_struct('user_role_group');

CREATE
    INDEX ON
    user_role_group(role_group_id);

CREATE
    INDEX ON
    user_role_group(user_id);

INSERT
    INTO
        user_role_group(
            id,
            user_id,
            role_group_id
        ) SELECT
            *
        FROM
            (
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
            )
            ) AS tmp(
                id,
                user_id,
                role_group_id
            )
        WHERE
            NOT EXISTS(
                SELECT
                    1
                FROM
                    user_role_group u
                WHERE
                    u.id = tmp.id
            );

CREATE
    TABLE
        IF NOT EXISTS dept(
            name VARCHAR(255) NOT NULL,
            doc text NULL
        );

comment ON
TABLE
    dept IS '部门';

SELECT
    add_base_struct('dept');

CREATE
    TABLE
        IF NOT EXISTS user_dept(
            user_id BIGINT NOT NULL,
            dept_id BIGINT NOT NULL
        );

comment ON
TABLE
    user_dept IS '用户  部门';

SELECT
    add_base_struct('user_dept');

SELECT
    add_presort_tree_struct('user_dept');
