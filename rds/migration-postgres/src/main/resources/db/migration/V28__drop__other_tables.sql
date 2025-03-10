drop
    table
        if exists biz_cert;

drop
    table
        if exists idcard_2;

drop
    table
        if exists household_cert;

drop
    table
        if exists bank_card;

select
    rm_presort_tree_struct('user_dept');

alter table
    if exists user_dept drop
        column if exists rpi;

select
    rm_presort_tree_struct('address');
