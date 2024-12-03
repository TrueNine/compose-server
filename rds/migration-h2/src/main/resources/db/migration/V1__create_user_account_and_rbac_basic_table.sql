create
    table
        if not exists user_account(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            create_user_id bigint default null,
            account varchar(255) default null,
            nick_name varchar(2047) default null,
            doc text default null,
            pwd_enc varchar(2047) default null,
            ban_time timestamp default null,
            last_login_time timestamp default now(),
            unique(account)
        );

insert
    into
        user_account(
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
                    user_account u
                where
                    u.account = tmp.account
                    and u.pwd_enc = tmp.pwd_enc
            );

create
    table
        if not exists user_info(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
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
                '2012-01-23',
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
                '2012-01-23',
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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar(255) not null,
            doc text
        );

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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar(255) not null,
            doc text
        );

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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar(255) not null,
            doc text
        );

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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            role_id bigint not null,
            permissions_id bigint not null
        );

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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            role_group_id bigint not null,
            role_id bigint not null
        );

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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint not null,
            role_group_id bigint not null
        );

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
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar(255) not null,
            doc text null
        );

create
    table
        if not exists user_dept(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            user_id bigint not null,
            dept_id bigint not null
        );
