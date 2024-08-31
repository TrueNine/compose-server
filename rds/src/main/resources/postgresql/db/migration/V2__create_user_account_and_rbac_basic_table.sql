create
    table
        if not exists usr(
            create_user_id bigint default null,
            account varchar(255) default null,
            nick_name varchar(2047) default null,
            doc text default null,
            pwd_enc varchar(2047) default null,
            ban_time timestamp default null,
            last_login_time timestamp default now(),
            unique(account)
        );

comment on
table
    usr is '用户';

select
    add_base_struct('usr');

insert
    into
        usr(
            id,
            create_user_id,
            account,
            nick_name,
            pwd_enc,
            last_login_time,
            doc
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                create_user_id,
                account,
                nick_name,
                pwd_enc,
                last_login_time,
                doc
            )
        where
            not exists(
                select
                    1
                from
                    usr u
                where
                    u.account = tmp.account
                    and u.pwd_enc = tmp.pwd_enc
            );

create
    table
        if not exists user_info(
            user_id bigint default null,
            create_user_id bigint default null,
            pri boolean default true,
            avatar_img_id bigint default null,
            first_name varchar(4095) default null,
            last_name varchar(4095) default null,
            email varchar(255) default null,
            birthday timestamp default null,
            address_details_id bigint default null,
            phone varchar(255) default null,
            spare_phone varchar(255) default null,
            id_card varchar(255) default null,
            gender int default null,
            wechat_openid varchar(255) default null,
            wechat_account varchar(255) default null,
            wechat_authid varchar(255) default null,
            qq_openid varchar(255) default null,
            qq_account varchar(255) default null,
            address_code varchar(127) default null,
            address_id bigint default null
        );

comment on
table
    user_info is '用户信息';

select
    add_base_struct('user_info');

create
    index if not exists user_id_idx on
    user_info(user_id);

create
    index if not exists create_user_id_idx on
    user_info(create_user_id);

create
    index if not exists phone_idx on
    user_info(phone);

create
    index if not exists email_idx on
    user_info(email);

create
    index if not exists id_card_idx on
    user_info(id_card);

create
    index if not exists address_details_id_idx on
    user_info(address_details_id);

create
    index if not exists avatar_img_id_idx on
    user_info(avatar_img_id);

create
    index if not exists wechat_openid_idx on
    user_info(wechat_openid);

create
    index if not exists wechat_authid_idx on
    user_info(wechat_authid);

insert
    into
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
        ) select
            *
        from
            (
            values(
                0,
                0,
                true,
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
                true,
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
            ) as tmp(
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
        where
            not exists(
                select
                    1
                from
                    user_info i
                where
                    i.id = tmp.id
            );

create
    table
        if not exists role(
            name varchar(255) not null,
            doc text
        );

comment on
table
    role is '角色';

select
    add_base_struct('role');

insert
    into
        role(
            id,
            name,
            doc
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                name,
                doc
            )
        where
            not exists(
                select
                    1
                from
                    role r
                where
                    r.id = tmp.id
            );

create
    table
        if not exists permissions(
            name varchar(255) not null,
            doc text
        );

comment on
table
    permissions is '权限';

select
    add_base_struct('permissions');

insert
    into
        permissions(
            id,
            name,
            doc
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                name,
                doc
            )
        where
            not exists(
                select
                    1
                from
                    permissions p
                where
                    p.id = tmp.id
            );

create
    table
        if not exists role_group(
            name varchar(255) not null,
            doc text
        );

comment on
table
    role_group is '角色组';

select
    add_base_struct('role_group');

insert
    into
        role_group(
            id,
            name,
            doc
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                name,
                doc
            )
        where
            not exists(
                select
                    1
                from
                    role_group r
                where
                    r.id = tmp.id
            );

create
    table
        if not exists role_permissions(
            role_id bigint not null,
            permissions_id bigint not null
        );

comment on
table
    role_permissions is '角色  权限';

select
    add_base_struct('role_permissions');

create
    index if not exists role_id_idx on
    role_permissions(role_id);

create
    index if not exists permissions_id_idx on
    role_permissions(permissions_id);

insert
    into
        role_permissions(
            id,
            role_id,
            permissions_id
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                role_id,
                permissions_id
            )
        where
            not exists(
                select
                    1
                from
                    role_permissions r
                where
                    r.id = tmp.id
            );

create
    table
        if not exists role_group_role(
            role_group_id bigint not null,
            role_id bigint not null
        );

comment on
table
    role_group_role is '角色组  角色';

select
    add_base_struct('role_group_role');

create
    index if not exists role_group_id_idx on
    role_group_role(role_group_id);

create
    index if not exists role_id_idx on
    role_group_role(role_id);

insert
    into
        role_group_role(
            id,
            role_group_id,
            role_id
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                role_group_id,
                role_id
            )
        where
            not exists(
                select
                    1
                from
                    role_group_role r
                where
                    r.id = tmp.id
            );

create
    table
        if not exists user_role_group(
            user_id bigint not null,
            role_group_id bigint not null
        );

comment on
table
    user_role_group is '用户  角色组';

select
    add_base_struct('user_role_group');

create
    index if not exists role_group_id_idx on
    user_role_group(role_group_id);

create
    index if not exists user_id_idx on
    user_role_group(user_id);

insert
    into
        user_role_group(
            id,
            user_id,
            role_group_id
        ) select
            *
        from
            (
            values(
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
            ) as tmp(
                id,
                user_id,
                role_group_id
            )
        where
            not exists(
                select
                    1
                from
                    user_role_group u
                where
                    u.id = tmp.id
            );

create
    table
        if not exists dept(
            name varchar(255) not null,
            doc text null
        );

comment on
table
    dept is '部门';

select
    add_base_struct('dept');

create
    table
        if not exists user_dept(
            user_id bigint not null,
            dept_id bigint not null
        );

comment on
table
    user_dept is '用户  部门';

select
    add_base_struct('user_dept');

select
    add_presort_tree_struct('user_dept');
