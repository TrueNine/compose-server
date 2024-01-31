create table if not exists api
(
  name           varchar(128) default null,
  doc            varchar(128) default null,
  permissions_id bigint       default 3,
  api_path       text,
  api_method     text,
  api_protocol   varchar(63)  default null
);
comment on table api is 'api';
select add_base_struct('api');
create index on api (permissions_id);


create table if not exists api_call_record
(
  api_id          bigint      not null,
  device_code     text        null,
  req_ip          varchar(63) null,
  login_ip        varchar(63) null,
  resp_code       int         null,
  resp_result_enc text
);
comment on table api_call_record is 'API请求记录';
select add_base_struct('api_call_record');
create index on api_call_record (api_id);


create table if not exists attachment
(
  meta_name varchar(127) default null,
  save_name varchar(127),
  base_url  varchar(255),
  base_uri  varchar(255),
  url_name  varchar(127) default null,
  url_doc   varchar(255),
  url_id    bigint null,
  att_type  int    not null,
  size      bigint       default null,
  mime_type varchar(63)
);
comment on table attachment is '文件';
select add_base_struct('attachment');
create index on attachment (url_id);
create index on attachment (meta_name);
create index on attachment (base_url);
create index on attachment (base_uri);
create index on attachment (att_type);
create index on attachment (mime_type);


create table if not exists address
(
  code         varchar(255) null,
  name         varchar(127) null, -- 中国最长的地名是新疆维吾尔自治区昌吉回族自治州木垒哈萨克自治县大南沟乌孜别克族乡
  year_version varchar(15)  null,
  level        integer default 0,
  center       varchar(255) null,
  leaf         boolean default false,
  unique (code)
);
comment on table address is '行政区代码';
select add_base_struct('address');
select add_presort_tree_struct('address');
create index on address (name);

insert into address(id, level, code, name, rln, rrn, tgi, center)
select *
from (values (0, 0, '000000000000', '', 1, 2, 0, null))
       as tmp(id, level, code, name, rln, rrn, tgi, center)
where not exists(select 1
                 from address a
                 where a.id = tmp.id);


create table if not exists address_details
(
  address_id      bigint      not null,
  user_id         bigint      null,
  phone           varchar(127),
  name            varchar(255),
  address_code    varchar(31) not null,
  address_details text        not null,
  center          varchar(255)
);
comment on table address_details is '地址详情';
select add_base_struct('address_details');
create index on address_details (address_id);
create index on address_details (user_id);
create index on address_details (address_code);


create table if not exists table_row_delete_record
(
  table_names     varchar(127) null,
  user_id         bigint       null,
  user_account    varchar(255) null,
  delete_datetime timestamp default now(),
  entity          json         not null
);
comment on table table_row_delete_record is '数据删除记录';
select add_base_struct('table_row_delete_record');
create index on table_row_delete_record (table_names);
create index on table_row_delete_record (user_account);
create index on table_row_delete_record (user_id);


create table if not exists table_row_change_record
(
  type                     boolean      not null,
  table_names              varchar(127) null,
  create_user_id           bigint       null,
  create_user_account      char(255)    null,
  create_datetime          timestamp    null,
  create_entity            json         null,
  last_modify_user_id      bigint       null,
  last_modify_user_account char(255)    null,
  last_modify_datetime     timestamp    null,
  last_modify_entity       json         not null
);
comment on table table_row_change_record is '数据变更记录';
select add_base_struct('table_row_change_record');
create index on table_row_change_record (table_names);
create index on table_row_change_record (create_user_account);
create index on table_row_change_record (last_modify_user_account);
create index on table_row_change_record (create_user_id);
create index on table_row_change_record (last_modify_user_id);
