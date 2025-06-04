create
    table
        if not exists menu(
            platform_type varchar not null,
            pattern varchar not null,
            title varchar not null,
            doc text default null,
            require_login boolean default null
        );

select
    add_base_struct('menu');

create
    table
        if not exists menu_role(
            menu_id bigint not null,
            role_id bigint not null
        );

select
    add_base_struct('menu_role');

create
    table
        if not exists menu_permissions(
            menu_id bigint not null,
            permissions_id bigint not null
        );

select
    add_base_struct('menu_permissions');
