select
    rm_base_struct('role_permissions');

select
    all_to_nullable('role_permissions');

select
    rm_base_struct('role_group_role');

select
    all_to_nullable('role_group_role');

select
    rm_base_struct('user_role_group');

select
    all_to_nullable('user_role_group');

select
    rm_base_struct('user_dept');

select
    all_to_nullable('user_dept');

select
    rm_base_struct('menu_role');

select
    all_to_nullable('menu_role');

select
    rm_base_struct('menu_permissions');

select
    all_to_nullable('menu_permissions');

drop
    table
        if exists audit;

drop
    table
        if exists audit_attachment;

drop
    table
        if exists table_row_delete_record;

drop
    table
        if exists table_row_change_record;
