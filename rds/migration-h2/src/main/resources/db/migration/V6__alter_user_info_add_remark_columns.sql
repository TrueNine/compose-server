alter table
    if exists user_info add column if not exists remark text default null;

alter table
    if exists user_info add column if not exists remark_name varchar(127) default null;
